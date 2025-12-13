package com.exmple.jobserver.domian.exceptions;

import com.exmple.jobserver.adapters.web.dto.JobResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<JobResponse> handleUserNotFound(UserNotFoundException ex) {
        return Mono.just(JobResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<JobResponse> handleProjectNotFound(ProjectNotFoundException ex) {
        return Mono.just(JobResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<JobResponse> handleJobNotFound(JobNotFoundException ex) {
        return Mono.just(JobResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<JobResponse> handleGeneric(Exception ex) {
        return Mono.just(JobResponse.fail("Internal Server Error"));
    }
}
