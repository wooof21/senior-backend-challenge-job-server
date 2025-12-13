package com.exmple.jobserver.adapters.mysql.job;


import com.exmple.jobserver.domian.model.JobStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table("jobs")
public class JobEntity implements Persistable<String> {

    @Id
    @Column("job_id")
    private String jobId;

    private String status;

    @Column("value_result")
    private Integer value;

    private String error;

    @Column("created_at")
    @ReadOnlyProperty
    private LocalDateTime createdAt;

    @Column("updated_at")
    @ReadOnlyProperty
    private LocalDateTime updatedAt;

    @Column("user_id")
    private String userId;

    @Column("project_id")
    private String projectId;

    //implement Persistable
    //tell Spring to insert even id is not null
    @Override
    public String getId() {
        return jobId;
    }

    @Override
    public boolean isNew() {
        return JobStatus.PENDING.name().equalsIgnoreCase(status);
    }
}
