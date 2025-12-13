package com.exmple.jobserver.adapters.mysql.project;

import com.exmple.jobserver.domian.model.Project;
import com.exmple.jobserver.ports.internal.ProjectContract;
import com.exmple.jobserver.utils.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProjectDBClient implements ProjectContract {

    private final ProjectRepo repo;

    @Override
    public Mono<Project> findProjectById(String projectId) {
        return repo.findById(projectId)
                .map(EntityMapper::fromProjectEntity);
    }
}
