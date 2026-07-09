package com.microservices.borrowingservice.command.aggregate;

import com.microservices.borrowingservice.command.command.ApproveBorrowingCommand;
import com.microservices.borrowingservice.command.command.CompensateBorrowingCommand;
import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.command.MarkBookReservedForBorrowingCommand;
import com.microservices.borrowingservice.command.command.RejectBorrowingCommand;
import com.microservices.borrowingservice.command.command.ReturnBorrowingCommand;
import com.microservices.borrowingservice.command.event.BorrowingApprovedEvent;
import com.microservices.borrowingservice.command.event.BorrowingBookReservedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCompensatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingRejectedEvent;
import com.microservices.borrowingservice.command.event.BorrowingReturnedEvent;
import com.microservices.borrowingservice.domain.model.BorrowingStatus;
import com.microservices.borrowingservice.mapper.BorrowingMapper;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

class BorrowingAggregateTest {
    private static final Date BORROWING_DATE = new Date(1000L);
    private static final Date RETURN_DATE = new Date(2000L);

    private AggregateTestFixture<BorrowingAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(BorrowingAggregate.class);
        fixture.registerInjectableResource(Mappers.getMapper(BorrowingMapper.class));
    }

    @Test
    void createBorrowingEmitsPendingBorrowingCreatedEvent() {
        fixture.givenNoPriorActivity()
                .when(new CreateBorrowingCommand(
                        "borrowing-1", "book-1", "employee-1", BORROWING_DATE, BorrowingStatus.PENDING))
                .expectEvents(new BorrowingCreatedEvent(
                        "borrowing-1", "book-1", "employee-1", BORROWING_DATE, BorrowingStatus.PENDING));
    }

    @Test
    void pendingBorrowingCanMoveToBookReserved() {
        fixture.given(pendingBorrowingCreated())
                .when(new MarkBookReservedForBorrowingCommand("borrowing-1"))
                .expectEvents(new BorrowingBookReservedEvent("borrowing-1"));
    }

    @Test
    void bookReservedBorrowingCanBeApproved() {
        fixture.given(pendingBorrowingCreated(), new BorrowingBookReservedEvent("borrowing-1"))
                .when(new ApproveBorrowingCommand("borrowing-1"))
                .expectEvents(new BorrowingApprovedEvent("borrowing-1"));
    }

    @Test
    void pendingBorrowingCanBeRejected() {
        fixture.given(pendingBorrowingCreated())
                .when(new RejectBorrowingCommand("borrowing-1", "Book is not ready"))
                .expectEvents(new BorrowingRejectedEvent("borrowing-1", "Book is not ready"));
    }

    @Test
    void bookReservedBorrowingCanBeCompensated() {
        fixture.given(pendingBorrowingCreated(), new BorrowingBookReservedEvent("borrowing-1"))
                .when(new CompensateBorrowingCommand("borrowing-1", "Employee is disciplined"))
                .expectEvents(new BorrowingCompensatedEvent("borrowing-1", "Employee is disciplined"));
    }

    @Test
    void approvedBorrowingCanBeReturned() {
        fixture.given(
                        pendingBorrowingCreated(),
                        new BorrowingBookReservedEvent("borrowing-1"),
                        new BorrowingApprovedEvent("borrowing-1"))
                .when(new ReturnBorrowingCommand("borrowing-1", RETURN_DATE))
                .expectEvents(new BorrowingReturnedEvent("borrowing-1", RETURN_DATE));
    }

    @Test
    void rejectedBorrowingCannotBeReturned() {
        fixture.given(pendingBorrowingCreated(), new BorrowingRejectedEvent("borrowing-1", "Book is not ready"))
                .when(new ReturnBorrowingCommand("borrowing-1", RETURN_DATE))
                .expectException(IllegalStateException.class);
    }

    @Test
    void returnDateCannotBeBeforeBorrowingDate() {
        fixture.given(
                        pendingBorrowingCreated(),
                        new BorrowingBookReservedEvent("borrowing-1"),
                        new BorrowingApprovedEvent("borrowing-1"))
                .when(new ReturnBorrowingCommand("borrowing-1", new Date(500L)))
                .expectException(IllegalArgumentException.class);
    }

    private BorrowingCreatedEvent pendingBorrowingCreated() {
        return new BorrowingCreatedEvent(
                "borrowing-1", "book-1", "employee-1", BORROWING_DATE, BorrowingStatus.PENDING);
    }
}
