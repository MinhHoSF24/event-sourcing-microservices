package com.microservices.userservice.domain.policy;

import com.microservices.userservice.domain.model.UserRegistrationProfile;
import org.springframework.util.StringUtils;

public final class UserRegistrationPolicy {
    private UserRegistrationPolicy() {
    }

    public static void validate(UserRegistrationProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("User registration profile is required");
        }
        requireText(profile.email(), "Email is required");
        requireText(profile.username(), "Username is required");
        requireText(profile.displayName(), "Display name is required");
        requireText(profile.password(), "Password is required");
    }

    private static void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
    }
}
