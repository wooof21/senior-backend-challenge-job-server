package com.exmple.jobserver.ports.internal;

import com.exmple.jobserver.domian.model.User;
import reactor.core.publisher.Mono;

public interface UserContract {

    Mono<User> findUserById(String userId);
}
