package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
public class PlaybackLogEnrichingService implements ItemProcessor<PlaybackLogCsvInputDto, PlaybackLogEntity> {

    @Override
    public PlaybackLogEntity process(@NonNull PlaybackLogCsvInputDto item) {

        // TODO enrich Data through api call
        String artistName = resolveArtist(item.isrcCode());
        String trackName = resolveTrack(item.isrcCode());
        String rightsHolder = resolveRightsHolder(item.isrcCode());
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

    private String resolveTrack(String isrcCode) {
        return "";
    }

    private String resolveRightsHolder(String isrcCode) {
        return "";
    }

    private String resolveArtist(String isrcCode) {
        return "";
    }

}
