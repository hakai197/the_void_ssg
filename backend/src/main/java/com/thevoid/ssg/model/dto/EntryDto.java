package com.thevoid.ssg.model.dto;

import java.time.LocalDateTime;

public record EntryDto(
        String id,
        String title,
        String slug,
        String content,
        String preview,
        Integer corruptionLevel,
        String entityInfluence,
        Boolean requiresRitual,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime lastCorrupted
) {}