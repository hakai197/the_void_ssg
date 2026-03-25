package com.thevoid.ssg.service;

import com.thevoid.ssg.model.enums.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EntityDetectionService {

    private static final Map<EntityType, List<Pattern>> ENTITY_PATTERNS = new HashMap<>();

    static {
        ENTITY_PATTERNS.put(EntityType.CTHULHU, List.of(
                Pattern.compile("(?i)\\bcthulhu\\b"),
                Pattern.compile("(?i)\\br'lyeh\\b"),
                Pattern.compile("(?i)\\bdead\\s+cthulhu\\b"),
                Pattern.compile("(?i)\\bph'nglui\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.NYARLATHOTEP, List.of(
                Pattern.compile("(?i)\\bnyarlathotep\\b"),
                Pattern.compile("(?i)\\bcrawling\\s+chaos\\b"),
                Pattern.compile("(?i)\\bblack\\s+pharaoh\\b"),
                Pattern.compile("(?i)\\bhaunter\\s+of\\s+the\\s+dark\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.YOG_SOTHOTH, List.of(
                Pattern.compile("(?i)\\byog-?sothoth\\b"),
                Pattern.compile("(?i)\\bkey\\s+and\\s+gate\\b"),
                Pattern.compile("(?i)\\blurker\\s+at\\s+the\\s+threshold\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.AZATHOTH, List.of(
                Pattern.compile("(?i)\\bazathoth\\b"),
                Pattern.compile("(?i)\\bnuclear\\s+chaos\\b"),
                Pattern.compile("(?i)\\bdemon\\s+sultan\\b"),
                Pattern.compile("(?i)\\bbinding\\s+ritual\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.DAGON, List.of(
                Pattern.compile("(?i)\\bdagon\\b"),
                Pattern.compile("(?i)\\bdeep\\s+ones\\b"),
                Pattern.compile("(?i)\\bfather\\s+dagon\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.SHUB_NIGGURATH, List.of(
                Pattern.compile("(?i)\\bshub-?niggurath\\b"),
                Pattern.compile("(?i)\\bblack\\s+goat\\b"),
                Pattern.compile("(?i)\\bthousand\\s+young\\b")
        ));

        ENTITY_PATTERNS.put(EntityType.HASTUR, List.of(
                Pattern.compile("(?i)\\bhastur\\b"),
                Pattern.compile("(?i)\\bking\\s+in\\s+yellow\\b"),
                Pattern.compile("(?i)\\bcarcosa\\b")
        ));
    }

    public boolean containsEntity(String content) {
        for (EntityType entity : EntityType.values()) {
            if (detectEntity(content, entity) != null) {
                return true;
            }
        }
        return false;
    }

    public String detectEntity(String content) {
        for (EntityType entity : EntityType.values()) {
            String detected = detectEntity(content, entity);
            if (detected != null) {
                return detected;
            }
        }
        return null;
    }

    private String detectEntity(String content, EntityType entity) {
        List<Pattern> patterns = ENTITY_PATTERNS.get(entity);
        if (patterns == null) return null;

        for (Pattern pattern : patterns) {
            if (pattern.matcher(content).find()) {
                log.debug("[ENTITY] Detected {} in content", entity.getName());
                return entity.getName();
            }
        }
        return null;
    }

    public String getEntityWarning(EntityType entity) {
        return switch(entity) {
            case CTHULHU -> "[WARNING] The Sleeper stirs in R'lyeh...";
            case NYARLATHOTEP -> "[WARNING] The Crawling Chaos walks among your words...";
            case YOG_SOTHOTH -> "[WARNING] The Key and Gate has been mentioned...";
            case AZATHOTH -> "[WARNING] The Nuclear Chaos listens...";
            case DAGON -> "[WARNING] Father Dagon rises from the depths...";
            case SHUB_NIGGURATH -> "[WARNING] The Black Goat of the Woods with a Thousand Young...";
            case HASTUR -> "[WARNING] The King in Yellow watches...";
        };
    }
}