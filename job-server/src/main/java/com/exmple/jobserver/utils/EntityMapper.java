package com.exmple.jobserver.utils;

import com.exmple.jobserver.adapters.mysql.job.JobEntity;
import com.exmple.jobserver.adapters.mysql.project.ProjectEntity;
import com.exmple.jobserver.adapters.mysql.user.UserEntity;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.domian.model.JobStatus;
import com.exmple.jobserver.domian.model.Project;
import com.exmple.jobserver.domian.model.User;

public class EntityMapper {

    public static JobEntity fromJob(Job job) {
        return JobEntity.builder()
                .jobId(job.getJobId())
                .status(job.getStatus().name())
                .value(job.getValue())
                .error(job.getError())
                .userId(job.getUser().getUserId())
                .projectId(job.getProject() != null ? job.getProject().getProjectId() : null)
                .build();
    }

    public static Job fromJobEntity(JobEntity entity) {
        return Job.builder()
                .jobId(entity.getJobId())
                .status(JobStatus.valueOf(entity.getStatus()))
                .value(entity.getValue())
                .error(entity.getError())
                .user(User.builder()
                        .userId(entity.getUserId())
                        .build())
                .project(entity.getProjectId() != null ?
                        Project.builder()
                                .projectId(entity.getProjectId())
                                .build() : null)
                .build();
    }

    public static UserEntity fromUser(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .build();
    }

    public static User fromUserEntity(UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .build();
    }

    public static ProjectEntity fromProject(Project project) {
        return ProjectEntity.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .build();
    }

    public static Project fromProjectEntity(ProjectEntity entity) {
        return Project.builder()
                .projectId(entity.getProjectId())
                .projectName(entity.getProjectName())
                .build();
    }
}
