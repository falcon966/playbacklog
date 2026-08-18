package de.tuantu.playbacklog.service.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PlaybackLogDto(
        UUID id,
        OffsetDateTime ts,
        String isrcCode,
        String stationId,
        int durationSeconds,
        int listenerCount,
        String artistName,
        String trackName,
        String rightsHolder,
        long listenedSeconds
) {
}
