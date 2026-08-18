package de.tuantu.playbacklog.batch;

import de.tuantu.playbacklog.IntegrationTest;
import de.tuantu.playbacklog.persistence.PlaybackLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.test.context.SpringBatchTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBatchTest
public class PlaybackLogBatchJobIT {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private PlaybackLogRepository repository;

    @Test
    void testEntireJobCompletesAndSavesData() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/test-logs.csv")
                .addString("runId", UUID.randomUUID().toString())
                .toJobParameters();

        JobExecution jobExecution = jobOperatorTestUtils.startJob(params);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        assertThat(repository.findAll()).hasSize(2);
    }
}
