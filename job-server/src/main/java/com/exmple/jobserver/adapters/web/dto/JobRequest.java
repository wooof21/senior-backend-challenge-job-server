package com.exmple.jobserver.adapters.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobRequest {

    @NotNull(message = "Missing Job ID")
    private String jobId;

    @NotNull(message = "Missing User ID")
    private String userId;

    private String projectId;
}
