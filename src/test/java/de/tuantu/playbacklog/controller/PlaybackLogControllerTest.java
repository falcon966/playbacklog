package de.tuantu.playbacklog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tuantu.playbacklog.service.FileUploadService;
import de.tuantu.playbacklog.service.PlaybackLogJobService;
import de.tuantu.playbacklog.service.domain.BatchJobDto;
import de.tuantu.playbacklog.service.domain.StoredFile;
import de.tuantu.playbacklog.shared.StorageType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaybacklogController.class)
public class PlaybackLogControllerTest {

    @MockitoBean
    private PlaybackLogJobService playbackLogService;

    @MockitoBean
    private FileUploadService fileUploadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getJobStatusTest() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        when(playbackLogService.getJob(jobExecution.getId())).thenReturn(jobExecution);
        mockMvc.perform(get("/playbacklog/jobstatus/"+jobExecution.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void uploadFileTest() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "2026-01-15T08:30:00Z,USRC17607839,RADIO_WDR,245,150000".getBytes()
        );
        StoredFile file = new StoredFile(
                UUID.randomUUID(),
                "Meine Testdatei",
                Path.of("src/test/resources/test-logs.csv"),
                StorageType.LOCAL_FILESYSTEM
        );
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        when(fileUploadService.store(any(),any())).thenReturn(file);
        when(playbackLogService.triggerImport(any())).thenReturn(jobExecution);
        BatchJobDto batchJobDto = BatchJobDto.from(jobExecution);

        MvcResult result = mockMvc.perform(multipart("/playbacklog/startupload")
                        .file(mockFile)
                        .param("description", "Meine Testdatei"))
                .andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BatchJobDto actualDto = objectMapper.readValue(jsonResponse, BatchJobDto.class);
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(batchJobDto);
    }

}
