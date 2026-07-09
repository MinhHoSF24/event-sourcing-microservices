package com.microservices.userservice.domain.model;

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        value = value.trim().toLowerCase();
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }

    public static String optional(String value) {
        return value == null ? null : of(value).value();
    }
}
