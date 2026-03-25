package com.thevoid.ssg.cli;

import com.thevoid.ssg.model.dto.CliEntryDto;
import com.thevoid.ssg.model.entity.Entry;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.model.enums.EntropyMode;
import com.thevoid.ssg.repository.EntryRepository;
import com.thevoid.ssg.repository.SiteRepository;
import com.thevoid.ssg.service.BuildService;
import com.thevoid.ssg.service.EntropyService;
import com.thevoid.ssg.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@ShellComponent
@RequiredArgsConstructor
public class VoidCommands {

    private final SiteService siteService;
    private final BuildService buildService;
    private final TerminalFormatter formatter;
    private final SiteRepository siteRepository;
    private final EntryRepository entryRepository;
    private final EntropyService entropyService;

    // ============ SITE MANAGEMENT COMMANDS ============

    @ShellMethod(key = "void init", value = "Initialize a new Void site")
    public String initSite(
            @ShellOption(help = "Name of your grimoire") String name,
            @ShellOption(help = "Entropy mode", defaultValue = "DAILY") EntropyMode mode,
            @ShellOption(help = "Sanity threshold (0-100)", defaultValue = "50") Integer sanity
    ) {
        try {
            Site site = siteService.createSite(name, mode, sanity);
            return formatter.formatInitSuccess(site);
        } catch (IllegalArgumentException e) {
            return formatter.formatError(e.getMessage());
        }
    }

    @ShellMethod(key = "void list", value = "List all sites")
    public String listSites() {
        List<Site> sites = siteService.getAllSites();

        if (sites.isEmpty()) {
            return formatter.formatNoSites();
        }

        StringBuilder output = new StringBuilder();
        output.append(formatter.formatSiteListHeader());

        for (Site site : sites) {
            output.append(formatter.formatSiteListItem(site));
        }

        return output.toString();
    }

    @ShellMethod(key = "void info", value = "Show site details")
    public String siteInfo(@ShellOption(help = "Site ID") String siteId) {
        Optional<Site> siteOpt = siteService.getSite(siteId);
        if (siteOpt.isEmpty()) {
            return formatter.formatError("Site not found in the void");
        }

        return formatter.formatSiteInfo(siteOpt.get());
    }

    @ShellMethod(key = "void delete", value = "Delete a site (consign to oblivion)")
    public String deleteSite(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Confirm deletion", defaultValue = "false") boolean confirm
    ) {
        Optional<Site> siteOpt = siteService.getSite(siteId);
        if (siteOpt.isEmpty()) {
            return formatter.formatError("Site not found");
        }

        if (!confirm) {
            return formatter.formatConfirmDelete(siteOpt.get().getName(), siteId);
        }

        String siteName = siteOpt.get().getName();
        siteService.deleteSite(siteId);
        return formatter.formatDeleteSuccess(siteName);
    }

    @ShellMethod(key = "void entropy", value = "Change entropy mode for a site")
    public String setEntropy(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entropy mode") EntropyMode mode
    ) {
        try {
            Site site = siteService.updateEntropyMode(siteId, mode);
            return formatter.formatEntropyChange(site.getName(), mode);
        } catch (IllegalArgumentException e) {
            return formatter.formatError(e.getMessage());
        }
    }

    @ShellMethod(key = "void intensity", value = "Set corruption intensity (0-100)")
    public String setIntensity(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Intensity 0-100") Integer intensity
    ) {
        try {
            Site site = siteService.updateCorruptionIntensity(siteId, intensity);
            return formatter.formatIntensityChange(site.getName(), intensity);
        } catch (IllegalArgumentException e) {
            return formatter.formatError(e.getMessage());
        }
    }

    @ShellMethod(key = "void build", value = "Build the static site")
    public String buildSite(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Watch mode", defaultValue = "false") boolean watch
    ) {
        if (watch) {
            return formatter.formatNotImplemented("Watch mode - the void is patient");
        }

        try {
            var result = buildService.buildSite(siteId);
            return result.narrative();
        } catch (IllegalArgumentException e) {
            return formatter.formatError(e.getMessage());
        }
    }

    // ============ ENTRY CRUD COMMANDS ============

    @ShellMethod(key = "void entry list", value = "List all entries in a site")
    public String listEntries(
            @ShellOption(help = "Site ID") String siteId
    ) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isEmpty()) {
            return formatter.formatError("Site not found in the void");
        }

        Site site = siteOpt.get();
        List<Entry> entries = entryRepository.findBySiteId(siteId);

        if (entries.isEmpty()) {
            return String.format("""
                %s[VOID]%s Site '%s' contains no entries.
                > Run %svoid entry add %s --title "My First Entry" --content "..."%s
                """,
                    "\u001B[36m", "\u001B[0m",
                    site.getName(),
                    "\u001B[32m", siteId, "\u001B[0m"
            );
        }

        StringBuilder output = new StringBuilder();
        output.append(formatter.formatEntryListHeader(site.getName(), entries.size()));

        for (Entry entry : entries) {
            CliEntryDto dto = toCliDto(entry);
            output.append(dto.formatCompact()).append("\n");
        }

        output.append("\n").append(formatter.formatHint("Use 'void entry show <site-id> <slug>' to view details"));

        return output.toString();
    }

    @ShellMethod(key = "void entry show", value = "Show entry details")
    public String showEntry(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entry slug") String slug
    ) {
        Optional<Entry> entryOpt = entryRepository.findBySiteIdAndSlug(siteId, slug);
        if (entryOpt.isEmpty()) {
            return formatter.formatError("Entry not found in the void");
        }

        Entry entry = entryOpt.get();
        CliEntryDto dto = toCliDto(entry);

        StringBuilder output = new StringBuilder();
        output.append(formatter.formatEntryDetailHeader(entry.getTitle()));
        output.append(dto.format()).append("\n");

        String contentPreview = entry.getContent();
        if (contentPreview == null || contentPreview.isEmpty()) {
            contentPreview = "[Empty]";
        } else if (contentPreview.length() > 500) {
            contentPreview = contentPreview.substring(0, 500) + "\n\n... [content truncated] ...";
        }
        output.append(formatter.formatContentPreview(contentPreview));

        return output.toString();
    }

    @ShellMethod(key = "void entry add", value = "Add a new entry to a site")
    @Transactional
    public String addEntry(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entry title", value = "--title") String title,
            @ShellOption(help = "Entry slug (URL-friendly)", value = "--slug") String slug,
            @ShellOption(help = "Entry content (markdown)", value = "--content", defaultValue = "") String content
    ) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isEmpty()) {
            return formatter.formatError("Site not found");
        }

        Site site = siteOpt.get();

        if (entryRepository.findBySiteIdAndSlug(siteId, slug).isPresent()) {
            return formatter.formatError("An entry with slug '" + slug + "' already exists in this site");
        }

        Entry entry = new Entry();
        entry.setTitle(title);
        entry.setSlug(slug);
        entry.setContent(content);
        entry.setSite(site);
        entry.setCorruptionLevel(0);
        entry.setViewCount(0);

        Entry saved = entryRepository.save(entry);

        return formatter.formatEntryCreated(saved.getTitle(), saved.getSlug(), saved.getId());
    }

    @ShellMethod(key = "void entry update", value = "Update an existing entry")
    @Transactional
    public String updateEntry(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entry slug") String slug,
            @ShellOption(help = "New title (optional)", value = "--title", defaultValue = ShellOption.NULL) String title,
            @ShellOption(help = "New content (optional)", value = "--content", defaultValue = ShellOption.NULL) String content
    ) {
        Optional<Entry> entryOpt = entryRepository.findBySiteIdAndSlug(siteId, slug);
        if (entryOpt.isEmpty()) {
            return formatter.formatError("Entry not found");
        }

        Entry entry = entryOpt.get();
        boolean changed = false;

        if (title != null && !title.equals(entry.getTitle())) {
            entry.setTitle(title);
            changed = true;
        }

        if (content != null && !content.equals(entry.getContent())) {
            entry.setContent(content);
            changed = true;
        }

        if (!changed) {
            return formatter.formatWarning("No changes made to the entry");
        }

        entryRepository.save(entry);

        return formatter.formatEntryUpdated(entry.getTitle(), entry.getSlug());
    }

    @ShellMethod(key = "void entry delete", value = "Delete an entry (consign to oblivion)")
    @Transactional
    public String deleteEntry(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entry slug") String slug,
            @ShellOption(help = "Confirm deletion", defaultValue = "false") boolean confirm
    ) {
        Optional<Entry> entryOpt = entryRepository.findBySiteIdAndSlug(siteId, slug);
        if (entryOpt.isEmpty()) {
            return formatter.formatError("Entry not found");
        }

        Entry entry = entryOpt.get();

        if (!confirm) {
            return formatter.formatConfirmDelete(entry.getTitle(), entry.getSlug());
        }

        String title = entry.getTitle();
        entryRepository.delete(entry);

        return formatter.formatEntryDeleted(title);
    }

    @ShellMethod(key = "void entry corrupt", value = "Preview corruption on an entry")
    public String previewCorruption(
            @ShellOption(help = "Site ID") String siteId,
            @ShellOption(help = "Entry slug") String slug,
            @ShellOption(help = "Viewer hash (for USER_BASED entropy)", defaultValue = "anonymous") String viewerHash
    ) {
        Optional<Entry> entryOpt = entryRepository.findBySiteIdAndSlug(siteId, slug);
        if (entryOpt.isEmpty()) {
            return formatter.formatError("Entry not found");
        }

        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isEmpty()) {
            return formatter.formatError("Site not found");
        }

        Entry entry = entryOpt.get();
        Site site = siteOpt.get();

        var preview = entropyService.previewCorruption(entry, site, viewerHash);

        StringBuilder output = new StringBuilder();
        output.append(formatter.formatCorruptionPreview(entry.getTitle(), preview.corruptionPercentage()));
        output.append("\n");
        output.append(formatter.formatContentPreview(preview.corruptedContent()));

        return output.toString();
    }

    private CliEntryDto toCliDto(Entry entry) {
        return new CliEntryDto(
                entry.getId(),
                entry.getTitle(),
                entry.getSlug(),
                entry.getCorruptionLevel(),
                entry.getEntityInfluence(),
                entry.getRequiresRitual(),
                entry.getViewCount(),
                entry.getCreatedAt(),
                entry.getLastCorrupted()
        );
    }
}