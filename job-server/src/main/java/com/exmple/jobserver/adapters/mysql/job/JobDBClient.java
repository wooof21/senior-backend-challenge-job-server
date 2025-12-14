package com.exmple.jobserver.adapters.mysql.job;

import com.exmple.jobserver.domian.exceptions.JobNotFoundException;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.ports.internal.JobContract;
import com.exmple.jobserver.utils.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JobDBClient implements JobContract {

    private final JobRepo repo;

    @Override
    public Mono<Job> saveJob(Job job) {
        return repo.save(EntityMapper.fromJob(job))
                .map(EntityMapper::fromJobEntity);
    }

    @Override
    public Mono<Job> findJobById(String jobId) {
        return repo.findById(jobId).map(EntityMapper::fromJobEntity);
    }

    @Override
    public Mono<Job> updateJob(Job job) {
        return repo.findById(job.getJobId())
                .switchIfEmpty(Mono.error(new JobNotFoundException(job.getJobId())))
                .flatMap(curr -> {
                    curr.setStatus(job.getStatus().name());
                    curr.setValue(job.getValue());
                    curr.setError(job.getError());
                    if (job.getProject() != null) {
                        curr.setProjectId(job.getProject().getProjectId());
                    }
                    return repo.save(curr);
                })
                .map(EntityMapper::fromJobEntity);
    }

    @Override
    public Mono<Boolean> existById(String jobId) {
        return repo.existsById(jobId);
    }
}
