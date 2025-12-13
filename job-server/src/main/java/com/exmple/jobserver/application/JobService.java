package com.exmple.jobserver.application;

import com.exmple.jobserver.adapters.web.dto.JobRequest;
import com.exmple.jobserver.domian.exceptions.ExternalServiceException;
import com.exmple.jobserver.domian.exceptions.JobNotFoundException;
import com.exmple.jobserver.domian.exceptions.ProjectNotFoundException;
import com.exmple.jobserver.domian.exceptions.UserNotFoundException;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.domian.model.JobStatus;
import com.exmple.jobserver.domian.model.Project;
import com.exmple.jobserver.domian.model.User;
import com.exmple.jobserver.ports.external.ExternalContract;
import com.exmple.jobserver.ports.internal.JobContract;
import com.exmple.jobserver.ports.internal.ProjectContract;
import com.exmple.jobserver.ports.internal.UserContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobContract jobContract;
    private final ExternalContract externalContract;
    private final ExecutorService vtExecutor;
    private final UserContract userContract;
    private final ProjectContract projectContract;


    public Mono<Job> submitJob(JobRequest request) {

        Job job = Job.builder()
                .jobId(request.getJobId())
                .status(JobStatus.PENDING)
                .build();

        return jobContract.saveJob(job)
                .doOnSuccess(saved ->
                        vtExecutor.submit(() -> processJobAsync(saved, request)));
    }

    private void processJobAsync(Job job, JobRequest request) {
        Mono.zip(getUserFromDB(request.getUserId()),
                        getProjectFromDB(request.getProjectId()))
                .flatMap(tuple -> {
                    Optional<Project> projectOpt = tuple.getT2();

                    Job inProgressJob = Job.builder()
                            .jobId(job.getJobId())
                            .status(JobStatus.PROCESSING)
                            .user(tuple.getT1())
                            .project(projectOpt.orElse(null))
                            .build();

                    return jobContract.updateJob(inProgressJob);
                })
                .flatMap(externalContract::process)
                .flatMap(jobContract::updateJob)
                .onErrorResume(ExternalServiceException.class, err ->
                        jobContract.updateJob(
                                Job.builder()
                                        .jobId(job.getJobId())
                                        .status(JobStatus.FAILED)
                                        .error(err.getMessage())
                                        .user(job.getUser())
                                        .project(job.getProject())
                                        .build()
                        ))
                .subscribe();
    }

    public Mono<Job> searchJob(String jobId) {
        return jobContract.findJobById(jobId)
                .switchIfEmpty(Mono.error(new JobNotFoundException(jobId)))
                .flatMap(job -> {
                    String userId = job.getUser().getUserId();
                    String projectId = Optional.ofNullable(job.getProject())
                                            .map(Project::getProjectId).orElse(null);
                    return Mono.zip(
                            getUserFromDB(userId),
                            getProjectFromDB(projectId)
                    ).map(tuple -> {
                        Optional<Project> projectOpt = tuple.getT2();
                        job.setUser(tuple.getT1());
                        job.setProject(projectOpt.orElse(null));
                        return job;
                    });
                });
    }

    private Mono<User> getUserFromDB(String userId) {
        return userContract.findUserById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)));
    }

    private Mono<Optional<Project>> getProjectFromDB(String projectId) {
        return Mono.justOrEmpty(projectId)
                .flatMap(pid ->
                        projectContract.findProjectById(pid)
                                .switchIfEmpty(Mono.error(
                                        new ProjectNotFoundException(pid)
                                ))
                )
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty());
    }
}
