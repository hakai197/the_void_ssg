package com.thevoid.ssg.model.enums;

import lombok.Getter;

@Getter
public enum EntityType {
    CTHULHU("Cthulhu", "The Great Dreamer"),
    NYARLATHOTEP("Nyarlathotep", "The Crawling Chaos"),
    YOG_SOTHOTH("Yog-Sothoth", "The Key and the Gate"),
    AZATHOTH("Azathoth", "The Nuclear Chaos"),
    DAGON("Dagon", "Father Dagon"),
    SHUB_NIGGURATH("Shub-Niggurath", "The Black Goat of the Woods"),
    HASTUR("Hastur", "The King in Yellow");

    private final String name;
    private final String title;

    EntityType(String name, String title) {
        this.name = name;
        this.title = title;
    }
}
