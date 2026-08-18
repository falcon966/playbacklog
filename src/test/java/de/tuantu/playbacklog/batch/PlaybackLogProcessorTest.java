package de.tuantu.playbacklog.batch;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.PlaybackLogEnrichingService;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaybackLogProcessorTest {

    @Test
    void process_MapsCsvInputToEntityCorrectly(){

        PlaybackLogEnrichingService processor = new PlaybackLogEnrichingService();
        PlaybackLogCsvInputDto input = new PlaybackLogCsvInputDto(
                OffsetDateTime.parse("2026-01-15T08:30:00Z"),
                "USRC17607839",
                "RADIO_WDR",
                245,
                150000
        );

        PlaybackLogEntity result = processor.process(input);


        assertThat(result).isNotNull();
        assertThat(result.getIsrcCode()).isEqualTo("USRC17607839");
        // 245 s * 150000 listener = 36750000
        assertThat(result.getListenedSeconds()).isEqualTo(36750000L);
    }

}
