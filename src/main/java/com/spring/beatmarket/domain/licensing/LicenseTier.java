package com.spring.beatmarket.domain.licensing;

import com.fasterxml.jackson.annotation.JsonValue;

enum LicenseTier {
    STANDARD("Standard"),
    COMMERCIAL("Commercial"),
    UNLIMITED("Unlimited");

    private final String displayName;

    LicenseTier(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}