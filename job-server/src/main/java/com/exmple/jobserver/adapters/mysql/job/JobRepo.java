package com.exmple.jobserver.adapters.mysql.job;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepo extends R2dbcRepository<JobEntity, String> {
}
