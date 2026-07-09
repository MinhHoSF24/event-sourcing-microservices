package com.microservices.bookservice.domain.exception;

public class DomainRuleViolationException extends IllegalStateException {
    public DomainRuleViolationException(String message) {
        super(message);
    }
}
