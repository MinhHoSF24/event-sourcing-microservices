package com.microservices.userservice.domain.model;

import org.springframework.util.StringUtils;

public record UserId(String value) {
    public static UserId of(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("User identity id is required");
        }
        return new UserId(value.trim());
    }
}
