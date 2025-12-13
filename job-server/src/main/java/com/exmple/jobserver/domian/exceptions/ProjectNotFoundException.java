package com.exmple.jobserver.domian.exceptions;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String projectId) {
        super(String.format("Project Id - %s Not Found", projectId));
    }
}
