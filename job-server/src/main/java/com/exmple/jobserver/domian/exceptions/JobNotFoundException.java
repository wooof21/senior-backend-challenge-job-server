package com.exmple.jobserver.domian.exceptions;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String jobId) {
        super(String.format("Job Id - %s Not Found", jobId));
    }
}
