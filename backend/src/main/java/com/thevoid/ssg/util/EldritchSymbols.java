package com.thevoid.ssg.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class EldritchSymbols {

    private static final String SYMBOLS = "⛧☠☥⛥⛤⚸𓁹𓂀𖣠𖦹";

    private static final List<String> WHISPERS = List.of(
            "Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn",
            "Ia! Ia! Shub-Niggurath!",
            "Yog-Sothoth is the gate",
            "The stars are right",
            "Tekeli-li! Tekeli-li!",
            "That is not dead which can eternal lie",
            "In his house at R'lyeh dead Cthulhu waits dreaming"
    );

    private static final List<String> ELDRITCH_TEXTS = List.of(
            "The angles between these words are wrong.",
            "Something watches from the space between letters.",
            "This knowledge was not meant for mortal eyes.",
            "The void whispers truths that break the mind.",
            "Reality is thinner here."
    );

    private static final List<String> CORRUPTED_IMAGES = List.of(
            "/void/static/noise.gif",
            "/void/static/eldritch_sigil.png",
            "/void/static/corrupted_texture.jpg",
            "/void/static/the_void.png"
    );

    public String getRandomSymbol(Random random) {
        return String.valueOf(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
    }

    public String getRandomWhisper(Random random) {
        return WHISPERS.get(random.nextInt(WHISPERS.size()));
    }

    public String getRandomEldritchText(Random random) {
        return ELDRITCH_TEXTS.get(random.nextInt(ELDRITCH_TEXTS.size()));
    }

    public String getCorruptedImagePath(Random random) {
        return CORRUPTED_IMAGES.get(random.nextInt(CORRUPTED_IMAGES.size()));
    }

    public String getAllSymbols() {
        return SYMBOLS;
    }
}