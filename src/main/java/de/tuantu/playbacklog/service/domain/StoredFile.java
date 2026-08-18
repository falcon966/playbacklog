package de.tuantu.playbacklog.service.domain;

import de.tuantu.playbacklog.shared.StorageType;

import java.nio.file.Path;
import java.util.UUID;

public record StoredFile(
        UUID id,
        String filename,
        Path storagePath,
        StorageType storageType
) {
}
