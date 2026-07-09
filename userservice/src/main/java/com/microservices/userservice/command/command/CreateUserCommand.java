package com.microservices.userservice.command.command;

import com.microservices.userservice.domain.model.UserId;
import com.microservices.userservice.domain.model.UserRegistrationProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String name;

    public CreateUserCommand(
            String userId,
            String email,
            String username,
            String firstName,
            String lastName,
            LocalDate dob,
            String name) {
        this.userId = UserId.of(userId).value();
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.name = name;
    }

    public static CreateUserCommand from(String userId, UserRegistrationProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("User registration profile is required");
        }
        return new CreateUserCommand(
                userId,
                profile.email(),
                profile.username(),
                profile.firstName(),
                profile.lastName(),
                profile.dob(),
                profile.displayName());
    }
}
