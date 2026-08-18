package de.tuantu.playbacklog.service.domain;

import java.time.OffsetDateTime;

public record PlaybackLogCsvInputDto (
    OffsetDateTime timestamp,
    String isrcCode,
    String stationId,
    int durationSeconds,
    int listenerCount
) {
}
