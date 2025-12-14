package com.exmple.jobserver.adapters.web;

import com.exmple.jobserver.adapters.web.dto.JobRequest;
import com.exmple.jobserver.adapters.web.dto.JobResponse;
import com.exmple.jobserver.application.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job submission and tracking")
public class JobController {

    private final JobService service;

    @Operation(summary = "Submit a new job")
    @PostMapping
    public Mono<JobResponse> submit(@Valid @RequestBody JobRequest request) {
        return service.submitJob(request)
                .map(JobResponse::success);
    }

    @Operation(summary = "Query job by ID")
    @GetMapping("/{id}")
    public Mono<JobResponse> searchJob(@PathVariable String id) {
        return service.searchJob(id)
                .map(JobResponse::success);
    }
}
