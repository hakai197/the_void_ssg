package com.thevoid.ssg.model.dto;

import com.thevoid.ssg.model.enums.EntropyMode;
import java.time.LocalDateTime;

public record SiteDto(
        String id,
        String name,
        EntropyMode entropyMode,
        Integer sanityThreshold,
        Integer corruptionIntensity,
        Integer entryCount,
        LocalDateTime lastBuilt,
        String entityWard
) {}