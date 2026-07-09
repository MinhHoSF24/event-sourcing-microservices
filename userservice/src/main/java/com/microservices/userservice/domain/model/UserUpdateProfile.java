package com.microservices.userservice.domain.model;

import java.time.LocalDate;

public record UserUpdateProfile(
        String email,
        String username,
        String firstName,
        String lastName,
        LocalDate dob,
        String displayName) {

    public static UserUpdateProfile of(
            String email,
            String username,
            String firstName,
            String lastName,
            LocalDate dob,
            String displayName) {
        return new UserUpdateProfile(
                EmailAddress.optional(email),
                Username.optional(username),
                normalize(firstName),
                normalize(lastName),
                dob,
                DisplayName.optional(displayName));
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
