package com.microservices.borrowingservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.command.DeleteBorrowingCommand;
import com.microservices.borrowingservice.command.command.ApproveBorrowingCommand;
import com.microservices.borrowingservice.command.command.CompensateBorrowingCommand;
import com.microservices.borrowingservice.command.command.MarkBookReservedForBorrowingCommand;
import com.microservices.borrowingservice.command.command.RejectBorrowingCommand;
import com.microservices.borrowingservice.command.command.ReturnBorrowingCommand;
import com.microservices.borrowingservice.command.data.BorrowingStatus;
import com.microservices.borrowingservice.command.event.BorrowingApprovedEvent;
import com.microservices.borrowingservice.command.event.BorrowingBookReservedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCompensatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingDeletedEvent;
import com.microservices.borrowingservice.command.event.BorrowingRejectedEvent;
import com.microservices.borrowingservice.command.event.BorrowingReturnedEvent;
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
    private Date returnDate;
    private BorrowingStatus status;

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand command, BorrowingMapper borrowingMapper) {
        AggregateLifecycle.apply(borrowingMapper.toBorrowingCreatedEvent(command));
    }

    @CommandHandler
    public void handle(DeleteBorrowingCommand command) {
        BorrowingDeletedEvent event = new BorrowingDeletedEvent(command.getId());
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(MarkBookReservedForBorrowingCommand command) {
        ensureStatus(BorrowingStatus.PENDING, "Only pending borrowing can reserve a book");
        AggregateLifecycle.apply(new BorrowingBookReservedEvent(command.getId()));
    }

    @CommandHandler
    public void handle(ApproveBorrowingCommand command) {
        ensureStatus(BorrowingStatus.BOOK_RESERVED, "Only reserved borrowing can be approved");
        AggregateLifecycle.apply(new BorrowingApprovedEvent(command.getId()));
    }

    @CommandHandler
    public void handle(RejectBorrowingCommand command) {
        ensureStatus(BorrowingStatus.PENDING, "Only pending borrowing can be rejected");
        AggregateLifecycle.apply(new BorrowingRejectedEvent(command.getId(), command.getReason()));
    }

    @CommandHandler
    public void handle(CompensateBorrowingCommand command) {
        ensureStatus(BorrowingStatus.BOOK_RESERVED, "Only reserved borrowing can be compensated");
        AggregateLifecycle.apply(new BorrowingCompensatedEvent(command.getId(), command.getReason()));
    }

    @CommandHandler
    public void handle(ReturnBorrowingCommand command) {
        ensureStatus(BorrowingStatus.APPROVED, "Only approved borrowing can be returned");
        AggregateLifecycle.apply(new BorrowingReturnedEvent(command.getId(), command.getReturnDate()));
    }

    @EventSourcingHandler
    public void on(BorrowingCreatedEvent event) {
        this.id = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = event.getEmployeeId();
        this.borrowingDate = event.getBorrowingDate();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(BorrowingDeletedEvent event) {
        this.id = event.getId();
    }

    @EventSourcingHandler
    public void on(BorrowingBookReservedEvent event) {
        this.id = event.getId();
        this.status = BorrowingStatus.BOOK_RESERVED;
    }

    @EventSourcingHandler
    public void on(BorrowingApprovedEvent event) {
        this.id = event.getId();
        this.status = BorrowingStatus.APPROVED;
    }

    @EventSourcingHandler
    public void on(BorrowingRejectedEvent event) {
        this.id = event.getId();
        this.status = BorrowingStatus.REJECTED;
    }

    @EventSourcingHandler
    public void on(BorrowingCompensatedEvent event) {
        this.id = event.getId();
        this.status = BorrowingStatus.COMPENSATED;
    }

    @EventSourcingHandler
    public void on(BorrowingReturnedEvent event) {
        this.id = event.getId();
        this.returnDate = event.getReturnDate();
        this.status = BorrowingStatus.RETURNED;
    }

    private void ensureStatus(BorrowingStatus expected, String message) {
        if (this.status != expected) {
            throw new IllegalStateException(message);
        }
    }
}
