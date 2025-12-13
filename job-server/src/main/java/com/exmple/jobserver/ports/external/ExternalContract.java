package com.exmple.jobserver.ports.external;

import com.exmple.jobserver.domian.model.Job;
import reactor.core.publisher.Mono;

public interface ExternalContract {

    Mono<Job> process(Job job);
}
