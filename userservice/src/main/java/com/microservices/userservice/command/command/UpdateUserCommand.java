package com.microservices.userservice.command.command;

import com.microservices.userservice.command.model.UpdateUserRequestModel;
import com.microservices.userservice.domain.model.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserCommand {
    @TargetAggregateIdentifier
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String name;

    public UpdateUserCommand(
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

    public static UpdateUserCommand from(String userId, UpdateUserRequestModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Update user request is required");
        }
        return new UpdateUserCommand(
                userId,
                model.getEmail(),
                model.getUsername(),
                model.getFirstName(),
                model.getLastName(),
                model.getDob(),
                model.getName());
    }
}
