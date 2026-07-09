package com.microservices.userservice.domain.policy;

import com.microservices.userservice.domain.model.UserRegistrationProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRegistrationPolicyTest {

    @Test
    void registrationRequiresValidEmail() {
        assertThatThrownBy(() -> UserRegistrationProfile.of(
                "invalid-email", "minh", "Minh", "Nguyen", null, "Minh Nguyen", "secret"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email must be valid");
    }

    @Test
    void registrationRequiresPassword() {
        UserRegistrationProfile profile = UserRegistrationProfile.of(
                "minh@example.com", "minh", "Minh", "Nguyen", null, "Minh Nguyen", " ");

        assertThatThrownBy(() -> UserRegistrationPolicy.validate(profile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password is required");
    }
}
