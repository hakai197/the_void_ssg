package com.thevoid.ssg.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "build_logs")
@Data
@NoArgsConstructor
public class BuildLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "LONGTEXT")
    private String narrative;

    @Column(name = "entity_detections")
    private Integer entityDetections = 0;

    @Column(name = "corrupted_entries")
    private Integer corruptedEntries = 0;

    @Column(name = "build_duration_ms")
    private Long buildDurationMs;

    @Column(name = "build_successful")
    private Boolean buildSuccessful = true;

    @Column(name = "warnings_count")
    private Integer warningsCount = 0;

    @Column(name = "whispers_generated")
    private Integer whispersGenerated = 0;
}