package de.tuantu.playbacklog.service.domain;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record PlaybackLogCsvInputDto (
        @NotNull OffsetDateTime timestamp,
        @NotBlank @Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{3}[0-9]{7}$") String isrcCode,
        @NotBlank String stationId,
        @Positive Integer durationSeconds,
        @PositiveOrZero Integer listenerCount
) {
}
