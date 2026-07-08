package com.microservices.borrowingservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveBorrowingCommand {
    @TargetAggregateIdentifier
    private String id;
}
