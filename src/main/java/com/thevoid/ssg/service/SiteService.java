package com.thevoid.ssg.service;

import com.thevoid.ssg.model.dto.SiteDto;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.model.enums.EntropyMode;
import com.thevoid.ssg.repository.SiteRepository;
import com.thevoid.ssg.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public Site createSite(String name, EntropyMode entropyMode, Integer sanityThreshold) {
        if (siteRepository.existsByName(name)) {
            throw new IllegalArgumentException("A site named '" + name + "' already exists in the void");
        }

        Site site = new Site();
        site.setName(name);
        site.setEntropyMode(entropyMode);
        site.setSanityThreshold(sanityThreshold);
        site.setOutputPath("./void-sites/" + name.toLowerCase().replaceAll("\\s+", "-"));
        site.setBuildHash(hashGenerator.generateSiteHash(name));
        site.setEntityWard(hashGenerator.generateEntityWard());

        Site saved = siteRepository.save(site);
        log.info("[VOID] Site '{}' awakened with entropy mode: {}", name, entropyMode);
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Site> getSite(String id) {
        return siteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Site> getSiteWithEntries(String id) {
        return siteRepository.findByIdWithEntries(id);
    }

    @Transactional(readOnly = true)
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    @Transactional
    public Site updateEntropyMode(String siteId, EntropyMode mode) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found in the void"));

        site.setEntropyMode(mode);
        site.setBuildHash(hashGenerator.regenerateSiteHash(site.getName(), mode));

        log.info("[ENTROPY] Site '{}' now bound to: {}", site.getName(), mode);
        return siteRepository.save(site);
    }

    @Transactional
    public Site updateSanityThreshold(String siteId, Integer threshold) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        site.setSanityThreshold(Math.max(0, Math.min(100, threshold)));
        return siteRepository.save(site);
    }

    @Transactional
    public Site updateCorruptionIntensity(String siteId, Integer intensity) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        site.setCorruptionIntensity(Math.max(0, Math.min(100, intensity)));
        log.info("[CORRUPTION] Site '{}' corruption intensity set to {}%", site.getName(), intensity);
        return siteRepository.save(site);
    }

    @Transactional
    public void deleteSite(String siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        log.warn("[VOID] Site '{}' being consigned to oblivion", site.getName());
        siteRepository.delete(site);
    }

    @Transactional(readOnly = true)
    public SiteDto toDto(Site site) {
        return new SiteDto(
                site.getId(),
                site.getName(),
                site.getEntropyMode(),
                site.getSanityThreshold(),
                site.getCorruptionIntensity(),
                site.getEntries().size(),
                site.getLastBuilt(),
                site.getEntityWard()
        );
    }
}