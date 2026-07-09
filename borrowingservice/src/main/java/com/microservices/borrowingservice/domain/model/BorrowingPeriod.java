package com.microservices.borrowingservice.domain.model;

import java.util.Date;

public record BorrowingPeriod(Date borrowingDate, Date returnDate) {
    public BorrowingPeriod {
        if (borrowingDate == null) {
            throw new IllegalArgumentException("Borrowing date is required");
        }
        if (returnDate != null && returnDate.before(borrowingDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrowing date");
        }
    }

    public static BorrowingPeriod starting(Date borrowingDate) {
        return new BorrowingPeriod(borrowingDate, null);
    }

    public static BorrowingPeriod of(Date borrowingDate, Date returnDate) {
        return new BorrowingPeriod(borrowingDate, returnDate);
    }
}
