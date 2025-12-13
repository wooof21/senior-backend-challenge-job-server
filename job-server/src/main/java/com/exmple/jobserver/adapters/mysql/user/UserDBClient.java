package com.exmple.jobserver.adapters.mysql.user;

import com.exmple.jobserver.domian.model.User;
import com.exmple.jobserver.ports.internal.UserContract;
import com.exmple.jobserver.utils.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDBClient implements UserContract {

    private final UserRepo repo;

    @Override
    public Mono<User> findUserById(String userId) {
        return repo.findById(userId)
                .map(EntityMapper::fromUserEntity);
    }
}
