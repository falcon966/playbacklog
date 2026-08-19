package de.tuantu.playbacklog.service.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public record PlaybackLogCsvInputDto (
        @NotNull OffsetDateTime timestamp,
        @NotBlank String isrcCode,
        @NotBlank String stationId,
        @Positive Integer durationSeconds,
        @PositiveOrZero Integer listenerCount
) {
}
