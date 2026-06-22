package com.microservices.borrowingservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.command.DeleteBorrowingCommand;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingDeletedEvent;
import com.microservices.borrowingservice.mapper.BorrowingMapper;

import lombok.NoArgsConstructor;

import java.util.Date;

@Aggregate
@NoArgsConstructor
public class BorrowingAggregate {
    @AggregateIdentifier
    private String id;
    private String bookId;
    private String employeeId;
    private Date borrowingDate;

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand command, BorrowingMapper borrowingMapper) {
        AggregateLifecycle.apply(borrowingMapper.toBorrowingCreatedEvent(command));
    }

    @CommandHandler
    public void handle(DeleteBorrowingCommand command) {
        BorrowingDeletedEvent event = new BorrowingDeletedEvent(command.getId());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BorrowingCreatedEvent event) {
        this.id = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = event.getEmployeeId();
        this.borrowingDate = event.getBorrowingDate();
    }

    @EventSourcingHandler
    public void on(BorrowingDeletedEvent event) {
        this.id = event.getId();
    }
}
