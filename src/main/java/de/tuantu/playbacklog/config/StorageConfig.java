package de.tuantu.playbacklog.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "playbacklog.storage")
@Validated
public record StorageConfig(@NotBlank String baseDir) {
}
