package com.thevoid.ssg.controller;

import com.thevoid.ssg.model.dto.EntryDto;
import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.repository.EntryRepository;
import com.thevoid.ssg.repository.SiteRepository;
import com.thevoid.ssg.service.EntropyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites/{siteId}/entries")
@RequiredArgsConstructor
public class EntryController {

    private final SiteRepository siteRepository;
    private final EntryRepository entryRepository;
    private final EntropyService entropyService;

    @GetMapping
    public ResponseEntity<List<EntryDto>> getEntries(@PathVariable String siteId) {
        Site site = siteRepository.findById(siteId).orElse(null);
        if (site == null) {
            return ResponseEntity.notFound().build();
        }

        List<EntryDto> entries = entryRepository.findBySiteId(siteId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EntryDto> getEntry(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestHeader(value = "X-Viewer-Hash", required = false, defaultValue = "unknown") String viewerHash
    ) {
        Entry entry = entryRepository.findBySiteIdAndSlug(siteId, slug).orElse(null);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        entryRepository.incrementViewCount(entry.getId());

        return ResponseEntity.ok(toDto(entry));
    }

    @PostMapping
    public ResponseEntity<EntryDto> createEntry(
            @PathVariable String siteId,
            @RequestParam String title,
            @RequestParam String slug,
            @RequestParam String content
    ) {
        Site site = siteRepository.findById(siteId).orElse(null);
        if (site == null) {
            return ResponseEntity.notFound().build();
        }

        Entry entry = new Entry();
        entry.setTitle(title);
        entry.setSlug(slug);
        entry.setContent(content);
        entry.setSite(site);

        Entry saved = entryRepository.save(entry);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<EntryDto> updateEntry(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestParam String content
    ) {
        Entry entry = entryRepository.findBySiteIdAndSlug(siteId, slug).orElse(null);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        entry.setContent(content);
        Entry saved = entryRepository.save(entry);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable String siteId,
            @PathVariable String slug
    ) {
        Entry entry = entryRepository.findBySiteIdAndSlug(siteId, slug).orElse(null);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        entryRepository.delete(entry);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{slug}/corrupt")
    public ResponseEntity<?> previewCorruption(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestHeader(value = "X-Viewer-Hash", required = false, defaultValue = "unknown") String viewerHash
    ) {
        Entry entry = entryRepository.findBySiteIdAndSlug(siteId, slug).orElse(null);
        Site site = siteRepository.findById(siteId).orElse(null);

        if (entry == null || site == null) {
            return ResponseEntity.notFound().build();
        }

        var preview = entropyService.previewCorruption(entry, site, viewerHash);
        return ResponseEntity.ok(preview);
    }

    private EntryDto toDto(Entry entry) {
        String preview = entry.getContent();
        if (preview.length() > 200) {
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