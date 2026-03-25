package com.thevoid.ssg.controller;

import com.thevoid.ssg.model.dto.SiteDto;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.model.enums.EntropyMode;
import com.thevoid.ssg.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping
    public ResponseEntity<List<SiteDto>> getAllSites() {
        List<SiteDto> sites = siteService.getAllSites().stream()
                .map(siteService::toDto)
                .toList();
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteDto> getSite(@PathVariable String id) {
        return siteService.getSite(id)
                .map(siteService::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SiteDto> createSite(
            @RequestParam String name,
            @RequestParam(defaultValue = "DAILY") EntropyMode entropyMode,
            @RequestParam(defaultValue = "50") Integer sanityThreshold
    ) {
        Site site = siteService.createSite(name, entropyMode, sanityThreshold);
        return ResponseEntity.ok(siteService.toDto(site));
    }

    @PatchMapping("/{id}/entropy")
    public ResponseEntity<SiteDto> updateEntropy(
            @PathVariable String id,
            @RequestParam EntropyMode mode
    ) {
        Site site = siteService.updateEntropyMode(id, mode);
        return ResponseEntity.ok(siteService.toDto(site));
    }

    @PatchMapping("/{id}/intensity")
    public ResponseEntity<SiteDto> updateIntensity(
            @PathVariable String id,
            @RequestParam Integer intensity
    ) {
        Site site = siteService.updateCorruptionIntensity(id, intensity);
        return ResponseEntity.ok(siteService.toDto(site));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable String id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}