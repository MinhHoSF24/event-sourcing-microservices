package com.microservices.bookservice.domain.model;

public record BookId(String value) {
    public BookId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Book id is required");
        }
    }

    public static BookId of(String value) {
        return new BookId(value);
    }
}
