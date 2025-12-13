package com.exmple.jobserver.ports.internal;

import com.exmple.jobserver.domian.model.Job;
import reactor.core.publisher.Mono;

public interface JobContract {

    Mono<Job> saveJob(Job job);
    Mono<Job> findJobById(String jobId);
    Mono<Job> updateJob(Job job);
}
