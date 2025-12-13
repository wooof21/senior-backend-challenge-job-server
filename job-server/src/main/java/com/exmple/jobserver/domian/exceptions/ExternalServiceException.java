package com.exmple.jobserver.domian.exceptions;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String msg) {
        super("External Service Call Failed");
    }
}
