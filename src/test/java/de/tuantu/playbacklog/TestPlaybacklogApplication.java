package de.tuantu.playbacklog;

import org.springframework.boot.SpringApplication;

public class TestPlaybacklogApplication {

    static void main(String[] args) {
        SpringApplication.from(PlaybacklogApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
