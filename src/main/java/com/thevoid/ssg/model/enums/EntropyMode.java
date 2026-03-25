package com.thevoid.ssg.model.enums;

public enum EntropyMode {
    NONE("No corruption - the void sleeps"),
    DAILY("Corruption changes with the cosmic calendar"),
    USER_BASED("Each viewer sees their own corruption"),
    CRYPTOGRAPHIC("Corruption bound to the site's hash");

    private final String description;

    EntropyMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}