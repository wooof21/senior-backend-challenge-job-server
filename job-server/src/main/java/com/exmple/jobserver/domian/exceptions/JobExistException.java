package com.exmple.jobserver.domian.exceptions;

public class JobExistException extends RuntimeException {

    public JobExistException(String jobId) {
        super(String.format("Job Id - %s Already Processed", jobId));
    }
}
