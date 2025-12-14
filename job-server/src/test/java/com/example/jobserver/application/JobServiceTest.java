package com.example.jobserver.application;

import com.exmple.jobserver.adapters.web.dto.JobRequest;
import com.exmple.jobserver.application.JobService;
import com.exmple.jobserver.domian.model.Job;
import com.exmple.jobserver.domian.model.JobStatus;
import com.exmple.jobserver.domian.model.Project;
import com.exmple.jobserver.domian.model.User;
import com.exmple.jobserver.ports.external.ExternalContract;
import com.exmple.jobserver.ports.internal.JobContract;
import com.exmple.jobserver.ports.internal.ProjectContract;
import com.exmple.jobserver.ports.internal.UserContract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    JobContract jobContract;

    @Mock
    ExternalContract externalContract;

    @Mock
    UserContract userContract;

    @Mock
    ProjectContract projectContract;

    @Mock
    ExecutorService vtExecutor;

    @InjectMocks
    JobService jobService;

    @Test
    void submitJob_happyPath() {
        JobRequest request = JobRequest.builder()
                .jobId("job-1")
                .userId("user-1")
                .projectId("proj-1")
                .build();

        // ---- mocks ----
        when(jobContract.existById("job-1"))
                .thenReturn(Mono.just(false));

        when(userContract.findUserById("user-1"))
                .thenReturn(Mono.just(User.builder().userId("user-1").build()));

        when(projectContract.findProjectById("proj-1"))
                .thenReturn(Mono.just(Project.builder().projectId("proj-1").build()));

        when(jobContract.saveJob(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // 🔑 IMPORTANT: stub async execution
        doReturn(null)
                .when(vtExecutor)
                .submit(any(Runnable.class));

        // ---- test ----
        StepVerifier.create(jobService.submitJob(request))
                .assertNext(job -> {
                    assertEquals("job-1", job.getJobId());
                    assertEquals(JobStatus.PENDING, job.getStatus());
                    assertEquals("user-1", job.getUser().getUserId());
                    assertEquals("proj-1", job.getProject().getProjectId());
                })
                .verifyComplete();

        // ---- verify ----
        verify(jobContract).saveJob(any());
        verify(vtExecutor).submit(any(Runnable.class));
    }

    @Test
    void searchJob_happyPath() {
        Job job = Job.builder()
                .jobId("job-1")
                .status(JobStatus.COMPLETED)
                .user(User.builder().userId("user-1").build())
                .project(Project.builder().projectId("proj-1").build())
                .build();

        when(jobContract.findJobById("job-1"))
                .thenReturn(Mono.just(job));

        when(userContract.findUserById("user-1"))
                .thenReturn(Mono.just(User.builder().userId("user-1").build()));

        when(projectContract.findProjectById("proj-1"))
                .thenReturn(Mono.just(Project.builder().projectId("proj-1").build()));

        StepVerifier.create(jobService.searchJob("job-1"))
                .assertNext(result -> {
                    assertEquals("job-1", result.getJobId());
                    assertNotNull(result.getUser());
                    assertNotNull(result.getProject());
                })
                .verifyComplete();
    }
}