package de.tuantu.playbacklog.controller;

import de.tuantu.playbacklog.service.FileUploadService;
import de.tuantu.playbacklog.service.PlaybackLogJobService;
import de.tuantu.playbacklog.service.domain.BatchJobDto;
import de.tuantu.playbacklog.service.domain.StoredFile;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/playbacklog")
public class PlaybacklogController {

    private final PlaybackLogJobService playbackLogService;
    private final FileUploadService fileUploadService;

    public PlaybacklogController(PlaybackLogJobService playbackLogService, FileUploadService fileUploadService) {
        this.playbackLogService = playbackLogService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/startupload")
    public BatchJobDto uploadFileAndStartImportJob(@RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            StoredFile storedFile = fileUploadService.store(inputStream, file.getOriginalFilename());
            JobExecution job = playbackLogService.triggerImport(storedFile);
            return BatchJobDto.from(job);
        }
    }

    @GetMapping("/jobstatus/{jobId}")
    public BatchJobDto getJobStatus(@PathVariable long jobId) {
        JobExecution job = playbackLogService.getJob(jobId);
        return BatchJobDto.from(job);
    }

}
