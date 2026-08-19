package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Service;

@Service
public class SkipListenerService implements SkipListener<PlaybackLogCsvInputDto, PlaybackLogEntity> {

    private static final Logger log = LoggerFactory.getLogger(SkipListenerService.class);

    //TODO Moelicherweise uebersprungene Zeilen in neue Datei für fix und retry schreiben

    @Override
    public void onSkipInRead(@NonNull Throwable t) {
        if (t instanceof FlatFileParseException ffpe) {
            log.warn("Zeile {} nicht parsebar: {}", ffpe.getLineNumber(), ffpe.getInput());
        }
    }

    @Override
    public void onSkipInProcess(@NonNull PlaybackLogCsvInputDto item, Throwable t) {
        log.warn("Item ungültig: {} - {}", item, t.getMessage());
    }

    @Override
    public void onSkipInWrite(@NonNull PlaybackLogEntity item, Throwable t) {
        log.warn("Schreiben fehlgeschlagen: {} - {}", item, t.getMessage());
    }
}
