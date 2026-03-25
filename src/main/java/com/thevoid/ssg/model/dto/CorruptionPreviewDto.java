package com.thevoid.ssg.model.dto;

import java.util.List;

public record CorruptionPreviewDto(
        String corruptedContent,
        int corruptionPercentage,
        List<String> corruptedElements,
        String narrative
) {}
