package com.microservices.bookservice.domain.model;

public record AuthorName(String value) {
    public AuthorName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Author name is required");
        }
    }

    public static AuthorName of(String value) {
        return new AuthorName(value.trim());
    }
}
