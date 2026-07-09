package com.microservices.userservice.command.aggregate;

import com.microservices.userservice.command.command.CreateUserCommand;
import com.microservices.userservice.command.command.DeleteUserCommand;
import com.microservices.userservice.command.command.UpdateUserCommand;
import com.microservices.userservice.command.event.UserCreatedEvent;
import com.microservices.userservice.command.event.UserDeletedEvent;
import com.microservices.userservice.command.event.UserUpdatedEvent;
import com.microservices.userservice.domain.model.DisplayName;
import com.microservices.userservice.domain.model.EmailAddress;
import com.microservices.userservice.domain.model.UserId;
import com.microservices.userservice.domain.model.UserUpdateProfile;
import com.microservices.userservice.domain.model.Username;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDate;

@Aggregate
@NoArgsConstructor
public class UserAggregate {
    @AggregateIdentifier
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String name;
    private boolean deleted;

    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        register(command);
    }

    @CommandHandler
    public void handle(UpdateUserCommand command) {
        ensureActive();
        UserUpdateProfile profile = UserUpdateProfile.of(
                command.getEmail(),
                command.getUsername(),
                command.getFirstName(),
                command.getLastName(),
                command.getDob(),
                command.getName());
        AggregateLifecycle.apply(new UserUpdatedEvent(
                UserId.of(command.getUserId()).value(),
                profile.email(),
                profile.username(),
                profile.firstName(),
                profile.lastName(),
                profile.dob(),
                profile.displayName()));
    }

    @CommandHandler
    public void handle(DeleteUserCommand command) {
        ensureActive();
        AggregateLifecycle.apply(new UserDeletedEvent(UserId.of(command.getUserId()).value()));
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.userId = event.getUserId();
        this.email = event.getEmail();
        this.username = event.getUsername();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.dob = event.getDob();
        this.name = event.getName();
        this.deleted = false;
    }

    @EventSourcingHandler
    public void on(UserUpdatedEvent event) {
        applyIfPresent(event);
    }

    @EventSourcingHandler
    public void on(UserDeletedEvent event) {
        this.userId = event.getUserId();
        this.deleted = true;
    }

    private void register(CreateUserCommand command) {
        AggregateLifecycle.apply(new UserCreatedEvent(
                UserId.of(command.getUserId()).value(),
                EmailAddress.of(command.getEmail()).value(),
                Username.of(command.getUsername()).value(),
                normalize(command.getFirstName()),
                normalize(command.getLastName()),
                command.getDob(),
                DisplayName.of(command.getName()).value()));
    }

    private void applyIfPresent(UserUpdatedEvent event) {
        if (event.getEmail() != null) {
            this.email = event.getEmail();
        }
        if (event.getUsername() != null) {
            this.username = event.getUsername();
        }
        if (event.getFirstName() != null) {
            this.firstName = event.getFirstName();
        }
        if (event.getLastName() != null) {
            this.lastName = event.getLastName();
        }
        if (event.getDob() != null) {
            this.dob = event.getDob();
        }
        if (event.getName() != null) {
            this.name = event.getName();
        }
    }

    private void ensureActive() {
        if (deleted) {
            throw new IllegalStateException("Deleted users cannot be changed");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
