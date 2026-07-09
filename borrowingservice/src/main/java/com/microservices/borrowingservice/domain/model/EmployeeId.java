package com.microservices.borrowingservice.domain.model;

public record EmployeeId(String value) {
    public EmployeeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Employee id is required");
        }
    }

    public static EmployeeId of(String value) {
        return new EmployeeId(value);
    }
}
