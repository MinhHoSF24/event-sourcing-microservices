package com.microservices.borrowingservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.microservices.borrowingservice.command.data.BorrowingStatus;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBorrowingCommand {

    @TargetAggregateIdentifier
    private String id;
    private String bookId;
    private String employeeId;
    private Date borrowingDate;
    private BorrowingStatus status;
}
