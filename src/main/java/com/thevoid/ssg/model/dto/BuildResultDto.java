package com.thevoid.ssg.model.dto;

public record BuildResultDto(
        String narrative,
        String buildLogId,
        int entityDetections,
        int corruptedEntries,
        long durationMs,
        boolean success
) {}