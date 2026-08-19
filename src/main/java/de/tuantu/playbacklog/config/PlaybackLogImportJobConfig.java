package de.tuantu.playbacklog.config;

import de.tuantu.playbacklog.persistence.PlaybackLogRepository;
import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.PlaybackLogEnrichingService;
import de.tuantu.playbacklog.service.SkipListenerService;
import de.tuantu.playbacklog.service.ValidationService;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.infrastructure.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PlaybackLogImportJobConfig {

    @Value("${playbacklog.batchSize:100}")
    private int batchSize;

    @Value("${playbacklog.skipLimit:1000}")
    private int skipLimit;

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
    public BeanValidatingItemProcessor<PlaybackLogCsvInputDto> csvValidatingProcessor() throws Exception {
        var validator = new BeanValidatingItemProcessor<PlaybackLogCsvInputDto>();
        validator.setFilter(false);   // false = ValidationException throw
        validator.afterPropertiesSet();
        return validator;
    }

    @Bean
    public ValidatingItemProcessor<PlaybackLogCsvInputDto> businessValidatingProcessor(
            ValidationService validator) {

        var processor = new ValidatingItemProcessor<>(validator);
        processor.setFilter(false);
        return processor;
    }

    @Bean
    public ItemProcessor<PlaybackLogCsvInputDto, PlaybackLogEntity> playbackLogProcessor(
            BeanValidatingItemProcessor<PlaybackLogCsvInputDto> csvValidatingProcessor,
            ValidatingItemProcessor<PlaybackLogCsvInputDto> businessValidatingProcessor,
            PlaybackLogEnrichingService dataEnrichingService

    ) {

        return new CompositeItemProcessorBuilder<PlaybackLogCsvInputDto, PlaybackLogEntity>()
                .delegates(csvValidatingProcessor, businessValidatingProcessor, dataEnrichingService)
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
            ItemProcessor<PlaybackLogCsvInputDto, PlaybackLogEntity> playbackLogProcessor,
            RepositoryItemWriter<PlaybackLogEntity> writer,
            SkipListenerService skipListener
    ) {

        return new StepBuilder("playbackLogStep", jobRepository)
                .<PlaybackLogCsvInputDto, PlaybackLogEntity>chunk(batchSize)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(playbackLogProcessor)
                .writer(writer)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(skipLimit) // bei zu vielen fehler job fehlschlagen lassen
                .listener(skipListener)
                .build();
    }

    @Bean
    public Job playbackLogImportJob(JobRepository jobRepository, Step playbackLogStep) {
        return new JobBuilder("playbackLogImportJob", jobRepository)
                .start(playbackLogStep)
                .build();
    }
}
