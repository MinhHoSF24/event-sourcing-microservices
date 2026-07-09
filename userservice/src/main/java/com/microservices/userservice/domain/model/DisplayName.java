package com.microservices.userservice.domain.model;

public record DisplayName(String value) {
    public DisplayName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Display name is required");
        }
        value = value.trim();
    }

    public static DisplayName of(String value) {
        return new DisplayName(value);
    }

    public static String optional(String value) {
        return value == null ? null : of(value).value();
    }
}
