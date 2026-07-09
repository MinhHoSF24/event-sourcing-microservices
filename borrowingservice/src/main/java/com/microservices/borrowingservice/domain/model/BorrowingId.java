package com.microservices.borrowingservice.domain.model;

public record BorrowingId(String value) {
    public BorrowingId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Borrowing id is required");
        }
    }

    public static BorrowingId of(String value) {
        return new BorrowingId(value);
    }
}
