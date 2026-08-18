package de.tuantu.playbacklog.persistence;

import de.tuantu.playbacklog.persistence.model.PlaybackLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaybackLogRepository extends JpaRepository<PlaybackLogEntity, UUID> {
}
