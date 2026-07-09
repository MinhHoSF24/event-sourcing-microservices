package com.microservices.borrowingservice.domain.policy;

import com.microservices.borrowingservice.domain.exception.DomainRuleViolationException;
import com.microservices.borrowingservice.domain.model.BorrowingStatus;

public final class BorrowingLifecyclePolicy {
    private BorrowingLifecyclePolicy() {
    }

    public static void ensureNewBorrowingStartsPending(BorrowingStatus status) {
        if (status != BorrowingStatus.PENDING) {
            throw new DomainRuleViolationException("Borrowing must start as pending");
        }
    }

    public static void ensureStatus(BorrowingStatus actual, BorrowingStatus expected, String message) {
        if (actual != expected) {
            throw new DomainRuleViolationException(message);
        }
    }
}
