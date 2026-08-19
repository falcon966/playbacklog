package de.tuantu.playbacklog.batch;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.PlaybackLogEnrichingService;
import de.tuantu.playbacklog.service.WorkCatalogAPIService;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import de.tuantu.playbacklog.service.domain.WorkCatalogDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaybackLogProcessorTest {

    @Mock
    private WorkCatalogAPIService workCatalogAPIService;

    @InjectMocks
    PlaybackLogEnrichingService processor;

    @Test
    void process_MapsCsvInputToEntityCorrectly(){

        PlaybackLogCsvInputDto input = new PlaybackLogCsvInputDto(
                OffsetDateTime.parse("2026-01-15T08:30:00Z"),
                "USRC17607839",
                "RADIO_WDR",
                245,
                150000
        );

        when(workCatalogAPIService.getAdditionalData("USRC17607839")).thenReturn(new WorkCatalogDto(
                "USRC17607839", "RADIO_WDR", "2026-01-15T08:30:00Z"));

        PlaybackLogEntity result = processor.process(input);


        assertThat(result).isNotNull();
        assertThat(result.getIsrcCode()).isEqualTo("USRC17607839");
        // 245 s * 150000 listener = 36750000
        assertThat(result.getListenedSeconds()).isEqualTo(36750000L);
    }

}
