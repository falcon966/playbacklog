package de.tuantu.playbacklog.persistence;

import de.tuantu.playbacklog.RepositoryTest;
import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class PlaybackLogRepositoryTest {

    @Autowired
    private PlaybackLogRepository playbackLogRepository;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void saveLogTestSuccess() {
        PlaybackLogEntity playbackLogEntity = easyRandom.nextObject(PlaybackLogEntity.class);
        playbackLogEntity.setId(null);

        playbackLogRepository.save(playbackLogEntity);

        assertThat(playbackLogRepository.findAll())
                .singleElement()
                .usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(playbackLogEntity);
    }

}
