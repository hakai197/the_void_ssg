package com.thevoid.ssg.service;

import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NavigationService {

    public List<NavigationLink> getVisibleLinks(Site site, List<Entry> allEntries,
                                                String viewerHash, LocalDateTime accessTime) {
        List<NavigationLink> visibleLinks = new ArrayList<>();

        for (Entry entry : allEntries) {
            if (shouldRevealEntry(entry, site, viewerHash, accessTime)) {
                visibleLinks.add(new NavigationLink(
                        entry.getTitle(),
                        entry.getSlug(),
                        entry.getCorruptionLevel(),
                        generateLinkWhisper(entry, viewerHash)
                ));
            }
        }

        Collections.shuffle(visibleLinks);

        if (site.getEntropyMode() != com.thevoid.ssg.model.enums.EntropyMode.NONE) {
            visibleLinks = obfuscateLinks(visibleLinks, viewerHash);
        }

        return visibleLinks;
    }

    private boolean shouldRevealEntry(Entry entry, Site site, String viewerHash,
                                      LocalDateTime accessTime) {
        int entryHash = entry.getId().hashCode();
        int viewerInfluence = viewerHash.hashCode();
        int timeInfluence = accessTime.getHour() * 100 + accessTime.getMinute();
        int siteInfluence = site.getBuildHash().hashCode();

        int visibilityScore = Math.abs((entryHash ^ viewerInfluence ^ timeInfluence ^ siteInfluence) % 101);
        int adjustedScore = visibilityScore - entry.getCorruptionLevel();

        return adjustedScore > site.getSanityThreshold();
    }

    private List<NavigationLink> obfuscateLinks(List<NavigationLink> links, String viewerHash) {
        Random random = new Random(viewerHash.hashCode());

        List<NavigationLink> result = new ArrayList<>();
        for (NavigationLink link : links) {
            if (random.nextDouble() < 0.2) {
                String[] obfuscatedNames = {
                        "[REDACTED]",
                        "???",
                        "The Unnameable",
                        "A Shadowed Path",
                        "Whispers in the Dark"
                };
                result.add(new NavigationLink(
                        obfuscatedNames[random.nextInt(obfuscatedNames.length)],
                        link.url(),
                        link.corruptionLevel(),
                        link.whisper()
                ));
            } else {
                result.add(link);
            }
        }
        return result;
    }

    private String generateLinkWhisper(Entry entry, String viewerHash) {
        Random random = new Random(Objects.hash(entry.getId(), viewerHash));

        List<String> whispers = List.of(
                "This path may not lead where you expect",
                "Something watches from this page",
                "Knowledge has a price",
                "Are you sure you want to know?",
                "The void beckons"
        );

        if (random.nextDouble() < 0.3) {
            return whispers.get(random.nextInt(whispers.size()));
        }
        return null;
    }

    public record NavigationLink(
            String title,
            String url,
            int corruptionLevel,
            String whisper
    ) {}
}