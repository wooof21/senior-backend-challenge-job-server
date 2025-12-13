package com.exmple.jobserver.adapters.mysql.project;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("projects")
@Data
@Builder
public class ProjectEntity {

    @Id
    @Column("project_id")
    private String projectId;

    @Column("project_name")
    private String projectName;
}
