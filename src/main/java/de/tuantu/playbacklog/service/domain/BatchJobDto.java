package de.tuantu.playbacklog.service.domain;

import org.springframework.batch.core.BatchStatus;

public record BatchJobDto(
        long jobId,
        String jobName,
        BatchStatus status
) {
}
