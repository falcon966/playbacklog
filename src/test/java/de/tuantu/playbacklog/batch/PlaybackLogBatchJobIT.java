package de.tuantu.playbacklog.batch;

import de.tuantu.playbacklog.IntegrationTest;
import de.tuantu.playbacklog.persistence.PlaybackLogRepository;
import de.tuantu.playbacklog.service.PlaybackLogJobService;
import de.tuantu.playbacklog.service.domain.StoredFile;
import de.tuantu.playbacklog.shared.StorageType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.test.context.SpringBatchTest;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@SpringBatchTest
public class PlaybackLogBatchJobIT {

    @Autowired
    private PlaybackLogRepository repository;

    @Autowired
    private PlaybackLogJobService playbackLogJobService;

    @Test
    void testEntireJobCompletesAndSavesData(){

        StoredFile storedFile = new StoredFile(
                UUID.randomUUID(),
                "test-logs.csv",
                Path.of("src/test/resources/test-logs.csv"),
                StorageType.LOCAL_FILESYSTEM
        );

        JobExecution jobExecution = playbackLogJobService.triggerImport(
                storedFile
        );

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.STARTING);

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void testRetrieveJob(){
        StoredFile storedFile = new StoredFile(
                UUID.randomUUID(),
                "test-logs.csv",
                Path.of("src/test/resources/test-logs.csv"),
                StorageType.LOCAL_FILESYSTEM
        );

        JobExecution jobExecution = playbackLogJobService.triggerImport(
                storedFile
        );

        JobExecution result = playbackLogJobService.getJob(jobExecution.getId());

        assertThat(result).isNotNull();
        assertThat(result.getJobInstance()).isEqualTo(jobExecution.getJobInstance());
    }

    @Test
    void testRetrieveJobNotFound(){
        assertThatThrownBy(() -> playbackLogJobService.getJob(123123)).isInstanceOf(
                NoSuchElementException.class
        );
    }
}
