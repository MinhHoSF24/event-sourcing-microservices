package com.microservices.userservice.command.aggregate;

import com.microservices.userservice.command.command.CreateUserCommand;
import com.microservices.userservice.command.command.DeleteUserCommand;
import com.microservices.userservice.command.command.UpdateUserCommand;
import com.microservices.userservice.command.event.UserCreatedEvent;
import com.microservices.userservice.command.event.UserDeletedEvent;
import com.microservices.userservice.command.event.UserUpdatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class UserAggregateTest {
    private AggregateTestFixture<UserAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(UserAggregate.class);
    }

    @Test
    void createUserEmitsUserCreatedEvent() {
        LocalDate dob = LocalDate.of(1995, 1, 1);

        fixture.givenNoPriorActivity()
                .when(new CreateUserCommand(
                        "identity-user-1",
                        "user@example.com",
                        "user1",
                        "First",
                        "Last",
                        dob,
                        "First Last"))
                .expectEvents(new UserCreatedEvent(
                        "identity-user-1",
                        "user@example.com",
                        "user1",
                        "First",
                        "Last",
                        dob,
                        "First Last"));
    }

    @Test
    void updateUserEmitsUserUpdatedEvent() {
        fixture.given(new UserCreatedEvent(
                        "identity-user-1",
                        "user@example.com",
                        "user1",
                        "First",
                        "Last",
                        LocalDate.of(1995, 1, 1),
                        "First Last"))
                .when(new UpdateUserCommand(
                        "identity-user-1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Updated Name"))
                .expectEvents(new UserUpdatedEvent(
                        "identity-user-1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Updated Name"));
    }

    @Test
    void deleteUserEmitsUserDeletedEvent() {
        fixture.given(new UserCreatedEvent(
                        "identity-user-1",
                        "user@example.com",
                        "user1",
                        "First",
                        "Last",
                        LocalDate.of(1995, 1, 1),
                        "First Last"))
                .when(new DeleteUserCommand("identity-user-1"))
                .expectEvents(new UserDeletedEvent("identity-user-1"));
    }

    @Test
    void updateDeletedUserFails() {
        fixture.given(
                        new UserCreatedEvent(
                                "identity-user-1",
                                "user@example.com",
                                "user1",
                                "First",
                                "Last",
                                LocalDate.of(1995, 1, 1),
                                "First Last"),
                        new UserDeletedEvent("identity-user-1"))
                .when(new UpdateUserCommand(
                        "identity-user-1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Updated Name"))
                .expectException(IllegalStateException.class);
    }
}
