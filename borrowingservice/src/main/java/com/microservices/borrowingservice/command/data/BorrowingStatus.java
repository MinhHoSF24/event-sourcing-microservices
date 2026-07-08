package com.microservices.borrowingservice.command.data;

public enum BorrowingStatus {
    PENDING,
    BOOK_RESERVED,
    APPROVED,
    REJECTED,
    COMPENSATED,
    RETURNED
}
