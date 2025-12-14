package com.exmple.jobserver.adapters.external;

import com.exmple.jobserver.adapters.web.dto.ExternalJobResponse;
import com.exmple.jobserver.domian.exceptions.ExternalServiceException;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.domian.model.JobStatus;
import com.exmple.jobserver.ports.external.ExternalContract;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class MockServiceClient implements ExternalContract {

    private final WebClient client;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public MockServiceClient(WebClient webClient,
                             RetryRegistry retryRegistry,
                             CircuitBreakerRegistry cbRegistry) {
        this.client = webClient;
        this.retry = retryRegistry.retry("externalService");
        this.circuitBreaker = cbRegistry.circuitBreaker("externalService");
    }

    @PostConstruct
    public void logCbConfig() {
        log.error(String.valueOf(circuitBreaker.getCircuitBreakerConfig()));
    }

    @Override
    public Mono<Job> process(Job job) {
        return this.client.post()
                .uri("/process")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("jobId", job.getJobId()))
                .retrieve()
                .bodyToMono(ExternalJobResponse.class)
                //  Circuit breaker
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                // Retry
                .transformDeferred(RetryOperator.of(retry))
                .map(res ->
                        Job.builder()
                                .jobId(res.getJobId())
                                .status(mapStatus(res.getStatus()))
                                .value(res.getValue())
                                .error(null)
                                .build());
    }

    private JobStatus mapStatus(String externalStatus) {
        return "DONE".equalsIgnoreCase(externalStatus) ?
                JobStatus.COMPLETED : JobStatus.FAILED;
    }
}
