package com.exmple.jobserver.adapters.mysql.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends R2dbcRepository<UserEntity, String> {
}
