package com.thevoid.ssg.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Data
@NoArgsConstructor
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "user_hash", nullable = false)
    private String userHash;

    @Column(name = "visit_count")
    private Integer visitCount = 1;

    @CreationTimestamp
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
}