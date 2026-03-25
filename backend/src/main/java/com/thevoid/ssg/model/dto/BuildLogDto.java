package com.thevoid.ssg.model.dto;

import java.time.LocalDateTime;

public record BuildLogDto(
        String id,
        LocalDateTime timestamp,
        String narrative,
        int entityDetections,
        int corruptedEntries,
        long buildDurationMs,
        boolean buildSuccessful,
        int warningsCount,
        int whispersGenerated
) {}
