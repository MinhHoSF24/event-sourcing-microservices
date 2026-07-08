package com.microservices.borrowingservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBorrowingCommand {
    @TargetAggregateIdentifier
    private String id;
    private Date returnDate;
}
