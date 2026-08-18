package de.tuantu.playbacklog.service.domain;

import de.tuantu.playbacklog.shared.StorageType;

import java.util.UUID;

public record StoredFile(
        UUID id,
        String filename,
        String storagePath,
        StorageType storageType
) {
}
