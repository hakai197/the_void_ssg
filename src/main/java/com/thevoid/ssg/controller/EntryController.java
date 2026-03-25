package com.thevoid.ssg.controller;

import com.thevoid.ssg.model.dto.CorruptionPreviewDto;
import com.thevoid.ssg.model.dto.EntryDto;
import com.thevoid.ssg.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites/{siteId}/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    @GetMapping
    public ResponseEntity<List<EntryDto>> getEntries(@PathVariable String siteId) {
        return entryService.findSite(siteId)
                .map(site -> ResponseEntity.ok(entryService.getEntries(siteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EntryDto> getEntry(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestHeader(value = "X-Viewer-Hash", required = false, defaultValue = "unknown") String viewerHash
    ) {
        return entryService.getEntry(siteId, slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntryDto> createEntry(
            @PathVariable String siteId,
            @RequestParam String title,
            @RequestParam String slug,
            @RequestParam String content
    ) {
        return entryService.findSite(siteId)
                .map(site -> ResponseEntity.ok(entryService.createEntry(site, title, slug, content)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{slug}")
    public ResponseEntity<EntryDto> updateEntry(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestParam String content
    ) {
        return entryService.updateEntry(siteId, slug, content)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable String siteId,
            @PathVariable String slug
    ) {
        if (entryService.deleteEntry(siteId, slug)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{slug}/corrupt")
    public ResponseEntity<CorruptionPreviewDto> previewCorruption(
            @PathVariable String siteId,
            @PathVariable String slug,
            @RequestHeader(value = "X-Viewer-Hash", required = false, defaultValue = "unknown") String viewerHash
    ) {
        return entryService.previewCorruption(siteId, slug, viewerHash)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}