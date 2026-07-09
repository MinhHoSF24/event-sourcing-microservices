package com.microservices.userservice.domain.model;

public record Username(String value) {
    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        value = value.trim();
    }

    public static Username of(String value) {
        return new Username(value);
    }

    public static String optional(String value) {
        return value == null ? null : of(value).value();
    }
}
