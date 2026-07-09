package com.microservices.userservice.command.command;

import com.microservices.userservice.domain.model.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@NoArgsConstructor
public class DeleteUserCommand {
    @TargetAggregateIdentifier
    private String userId;

    public DeleteUserCommand(String userId) {
        this.userId = UserId.of(userId).value();
    }
}
