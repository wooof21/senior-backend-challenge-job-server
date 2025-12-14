package com.exmple.jobserver.domian.exceptions;

import com.exmple.jobserver.adapters.web.dto.JobResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<JobResponse> handleValidationError(WebExchangeBindException ex) {

        String errorMessage = ex.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return Mono.just(JobResponse.fail(errorMessage));
    }

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

    @ExceptionHandler(JobExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<JobResponse> handleJobExist(JobExistException ex) {
        return Mono.just(JobResponse.fail(ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<JobResponse> handleGeneric(Exception ex) {
        return Mono.just(JobResponse.fail("Internal Server Error"));
    }
}
