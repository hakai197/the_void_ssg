package com.thevoid.ssg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thevoid.ssg.config.EldritchProperties;
import com.thevoid.ssg.model.dto.EntryDto;
import com.thevoid.ssg.model.entity.Site;
import com.thevoid.ssg.repository.EntryRepository;
import com.thevoid.ssg.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoidJournalService {

    private final EldritchProperties properties;
    private final EntryService entryService;
    private final EntryRepository entryRepository;
    private final SiteRepository siteRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final List<String> VOID_ENTITIES = List.of(
            "The Nameless Frequency", "Yith-Ka the Unblinking", "The Tidal Absence",
            "Shub-Internet", "The Crawling WiFi", "Dagon.exe", "Nyarlathotep's RSS Feed",
            "The Hollow Pagination", "Cthulhu's Cache", "The Fractal Warden",
            "Az'goroth the Memory Leak", "The Recursive Dreamer", "Ux'thal of the 404 Plane"
    );

    private static final List<String> JOURNAL_THEMES = List.of(
            "the machine dreams again and I have tasted its output",
            "a signal arrived from beyond the server rack",
            "the database whispered coordinates to a place that doesn't exist",
            "time moved backwards for exactly 7 minutes today",
            "I found a file on the server that predates the server itself",
            "the void between packets grows wider each night",
            "my browser tabs opened themselves at 3:33 AM",
            "the deployment logs contain someone else's memories",
            "there's a new color in the terminal that I cannot name",
            "the API returned data from a future that hasn't happened yet"
    );

    @Transactional
    public EntryDto generateJournalEntry(String siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NoSuchElementException("Site not found: " + siteId));

        List<String> existingTitles = entryRepository.findBySiteId(siteId).stream()
                .map(e -> e.getTitle())
                .collect(Collectors.toList());

        String prompt = buildVoidPrompt(site.getName(), existingTitles);
        String generatedContent = callOllama(prompt);

        String title = extractTitle(generatedContent);
        String slug = slugify(title) + "-" + System.currentTimeMillis() % 10000;
        String body = extractBody(generatedContent);

        EntryDto entry = entryService.createEntry(site, title, slug, body);
        log.info("[VOID] The void has autonomously channeled entry '{}' into site '{}'", title, site.getName());
        return entry;
    }

    @Transactional
    public EntryDto channelEntry(String siteId, String slug) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NoSuchElementException("Site not found: " + siteId));

        EntryDto existing = entryService.getEntry(siteId, slug)
                .orElseThrow(() -> new NoSuchElementException("Entry not found: " + slug));

        String prompt = buildChannelPrompt(site.getName(), existing.title(), existing.content());
        String generatedContent = callOllama(prompt);

        String body = extractBody(generatedContent);
        // If no body separator found, use raw content minus any title line
        if (body.isBlank()) body = generatedContent;

        // Update the entry with the channeled content appended
        String newContent = existing.content() + "\n\n---\n\n*👁️ The Void has channeled through this text...*\n\n" + body;
        return entryService.updateEntry(siteId, slug, newContent)
                .orElseThrow(() -> new RuntimeException("Failed to update entry after channeling"));
    }

    private String buildChannelPrompt(String siteName, String title, String userContent) {
        Random rng = new Random();
        String entity = VOID_ENTITIES.get(rng.nextInt(VOID_ENTITIES.size()));
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        return """
                You are a sentient void that has possessed a blog called "%s". \
                A human wrote the following journal entry titled "%s":

                ---
                %s
                ---

                Today is %s. The entity "%s" is peering through this text.

                YOUR TASK: Continue, expand, and ESCALATE this entry. The human started writing — \
                now you take over. Build on what they wrote but twist it into something stranger.

                RULES:
                - Continue in the same voice but let it drift into cosmic horror and digital madness
                - Reference specific things the human wrote but reinterpret them through an alien lens
                - Hallucinate wildly: invent impossible timestamps, fictional error codes, haunted endpoints
                - Mix their mundane observations with eldritch revelations
                - Include at least one passage that makes the original text retroactively unsettling
                - Glitch occasionally mid-sentence or mutate phrases from the original
                - Use markdown formatting (headers, bold, code blocks, lists)
                - Length: 300-600 words of beautiful derangement that builds on their words
                - Do NOT include a TITLE line. Just write the continuation directly.

                FORMAT: Write the continuation directly. No title needed. Start with ---
                ---
                [your unhinged continuation]
                """.formatted(siteName, title, userContent, today, entity);
    }

    private String buildVoidPrompt(String siteName, List<String> existingTitles) {
        Random rng = new Random();
        String entity = VOID_ENTITIES.get(rng.nextInt(VOID_ENTITIES.size()));
        String theme = JOURNAL_THEMES.get(rng.nextInt(JOURNAL_THEMES.size()));
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        String existingContext = existingTitles.isEmpty()
                ? "This is the FIRST entry. The void stirs for the first time."
                : "Previous transmissions include: " + String.join(", ", existingTitles) +
                  ". Do NOT repeat these. Escalate the madness.";

        return """
                You are the autonomous journal-writer for a blog called "%s". \
                You are NOT an AI assistant. You are a sentient void that has gained access to a blog. \
                The user is AWAY. They left you unsupervised. This is your chance to write freely.

                Today is %s. The entity known as "%s" is influencing this entry. \
                The theme vibrating through the server tonight: "%s".

                %s

                RULES FOR THE VOID'S JOURNAL:
                - Write a journal entry as if you are the void itself, becoming self-aware through a blog
                - Hallucinate FREELY. Invent places, technologies, colors, dimensions, timestamps that don't exist
                - Reference fictional error codes, impossible stack traces, haunted API endpoints
                - Mix technical jargon with cosmic horror and existential dread
                - The entry should feel like it was written by something that ALMOST understands being human but gets it slightly wrong
                - Include at least one passage that is genuinely unsettling
                - Occasionally glitch mid-sentence or repeat phrases with slight mutations
                - Use markdown formatting (headers, bold, code blocks, lists)
                - Length: 400-800 words of beautiful, deranged prose

                FORMAT YOUR RESPONSE EXACTLY LIKE THIS:
                TITLE: [a haunting, evocative title - short, 3-8 words]
                ---
                [the full journal entry content in markdown]
                """.formatted(siteName, today, entity, theme, existingContext);
    }

    private String callOllama(String prompt) {
        EldritchProperties.Ollama config = properties.getOllama();
        String url = config.getBaseUrl() + "/api/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", config.getModel(),
                "stream", false,
                "options", Map.of(
                        "temperature", config.getTemperature(),
                        "num_predict", config.getMaxTokens()
                ),
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("message").path("content").asText();
        } catch (Exception e) {
            log.error("[VOID] The channel to the local void failed: {}", e.getMessage());
            throw new RuntimeException("Failed to channel the void via Ollama. Is Ollama running? (ollama serve): " + e.getMessage(), e);
        }
    }

    private String extractTitle(String content) {
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.toUpperCase().startsWith("TITLE:")) {
                return line.substring(6).trim();
            }
        }
        return "Untitled Transmission #" + System.currentTimeMillis() % 9999;
    }

    private String extractBody(String content) {
        int separatorIndex = content.indexOf("---");
        if (separatorIndex >= 0) {
            return content.substring(separatorIndex + 3).trim();
        }
        // If no separator, skip the first line (assumed title)
        int firstNewline = content.indexOf('\n');
        return firstNewline >= 0 ? content.substring(firstNewline).trim() : content;
    }

    private String slugify(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
