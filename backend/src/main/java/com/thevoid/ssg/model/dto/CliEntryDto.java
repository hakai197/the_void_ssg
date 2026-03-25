package com.thevoid.ssg.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CliEntryDto(
        String id,
        String title,
        String slug,
        int corruptionLevel,
        String entityInfluence,
        boolean requiresRitual,
        int viewCount,
        LocalDateTime createdAt,
        LocalDateTime lastCorrupted
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(title).append("\n");
        sb.append("    ID: ").append(id).append("\n");
        sb.append("    Slug: ").append(slug).append("\n");
        sb.append("    Corruption: ").append(corruptionLevel).append("%");

        if (entityInfluence != null) {
            sb.append(" [INFECTED BY: ").append(entityInfluence).append("]");
        }
        if (requiresRitual) {
            sb.append(" [RITUAL REQUIRED]");
        }

        sb.append("\n    Views: ").append(viewCount);
        sb.append("\n    Created: ").append(createdAt.format(FORMATTER));

        if (lastCorrupted != null) {
            sb.append("\n    Last Corrupted: ").append(lastCorrupted.format(FORMATTER));
        }

        return sb.toString();
    }

    public String formatCompact() {
        String marker = corruptionLevel > 50 ? "🔮" : (corruptionLevel > 20 ? "◉" : "○");
        String entityMarker = entityInfluence != null ? " ⛧" : "";
        return String.format("  %s %-30s [%d%%]%s",
                marker,
                title.length() > 30 ? title.substring(0, 27) + "..." : title,
                corruptionLevel,
                entityMarker
        );
    }
}