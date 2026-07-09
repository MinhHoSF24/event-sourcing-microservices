package com.microservices.bookservice.domain.policy;

import com.microservices.bookservice.domain.exception.DomainRuleViolationException;

public final class BookAvailabilityPolicy {
    private BookAvailabilityPolicy() {
    }

    public static void ensureAvailabilityKnown(Boolean ready) {
        if (ready == null) {
            throw new DomainRuleViolationException("Book availability is required");
        }
    }

    public static void ensureCanReserve(Boolean ready) {
        if (!Boolean.TRUE.equals(ready)) {
            throw new DomainRuleViolationException("Book is not ready for borrowing");
        }
    }

    public static void ensureCanRelease(Boolean ready) {
        if (Boolean.TRUE.equals(ready)) {
            throw new DomainRuleViolationException("Book is already available");
        }
    }

    public static void ensureCanDelete(Boolean ready) {
        if (Boolean.FALSE.equals(ready)) {
            throw new DomainRuleViolationException("Cannot delete a reserved book");
        }
    }
}
