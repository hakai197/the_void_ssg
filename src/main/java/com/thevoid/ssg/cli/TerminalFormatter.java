package com.thevoid.ssg.cli;

import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.model.enums.EntropyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TerminalFormatter {

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String GRAY = "\u001B[90m";
    private static final String BOLD = "\u001B[1m";

    private static final String[] WHISPERS = {
            "The angles are not what they seem...",
            "Something watches from the periphery...",
            "Have you read what you wrote at 3:33 AM?",
            "The void whispers back...",
            "Some knowledge should remain hidden",
            "Your words echo in places that shouldn't exist",
            "The silence between letters is growing",
            "Reality flickers at the edges of this text"
    };

    // ============ BUILD FORMATTING METHODS ============

    public String formatHeader(String siteName, EntropyMode mode) {
        return String.format("""
            %s╔══════════════════════════════════════════════════════╗
            ║  THE VOID AWAKENS: %-40s ║
            ║  Entropy: %-45s ║
            ╚══════════════════════════════════════════════════════╝%s
            
            """, PURPLE, truncate(siteName, 40), mode.getDescription(), RESET);
    }

    public String formatEntityWarning(String slug) {
        return String.format("%s[WARNING]%s Entity detected in %s. Locking file...\n",
                RED, RESET, slug);
    }

    public String formatCompileSuccess(String title) {
        return String.format("%s[OK]%s Compiled %s\n", GREEN, RESET, title);
    }

    public String formatWhisper() {
        String whisper = WHISPERS[(int)(Math.random() * WHISPERS.length)];
        return String.format("%s[WHISPER]%s %s\n", CYAN, RESET, whisper);
    }

    public String formatLinkingPhase() {
        return String.format("\n%s[LINKING]%s Obfuscating navigation paths...\n",
                YELLOW, RESET);
    }

    public String formatBuildComplete(int totalEntries, int corrupted, int entities, EntropyMode mode) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n%s✓ Build complete.%s ", GREEN, RESET));
        sb.append(String.format("The void now contains %d entries.\n", totalEntries));

        if (corrupted > 0) {
            sb.append(String.format("%s[CORRUPTION]%s %d entries bear the mark.\n",
                    RED, RESET, corrupted));
        }

        if (entities > 0) {
            sb.append(String.format("%s[ENTITY]%s %d entities detected and bound.\n",
                    PURPLE, RESET, entities));
        }

        if (mode != EntropyMode.NONE) {
            sb.append(String.format("\n%s> Some pages may not appear to all visitors.%s\n",
                    GRAY, RESET));
            sb.append(String.format("%s> The void chooses its readers.%s\n",
                    GRAY, RESET));
        }

        return sb.toString();
    }

    // ============ SITE FORMATTING METHODS ============

    public String formatInitSuccess(Site site) {
        StringBuilder sb = new StringBuilder();
        sb.append(PURPLE).append("╔════════════════════════════════════════╗\n");
        sb.append("║  SITE AWAKENED                         ║\n");
        sb.append("╚════════════════════════════════════════╝").append(RESET).append("\n\n");
        sb.append("Name: ").append(BOLD).append(site.getName()).append(RESET).append("\n");
        sb.append("ID: ").append(BOLD).append(site.getId()).append(RESET).append("\n");
        sb.append("Path: ").append(BOLD).append(site.getOutputPath()).append(RESET).append("\n");
        sb.append("Entropy: ").append(BOLD).append(site.getEntropyMode().getDescription()).append(RESET).append("\n");
        sb.append("Ward: ").append(BOLD).append(site.getEntityWard()).append(RESET).append("\n\n");
        sb.append(GRAY).append("The void now knows your name.").append(RESET).append("\n\n");
        sb.append("> Run ").append(GREEN).append("void build ").append(site.getId()).append(RESET).append(" to create your grimoire.\n");
        return sb.toString();
    }

    public String formatSiteListHeader() {
        return String.format("%s╔══════════════════════════════════════════════════════╗%s\n",
                PURPLE, RESET);
    }

    public String formatSiteListItem(Site site) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String lastBuilt = site.getLastBuilt() != null ? site.getLastBuilt().format(formatter) : "Never";
        return String.format("  %s•%s %s %s[%s]%s - Last built: %s\n",
                CYAN, RESET,
                site.getName(),
                GRAY, site.getEntropyMode(), RESET,
                lastBuilt
        );
    }

    public String formatSiteInfo(Site site) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("""
            %s════════════════════════════════════════%s
            
            %sName:%s %s
            %sID:%s %s
            %sEntropy:%s %s
            %sSanity Threshold:%s %d
            %sCorruption Intensity:%s %d%%
            %sEntries:%s %d
            %sLast Built:%s %s
            %sWard:%s %s
            
            %s════════════════════════════════════════%s
            """,
                PURPLE, RESET,
                BOLD, RESET, site.getName(),
                BOLD, RESET, site.getId(),
                BOLD, RESET, site.getEntropyMode().getDescription(),
                BOLD, RESET, site.getSanityThreshold(),
                BOLD, RESET, site.getCorruptionIntensity(),
                BOLD, RESET, site.getEntries().size(),
                BOLD, RESET, site.getLastBuilt() != null ? site.getLastBuilt().format(formatter) : "Never",
                BOLD, RESET, site.getEntityWard(),
                PURPLE, RESET
        );
    }

    public String formatNoSites() {
        return String.format("""
            %s[VOID]%s No sites found.
            > Run %svoid init <name>%s to create your first grimoire.
            """, CYAN, RESET, GREEN, RESET);
    }

    public String formatDeleteSuccess(String siteName) {
        return String.format("""
            %s[OBLIVION]%s Site '%s' has been consigned to the void.
            Its knowledge fades from memory...
            """, PURPLE, RESET, siteName);
    }

    public String formatEntropyChange(String siteName, EntropyMode mode) {
        return String.format("""
            %s[ENTROPY]%s Site '%s' now bound to: %s%s%s
            The void shifts. Reality bends.
            """, CYAN, RESET, siteName, BOLD, mode.getDescription(), RESET);
    }

    public String formatIntensityChange(String siteName, int intensity) {
        return String.format("""
            %s[CORRUPTION]%s Site '%s' intensity set to %s%d%%%s
            The decay accelerates...
            """, RED, RESET, siteName, BOLD, intensity, RESET);
    }

    // ============ ENTRY FORMATTING METHODS ============

    public String formatEntryListHeader(String siteName, int entryCount) {
        return String.format("""
            %s╔══════════════════════════════════════════════════════╗
            ║  GRIMOIRE: %-40s ║
            ║  Entries: %-44d ║
            ╚══════════════════════════════════════════════════════╝%s
            
            """, PURPLE, truncate(siteName, 40), entryCount, RESET);
    }

    public String formatEntryDetailHeader(String title) {
        return String.format("""
            %s╔══════════════════════════════════════════════════════╗
            ║  ENTRY: %-45s ║
            ╚══════════════════════════════════════════════════════╝%s
            
            """, PURPLE, truncate(title, 45), RESET);
    }

    public String formatContentPreview(String content) {
        return String.format("""
            %s━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%s
            %sCONTENT:%s
            %s%s%s
            %s━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%s
            """,
                GRAY, RESET,
                BOLD, RESET,
                GRAY, content, RESET,
                GRAY, RESET
        );
    }

    public String formatEntryCreated(String title, String slug, String id) {
        return String.format("""
            %s[CREATED]%s Entry '%s' has been inscribed in the void.
              Slug: %s
              ID: %s
            
            > Run %svoid entry show <site-id> %s%s to view your creation.
            """,
                GREEN, RESET, title, slug, id,
                GREEN, slug, RESET
        );
    }

    public String formatEntryUpdated(String title, String slug) {
        return String.format("""
            %s[MODIFIED]%s Entry '%s' has been altered.
            The void remembers the change...
            
            > Run %svoid entry show <site-id> %s%s to see the revision.
            """,
                YELLOW, RESET, title,
                GREEN, slug, RESET
        );
    }

    public String formatEntryDeleted(String title) {
        return String.format("""
            %s[OBLIVION]%s Entry '%s' has been consigned to oblivion.
            Its knowledge fades from memory...
            The void hungers.
            """,
                PURPLE, RESET, title
        );
    }

    public String formatConfirmDelete(String title, String slug) {
        return String.format("""
            %s[WARNING]%s You are about to delete '%s'.
            This action cannot be undone.
            
            > To confirm, run: %svoid entry delete <site-id> %s --confirm true%s
            """,
                RED, RESET, title,
                GREEN, slug, RESET
        );
    }

    public String formatCorruptionPreview(String title, int corruptionPercentage) {
        String corruptionSymbol = corruptionPercentage > 70 ? "🔮🔮🔮" :
                (corruptionPercentage > 40 ? "🔮🔮" :
                        (corruptionPercentage > 10 ? "🔮" : "○"));

        return String.format("""
            %s[CORRUPTION PREVIEW]%s %s
            Corruption level: %s%d%%%s %s
            
            """,
                RED, RESET, title,
                BOLD, corruptionPercentage, RESET,
                corruptionSymbol
        );
    }

    // ============ GENERAL UTILITY METHODS ============

    public String formatError(String message) {
        return String.format("%s[ERROR]%s %s\n", RED, RESET, message);
    }

    public String formatHint(String hint) {
        return String.format("%s[?]%s %s\n", CYAN, RESET, hint);
    }

    public String formatWarning(String warning) {
        return String.format("%s[!]%s %s\n", YELLOW, RESET, warning);
    }

    public String formatNotImplemented(String feature) {
        return String.format("%s[VOID]%s %s - not yet implemented.\n", CYAN, RESET, feature);
    }

    private String truncate(String s, int length) {
        if (s == null) return "";
        if (s.length() <= length) return s;
        return s.substring(0, length - 3) + "...";
    }
}