package com.exmple.jobserver.application;

import com.exmple.jobserver.adapters.web.dto.JobRequest;
import com.exmple.jobserver.domian.exceptions.*;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.domian.model.JobStatus;
import com.exmple.jobserver.domian.model.Project;
import com.exmple.jobserver.domian.model.User;
import com.exmple.jobserver.ports.external.ExternalContract;
import com.exmple.jobserver.ports.internal.JobContract;
import com.exmple.jobserver.ports.internal.ProjectContract;
import com.exmple.jobserver.ports.internal.UserContract;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobContract jobContract;
    private final ExternalContract externalContract;
    private final ExecutorService vtExecutor;
    private final UserContract userContract;
    private final ProjectContract projectContract;


    public Mono<Job> submitJob(JobRequest request) {

        return jobContract.existById(request.getJobId())
                .flatMap(exist -> {
                    if (exist) {
                        return Mono.error(new JobExistException(request.getJobId()));
                    }

                    // 🔥 VALIDATE USER + PROJECT FIRST
                    return Mono.zip(
                            getUserFromDB(request.getUserId()),
                            getProjectFromDB(request.getProjectId())
                    );
                }).flatMap(tuple -> {
                    User user = tuple.getT1();
                    Optional<Project> projectOpt = tuple.getT2();

                    Job job = Job.builder()
                            .jobId(request.getJobId())
                            .status(JobStatus.PENDING)
                            .user(user)
                            .project(projectOpt.orElse(null))
                            .build();

                    return jobContract.saveJob(job)
                            .doOnSuccess(saved ->
                                    vtExecutor.submit(() -> processJobAsync(saved, request)));
                });
    }

    private void processJobAsync(Job job, JobRequest request) {
        Job inProgressJob = Job.builder()
                .jobId(job.getJobId())
                .status(JobStatus.PROCESSING)
                .user(job.getUser())
                .project(job.getProject())
                .build();

        jobContract.updateJob(inProgressJob)
                .flatMap(externalContract::process)
                .flatMap(jobContract::updateJob)
                .onErrorResume(ex -> handleException(ex, inProgressJob))
                .subscribe();
    }

    private Mono<Job> handleException(Throwable ex, Job job) {

        // 🔥 Unwrap Reactor / async wrappers
        Throwable cause = Exceptions.unwrap(ex);
        log.error("Job {} failed with exception:", job.getJobId(), cause);


        // Circuit breaker is OPEN
        if(cause instanceof CallNotPermittedException) {
            return markFailed(job,
                    "External Service Unavailable (Circuit Breaker Open)");
        }
        // HTTP response timeout
        if(cause instanceof ReadTimeoutException) {
            return markFailed(job,
                    "External Service Response Timeout");
        }
        // TCP connection timeout
        if(cause instanceof ConnectTimeoutException) {
            return markFailed(job,
                    "External Service Connection Timeout");
        }
        // Generic timeout
        if(cause instanceof TimeoutException) {
            return markFailed(job, "External Service Timeout");
        }
        // External service returned error response (4xx/5xx)
        if(cause instanceof ExternalServiceException) {
            return markFailed(job, ex.getMessage());
        }

        if (cause instanceof WebClientResponseException wcre) {
            return markFailed(job,
                    "External Service Error: HTTP " + wcre.getStatusCode());
        }

        // Catch-all other exceptions
        return markFailed(job,
                "Unexpected Processing Error");
    }

    private Mono<Job> markFailed(Job job, String errorMsg) {
        return jobContract.updateJob(
                Job.builder()
                        .jobId(job.getJobId())
                        .status(JobStatus.FAILED)
                        .error(errorMsg)
                        .user(job.getUser())
                        .project(job.getProject())
                        .build()
        );
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
