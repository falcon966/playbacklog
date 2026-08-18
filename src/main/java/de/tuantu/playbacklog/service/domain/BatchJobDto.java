package de.tuantu.playbacklog.service.domain;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;

public record BatchJobDto(
        long jobId,
        String jobName,
        BatchStatus status
) {
    public static BatchJobDto from(JobExecution jobExecution){
        return new BatchJobDto(
                jobExecution.getId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()
        );
    }
}
