package de.tuantu.playbacklog.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "playback_log")
public class PlaybackLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    @Column(name = "ts", nullable = false)
    private OffsetDateTime ts;

    @Getter
    @Setter
    @Column(name = "isrc_code", nullable = false)
    private String isrcCode;

    @Getter
    @Setter
    @Column(name = "station_id", nullable = false)
    private String stationId;

    @Getter
    @Setter
    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Getter
    @Setter
    @Column(name = "listener_count", nullable = false)
    private int listenerCount;

    @Getter
    @Setter
    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Getter
    @Setter
    @Column(name = "track_name", nullable = false)
    private String trackName;

    @Getter
    @Setter
    @Column(name = "rights_holder", nullable = false)
    private String rightsHolder;

    @Getter
    @Setter
    @Column(name = "listener_seconds")
    private long listenedSeconds;

    @Getter
    @Setter
    @Column(name = "import_filename")
    private String importFilename;

    @Getter
    @Setter
    @Column(name = "import_job_id")
    private long importJobId;

}
