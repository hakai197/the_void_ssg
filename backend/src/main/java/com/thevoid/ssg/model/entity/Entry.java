package com.thevoid.ssg.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "entries", indexes = {
        @Index(name = "idx_site_slug", columnList = "site_id, slug", unique = true),
        @Index(name = "idx_corruption", columnList = "corruption_level")
})
@Data
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private String slug;

    @Column(name = "corruption_level")
    private Integer corruptionLevel = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "corruption_pattern", columnDefinition = "JSON")
    private Map<String, Object> corruptionPattern;

    @Column(name = "entity_influence")
    private String entityInfluence;

    @Column(name = "requires_ritual")
    private Boolean requiresRitual = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_corrupted")
    private LocalDateTime lastCorrupted;

    @Version
    private Integer version;
}