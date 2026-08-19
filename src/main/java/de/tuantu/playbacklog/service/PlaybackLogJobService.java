package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.service.domain.StoredFile;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PlaybackLogJobService {
    private final JobOperator jobOperator;
    private final Job playbackLogImportJob;
    private final JobRepository jobRepository;

    public PlaybackLogJobService(JobOperator jobOperator, Job playbackLogImportJob, JobRepository jobRepository) {
        this.jobOperator = jobOperator;
        this.playbackLogImportJob = playbackLogImportJob;
        this.jobRepository = jobRepository;
    }

    public JobExecution triggerImport(
            StoredFile storedFile
    ){
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", storedFile.storagePath().toAbsolutePath().toString())
                .addString("filename", storedFile.filename())
                .toJobParameters();
        try {
            return jobOperator.start(playbackLogImportJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException("Konnte den Batch-Job nicht starten", e);
        }

    }

    public JobExecution getJob(long jobId) {
        JobExecution job = jobRepository.getJobExecution(jobId);
        if(job == null)
            throw new NoSuchElementException("Job mit der ID " + jobId + " wurde nicht gefunden");
        return job;
    }
}
