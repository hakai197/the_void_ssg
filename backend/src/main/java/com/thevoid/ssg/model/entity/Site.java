package com.thevoid.ssg.model.entity;

import com.thevoid.ssg.model.enums.EntropyMode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
@Data
@NoArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "output_path")
    private String outputPath;

    @Enumerated(EnumType.STRING)  // Add this annotation
    @Column(name = "entropy_mode", nullable = false)
    private EntropyMode entropyMode = EntropyMode.DAILY;

    @Column(name = "sanity_threshold")
    private Integer sanityThreshold = 50;

    @Column(name = "corruption_intensity")
    private Integer corruptionIntensity = 30;

    @Column(name = "build_hash")
    private String buildHash;

    @Column(name = "entity_ward")
    private String entityWard;

    @Column(name = "last_built")
    private LocalDateTime lastBuilt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<BuildLog> buildLogs = new ArrayList<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Visitor> visitors = new ArrayList<>();

    public void addEntry(Entry entry) {
        entries.add(entry);
        entry.setSite(this);
    }

    public void removeEntry(Entry entry) {
        entries.remove(entry);
        entry.setSite(null);
    }
}