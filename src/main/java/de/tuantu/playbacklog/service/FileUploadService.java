package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.config.StorageConfig;
import de.tuantu.playbacklog.service.domain.StoredFile;
import de.tuantu.playbacklog.shared.StorageType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path fileUploadBaseDir;

    public FileUploadService(StorageConfig storageConfig) {
        this.fileUploadBaseDir = Paths.get(storageConfig.baseDir())
                .toAbsolutePath()
                .normalize();
    }

    public StoredFile store(InputStream in, String originalFilename) throws IOException {

        UUID uuid = UUID.randomUUID();
        String filename = uuid+"_"+originalFilename;
        Path target = fileUploadBaseDir.resolve(filename).normalize();
        if (!target.startsWith(fileUploadBaseDir)) throw new IllegalArgumentException("Ungültiger Dateiname");

        StoredFile fileInfo = new StoredFile(
            uuid,
            filename,
            target,
            StorageType.LOCAL_FILESYSTEM
        );

        Path tempFile = Files.createTempFile(fileUploadBaseDir, "upload_", ".tmp");
        try (in) {
            Files.copy(
                    in,
                    tempFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
            Files.move(
                    tempFile,
                    fileInfo.storagePath(),
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            Files.deleteIfExists(tempFile);
            throw new IOException("Fehler beim Speichern der Datei. Temporäre Datei wurde aufgeräumt.", e);
        }
        return fileInfo;
    }

}
