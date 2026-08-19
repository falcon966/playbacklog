package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import de.tuantu.playbacklog.service.domain.WorkCatalogDto;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
public class PlaybackLogEnrichingService implements ItemProcessor<PlaybackLogCsvInputDto, PlaybackLogEntity> {

    private WorkCatalogAPIService workCatalogAPIService;

    public PlaybackLogEnrichingService(WorkCatalogAPIService workCatalogAPIService) {
        this.workCatalogAPIService = workCatalogAPIService;
    }

    @Override
    public PlaybackLogEntity process(@NonNull PlaybackLogCsvInputDto item) {

        // TODO enrich Data through api call
        WorkCatalogDto additionalData = workCatalogAPIService.getAdditionalData(item.isrcCode());
        String artistName = additionalData.artistName();
        String trackName = additionalData.trackName();
        String rightsHolder = additionalData.rightsHolder();
        long listenedSeconds = (long) item.durationSeconds() * item.listenerCount();

        PlaybackLogEntity entity = new PlaybackLogEntity();
        entity.setTs(item.timestamp());
        entity.setIsrcCode(item.isrcCode());
        entity.setStationId(item.stationId());
        entity.setDurationSeconds(item.durationSeconds());
        entity.setListenerCount(item.listenerCount());

        entity.setArtistName(artistName);
        entity.setTrackName(trackName);
        entity.setRightsHolder(rightsHolder);
        entity.setListenedSeconds(listenedSeconds);

        return entity;

    }

}
