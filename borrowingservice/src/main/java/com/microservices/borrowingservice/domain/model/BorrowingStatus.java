package com.microservices.borrowingservice.domain.model;

public enum BorrowingStatus {
    PENDING,
    BOOK_RESERVED,
    APPROVED,
    REJECTED,
    COMPENSATED,
    RETURNED
}
