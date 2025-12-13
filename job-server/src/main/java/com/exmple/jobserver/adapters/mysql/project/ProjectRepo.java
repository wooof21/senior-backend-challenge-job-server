package com.exmple.jobserver.adapters.mysql.project;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends R2dbcRepository<ProjectEntity, String> {
}
