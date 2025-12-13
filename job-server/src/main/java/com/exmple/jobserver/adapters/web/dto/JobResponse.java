package com.exmple.jobserver.adapters.web.dto;

import com.exmple.jobserver.domian.model.Job;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {

    private String status;
    private Job data;
    private String error;

    public static JobResponse success(Job job) {
        return JobResponse.builder()
                .status("SUCCESS")
                .data(job)
                .error(null)
                .build();
    }

    public static JobResponse fail(String error) {
        return JobResponse.builder()
                .status("ERROR")
                .data(null)
                .error(error)
                .build();
    }
}
