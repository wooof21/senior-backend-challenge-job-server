package com.exmple.jobserver.adapters.web;

import com.exmple.jobserver.adapters.web.dto.JobRequest;
import com.exmple.jobserver.adapters.web.dto.JobResponse;
import com.exmple.jobserver.application.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService service;

    @PostMapping
    public Mono<JobResponse> submit(@Valid @RequestBody JobRequest request) {
        return service.submitJob(request)
                .map(JobResponse::success);
    }

    @GetMapping("/{id}")
    public Mono<JobResponse> searchJob(@PathVariable String id) {
        return service.searchJob(id)
                .map(JobResponse::success);
    }
}
