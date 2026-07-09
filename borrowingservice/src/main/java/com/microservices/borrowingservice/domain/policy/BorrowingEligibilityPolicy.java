package com.microservices.borrowingservice.domain.policy;

import java.util.Optional;

public final class BorrowingEligibilityPolicy {
    private BorrowingEligibilityPolicy() {
    }

    public static Optional<String> reasonBookCannotBeBorrowed(Boolean bookReady) {
        return Boolean.TRUE.equals(bookReady)
                ? Optional.empty()
                : Optional.of("Book is not ready for borrowing");
    }

    public static Optional<String> reasonEmployeeCannotBorrow(Boolean disciplined) {
        return Boolean.TRUE.equals(disciplined)
                ? Optional.of("Employee is disciplined and cannot borrow books")
                : Optional.empty();
    }
}
