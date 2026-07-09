package com.microservices.bookservice.domain.model;

public record BookTitle(String value) {
    public BookTitle {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Book title is required");
        }
    }

    public static BookTitle of(String value) {
        return new BookTitle(value.trim());
    }
}
