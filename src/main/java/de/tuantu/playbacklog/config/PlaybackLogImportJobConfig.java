package de.tuantu.playbacklog.config;

import de.tuantu.playbacklog.persistence.PlaybackLogRepository;
import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.PlaybackLogEnrichingService;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import de.tuantu.playbacklog.service.domain.StoredFile;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PlaybackLogImportJobConfig {

    private final PlaybackLogEnrichingService processor;

    public PlaybackLogImportJobConfig(PlaybackLogEnrichingService processor) {
        this.processor = processor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<PlaybackLogCsvInputDto> playbackLogItemReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {

        RecordFieldSetMapper<PlaybackLogCsvInputDto> mapper = new RecordFieldSetMapper<>(
                PlaybackLogCsvInputDto.class, ApplicationConversionService.getSharedInstance());

        return new FlatFileItemReaderBuilder<PlaybackLogCsvInputDto>()
                .name("playbackLogItemReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .delimiter(",")
                .names("timestamp", "isrcCode", "stationId", "durationSeconds", "listenerCount")
                .linesToSkip(1)
                .fieldSetMapper(mapper)
                .build();
    }

    @Bean
    public RepositoryItemWriter<PlaybackLogEntity> playbackLogItemWriter(
            PlaybackLogRepository repository) {

        return new RepositoryItemWriterBuilder<PlaybackLogEntity>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step playbackLogStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<PlaybackLogCsvInputDto> reader,
            RepositoryItemWriter<PlaybackLogEntity> writer) {

        return new StepBuilder("playbackLogStep", jobRepository)
                .<PlaybackLogCsvInputDto, PlaybackLogEntity>chunk(100)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(this.processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job playbackLogImportJob(JobRepository jobRepository, Step playbackLogStep) {
        return new JobBuilder("playbackLogImportJob", jobRepository)
                .start(playbackLogStep)
                .build();
    }
}
