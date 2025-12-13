package com.exmple.jobserver.domian.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Job {

    private String jobId;
    private JobStatus status;
    private Integer value;
    private String error;

    private User user;
    private Project project;
}
