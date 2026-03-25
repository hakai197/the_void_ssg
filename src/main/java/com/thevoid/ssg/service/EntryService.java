package com.thevoid.ssg.service;

import com.thevoid.ssg.model.dto.CorruptionPreviewDto;
import com.thevoid.ssg.model.dto.EntryDto;
import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.repository.EntryRepository;
import com.thevoid.ssg.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryService {

    private final EntryRepository entryRepository;
    private final SiteRepository siteRepository;
    private final EntropyService entropyService;

    @Transactional(readOnly = true)
    public Optional<Site> findSite(String siteId) {
        return siteRepository.findById(siteId);
    }

    @Transactional(readOnly = true)
    public List<EntryDto> getEntries(String siteId) {
        return entryRepository.findBySiteId(siteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<EntryDto> getEntry(String siteId, String slug) {
        return entryRepository.findBySiteIdAndSlug(siteId, slug)
                .map(entry -> {
                    entryRepository.incrementViewCount(entry.getId());
                    return toDto(entry);
                });
    }

    @Transactional
    public EntryDto createEntry(Site site, String title, String slug, String content) {
        Entry entry = new Entry();
        entry.setTitle(title);
        entry.setSlug(slug);
        entry.setContent(content);
        entry.setSite(site);

        Entry saved = entryRepository.save(entry);
        log.info("[VOID] Entry '{}' inscribed into site '{}'", title, site.getName());
        return toDto(saved);
    }

    @Transactional
    public Optional<EntryDto> updateEntry(String siteId, String slug, String content) {
        return entryRepository.findBySiteIdAndSlug(siteId, slug)
                .map(entry -> {
                    entry.setContent(content);
                    return toDto(entryRepository.save(entry));
                });
    }

    @Transactional
    public boolean deleteEntry(String siteId, String slug) {
        return entryRepository.findBySiteIdAndSlug(siteId, slug)
                .map(entry -> {
                    entryRepository.delete(entry);
                    log.info("[VOID] Entry '{}' consumed by the void", slug);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<CorruptionPreviewDto> previewCorruption(String siteId, String slug, String viewerHash) {
        Entry entry = entryRepository.findBySiteIdAndSlug(siteId, slug).orElse(null);
        Site site = siteRepository.findById(siteId).orElse(null);

        if (entry == null || site == null) {
            return Optional.empty();
        }

        return Optional.of(entropyService.previewCorruption(entry, site, viewerHash));
    }

    public EntryDto toDto(Entry entry) {
        String preview = entry.getContent();
        if (preview != null && preview.length() > 200) {
            preview = preview.substring(0, 200) + "...";
        }

        return new EntryDto(
                entry.getId(),
                entry.getTitle(),
                entry.getSlug(),
                entry.getContent(),
                preview,
                entry.getCorruptionLevel(),
                entry.getEntityInfluence(),
                entry.getRequiresRitual(),
                entry.getViewCount(),
                entry.getCreatedAt(),
                entry.getLastCorrupted()
        );
    }
}
