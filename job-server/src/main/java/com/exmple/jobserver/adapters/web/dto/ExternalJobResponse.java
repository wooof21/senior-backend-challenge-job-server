package com.exmple.jobserver.adapters.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalJobResponse {

    private String jobId;
    private String status;
    private Integer value;
}
