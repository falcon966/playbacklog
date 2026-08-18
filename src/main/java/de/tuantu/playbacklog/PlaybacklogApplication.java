package de.tuantu.playbacklog;

import de.tuantu.playbacklog.config.StorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageConfig.class)
public class PlaybacklogApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaybacklogApplication.class, args);
    }

}
