package com.exmple.jobserver.domian.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super(String.format("User Id - %s Not Found", userId));
    }
}
