package com.thevoid.ssg.service;

import com.thevoid.ssg.model.dto.BuildResultDto;
import com.thevoid.ssg.model.entity.BuildLog;
import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.repository.BuildLogRepository;
import com.thevoid.ssg.repository.EntryRepository;
import com.thevoid.ssg.repository.SiteRepository;
import com.thevoid.ssg.cli.TerminalFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildService {

    private final SiteRepository siteRepository;
    private final EntryRepository entryRepository;
    private final BuildLogRepository buildLogRepository;
    private final EntropyService entropyService;
    private final EntityDetectionService entityDetectionService;
    private final TerminalFormatter terminalFormatter;

    // Create a virtual thread executor for async builds
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public CompletableFuture<BuildResultDto> buildSiteAsync(String siteId) {
        return CompletableFuture.supplyAsync(
                () -> buildSite(siteId),
                virtualThreadExecutor
        );
    }

    @Transactional
    public BuildResultDto buildSite(String siteId) {
        long startTime = System.currentTimeMillis();

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found in the void"));

        List<Entry> entries = entryRepository.findBySiteId(siteId);

        StringBuilder narrative = new StringBuilder();
        narrative.append(terminalFormatter.formatHeader(site.getName(), site.getEntropyMode()));

        int entityDetections = 0;
        int corruptedEntries = 0;
        int warnings = 0;
        int whispers = 0;

        for (Entry entry : entries) {
            if (entityDetectionService.containsEntity(entry.getContent())) {
                entityDetections++;
                warnings++;
                narrative.append(terminalFormatter.formatEntityWarning(entry.getSlug()));

                entry.setCorruptionLevel(Math.min(100, entry.getCorruptionLevel() + 15));
                entry.setEntityInfluence(entityDetectionService.detectEntity(entry.getContent()));
            } else {
                narrative.append(terminalFormatter.formatCompileSuccess(entry.getTitle()));
            }

            if (site.getEntropyMode() != com.thevoid.ssg.model.enums.EntropyMode.NONE) {
                String corrupted = entropyService.applyEntropy(
                        entry, site, "BUILD_PROCESS"
                );
                entry.setContent(corrupted);
                corruptedEntries++;
            }

            if (Math.random() < 0.1) {
                whispers++;
                narrative.append(terminalFormatter.formatWhisper());
            }

            entryRepository.save(entry);
        }

        narrative.append(terminalFormatter.formatLinkingPhase());
        narrative.append(terminalFormatter.formatBuildComplete(
                entries.size(),
                corruptedEntries,
                entityDetections,
                site.getEntropyMode()
        ));

        BuildLog buildLog = new BuildLog();
        buildLog.setSite(site);
        buildLog.setNarrative(narrative.toString());
        buildLog.setEntityDetections(entityDetections);
        buildLog.setCorruptedEntries(corruptedEntries);
        buildLog.setWarningsCount(warnings);
        buildLog.setWhispersGenerated(whispers);
        buildLog.setBuildDurationMs(System.currentTimeMillis() - startTime);
        buildLog.setBuildSuccessful(true);

        buildLogRepository.save(buildLog);

        site.setLastBuilt(LocalDateTime.now());
        site.setBuildHash(generateNewBuildHash(site, entries));
        siteRepository.save(site);

        return new BuildResultDto(
                narrative.toString(),
                buildLog.getId(),
                entityDetections,
                corruptedEntries,
                System.currentTimeMillis() - startTime,
                true
        );
    }

    private String generateNewBuildHash(Site site, List<Entry> entries) {
        StringBuilder hashSource = new StringBuilder();
        hashSource.append(site.getName());
        hashSource.append(site.getEntropyMode());
        hashSource.append(site.getCorruptionIntensity());
        entries.forEach(e -> hashSource.append(e.getCorruptionLevel()));
        hashSource.append(LocalDateTime.now().toLocalDate());

        return Integer.toHexString(hashSource.toString().hashCode());
    }

    @Transactional(readOnly = true)
    public List<BuildLog> getBuildHistory(String siteId) {
        return buildLogRepository.findBySiteIdOrderByTimestampDesc(siteId);
    }
}