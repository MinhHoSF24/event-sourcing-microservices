package com.microservices.borrowingservice.domain.exception;

public class DomainRuleViolationException extends IllegalStateException {
    public DomainRuleViolationException(String message) {
        super(message);
    }
}
