package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import de.tuantu.playbacklog.service.domain.WorkCatalogDto;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@StepScope
public class PlaybackLogEnrichingService implements ItemProcessor<PlaybackLogCsvInputDto, PlaybackLogEntity> {

    private final WorkCatalogAPIService workCatalogAPIService;

    public PlaybackLogEnrichingService(WorkCatalogAPIService workCatalogAPIService) {
        this.workCatalogAPIService = workCatalogAPIService;
    }

    @Value("#{stepExecution.jobExecution.jobInstance.id}")
    private Long jobId;

    @Value("#{jobParameters['filename']}")
    private String filename;

    @Override
    public PlaybackLogEntity process(@NonNull PlaybackLogCsvInputDto item) {

        // TODO enrich Data through api call
        Optional<WorkCatalogDto> additionalData = workCatalogAPIService.getAdditionalData(item.isrcCode());

        long listenedSeconds = (long) item.durationSeconds() * item.listenerCount();

        PlaybackLogEntity entity = new PlaybackLogEntity();
        entity.setImportJobId(jobId);
        entity.setImportFilename(filename);
        entity.setTs(item.timestamp());
        entity.setIsrcCode(item.isrcCode());
        entity.setStationId(item.stationId());
        entity.setDurationSeconds(item.durationSeconds());
        entity.setListenerCount(item.listenerCount());

        if(additionalData.isPresent()) {
            entity.setArtistName(additionalData.get().artistName());
            entity.setTrackName(additionalData.get().trackName());
            entity.setRightsHolder(additionalData.get().rightsHolder());
        } else {
            entity.setArtistName("");
            entity.setTrackName("");
            entity.setRightsHolder("");
        }
        entity.setListenedSeconds(listenedSeconds);

        return entity;

    }

}
