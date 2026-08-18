package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.config.StorageConfig;
import de.tuantu.playbacklog.service.domain.StoredFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

    @TempDir
    Path tempBaseDir;

    @Mock
    private StorageConfig storageConfig;

    private FileUploadService fileUploadService;

    @BeforeEach
    void setup(){
        when(storageConfig.baseDir()).thenReturn(tempBaseDir.toString());
        fileUploadService = new FileUploadService(storageConfig);
    }

    @Test
    void storeFileTestSuccess(){
        String originalFilename = "test.csv";
        String fileContent = "2026-01-15T08:30:00Z,USRC17607839,RADIO_WDR,245,150000";
        InputStream in = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        StoredFile storedFile = null;
        try {
            storedFile = fileUploadService.store(in, originalFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert storedFile != null;
        assertThat(storedFile.filename()).contains(originalFilename);
        assertThat(storedFile.storagePath()).isNotNull();
        assertThat(storedFile.storagePath().toString()).contains(tempBaseDir.toString());
        assertThat(storedFile.storagePath().toString()).contains(originalFilename);
    }

}
