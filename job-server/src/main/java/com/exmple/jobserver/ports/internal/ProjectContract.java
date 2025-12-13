package com.exmple.jobserver.ports.internal;

import com.exmple.jobserver.domian.model.Project;
import reactor.core.publisher.Mono;

public interface ProjectContract {

    Mono<Project> findProjectById(String projectId);
}
