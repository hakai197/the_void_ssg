package com.thevoid.ssg.service;

import com.thevoid.ssg.model.dto.CorruptionPreviewDto;
import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.model.enums.EntropyMode;
import com.thevoid.ssg.util.EldritchSymbols;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntropyService {

    private final EldritchSymbols eldritchSymbols;

    private static final List<Pattern> CORRUPTION_PATTERNS = List.of(
            Pattern.compile("(?i)(the|that|this) (truth|knowledge|secret)"),
            Pattern.compile("(?i)(ancient|old|eldritch) (one|god|being)"),
            Pattern.compile("(?i)(call|summon|invoke)"),
            Pattern.compile("(?i)(dream|nightmare|vision)")
    );

    public String applyEntropy(Entry entry, Site site, String viewerIdentifier) {
        if (site.getEntropyMode() == EntropyMode.NONE) {
            return entry.getContent();
        }

        int seed = calculateCorruptionSeed(entry, site, viewerIdentifier);
        Random random = new Random(seed);

        String corrupted = entry.getContent();

        double intensity = site.getCorruptionIntensity() / 100.0;
        int corruptionPasses = Math.max(1, (int)(intensity * 5));

        for (int i = 0; i < corruptionPasses; i++) {
            corrupted = applyCorruptionPass(corrupted, random, intensity);
        }

        entry.setCorruptionLevel(Math.min(100, entry.getCorruptionLevel() +
                (int)(random.nextDouble() * 10 * intensity)));
        entry.setLastCorrupted(LocalDateTime.now());

        return corrupted;
    }

    private int calculateCorruptionSeed(Entry entry, Site site, String viewerIdentifier) {
        return switch(site.getEntropyMode()) {
            case DAILY -> Objects.hash(
                    entry.getId(),
                    LocalDate.now().toString(),
                    site.getBuildHash()
            );
            case USER_BASED -> Objects.hash(
                    entry.getId(),
                    viewerIdentifier,
                    site.getBuildHash()
            );
            case CRYPTOGRAPHIC -> Objects.hash(
                    entry.getId(),
                    site.getBuildHash(),
                    entry.getVersion()
            );
            default -> entry.getContent().hashCode();
        };
    }

    private String applyCorruptionPass(String content, Random random, double intensity) {
        StringBuilder result = new StringBuilder(content);

        if (random.nextDouble() < 0.1 * intensity) {
            int position = random.nextInt(result.length());
            result.replace(position, position + 1,
                    eldritchSymbols.getRandomSymbol(random));
        }

        if (random.nextDouble() < 0.05 * intensity) {
            for (Pattern pattern : CORRUPTION_PATTERNS) {
                var matcher = pattern.matcher(result);
                if (matcher.find() && random.nextDouble() < 0.3) {
                    String replacement = eldritchSymbols.getRandomWhisper(random);
                    result.replace(matcher.start(), matcher.end(),
                            "[" + replacement + "]");
                    break;
                }
            }
        }

        if (random.nextDouble() < 0.08 * intensity) {
            Pattern imagePattern = Pattern.compile("!\\[.*?\\]\\(.*?\\)");
            var matcher = imagePattern.matcher(result);
            if (matcher.find()) {
                String corruptedImage = eldritchSymbols.getCorruptedImagePath(random);
                result.replace(matcher.start(), matcher.end(),
                        "![CORRUPTED](" + corruptedImage + ")");
            }
        }

        if (random.nextDouble() < 0.03 * intensity) {
            String insertion = "\n\n> " + eldritchSymbols.getRandomEldritchText(random);
            result.append(insertion);
        }

        return result.toString();
    }

    public CorruptionPreviewDto previewCorruption(Entry entry, Site site, String viewerIdentifier) {
        String corrupted = applyEntropy(entry, site, viewerIdentifier);
        int corruptionPercentage = calculateCorruptionPercentage(entry.getContent(), corrupted);

        return new CorruptionPreviewDto(
                corrupted,
                corruptionPercentage,
                identifyCorruptedElements(entry.getContent(), corrupted),
                generateCorruptionNarrative(corruptionPercentage)
        );
    }

    private int calculateCorruptionPercentage(String original, String corrupted) {
        if (original.isEmpty()) return 0;

        int differences = 0;
        int minLength = Math.min(original.length(), corrupted.length());

        for (int i = 0; i < minLength; i++) {
            if (original.charAt(i) != corrupted.charAt(i)) {
                differences++;
            }
        }

        differences += Math.abs(original.length() - corrupted.length());
        return Math.min(100, (differences * 100) / original.length());
    }

    private List<String> identifyCorruptedElements(String original, String corrupted) {
        List<String> elements = new ArrayList<>();

        if (corrupted.split("\n\n").length < original.split("\n\n").length) {
            elements.add("PARAGRAPHS_VANISHED");
        }

        for (char symbol : eldritchSymbols.getAllSymbols().toCharArray()) {
            if (corrupted.indexOf(symbol) > -1 && original.indexOf(symbol) == -1) {
                elements.add("ELDRITCH_MARKINGS");
                break;
            }
        }

        if (corrupted.contains("CORRUPTED") && !original.contains("CORRUPTED")) {
            elements.add("IMAGES_CORRUPTED");
        }

        return elements;
    }

    private String generateCorruptionNarrative(int percentage) {
        if (percentage < 10) return "The void stirs... barely noticeable.";
        if (percentage < 30) return "Reality begins to fray at the edges.";
        if (percentage < 50) return "Words twist and meanings shift.";
        if (percentage < 70) return "The text bleeds into something else.";
        return "The entry is no longer entirely of this world.";
    }
}
