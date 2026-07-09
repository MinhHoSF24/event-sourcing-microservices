package com.microservices.userservice.domain.model;

import java.time.LocalDate;

public record UserRegistrationProfile(
        String email,
        String username,
        String firstName,
        String lastName,
        LocalDate dob,
        String displayName,
        String password) {

    public static UserRegistrationProfile of(
            String email,
            String username,
            String firstName,
            String lastName,
            LocalDate dob,
            String displayName,
            String password) {
        return new UserRegistrationProfile(
                EmailAddress.of(email).value(),
                Username.of(username).value(),
                normalize(firstName),
                normalize(lastName),
                dob,
                DisplayName.of(displayName).value(),
                password);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
