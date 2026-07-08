package com.microservices.borrowingservice.command.saga;

import com.microservices.borrowingservice.command.command.ApproveBorrowingCommand;
import com.microservices.borrowingservice.command.command.CompensateBorrowingCommand;
import com.microservices.borrowingservice.command.command.MarkBookReservedForBorrowingCommand;
import com.microservices.borrowingservice.command.command.RejectBorrowingCommand;
import com.microservices.borrowingservice.command.data.BorrowingStatus;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;
import com.microservices.commonservice.model.BookResponseCommonModel;
import com.microservices.commonservice.model.EmployeeResponseCommonModel;
import com.microservices.commonservice.queries.GetBookDetailQuery;
import com.microservices.commonservice.queries.GetDetailEmployeeQuery;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BorrowingSagaTest {
    private static final Date BORROWING_DATE = new Date(1000L);

    private SagaTestFixture<BorrowingSaga> fixture;
    private QueryGateway queryGateway;

    @BeforeEach
    void setUp() {
        fixture = new SagaTestFixture<>(BorrowingSaga.class);
        queryGateway = mock(QueryGateway.class);
        fixture.registerResource(queryGateway);
    }

    @Test
    void unavailableBookRejectsBorrowing() {
        stubQueries(false, false);

        fixture.givenNoPriorActivity()
                .whenPublishingA(borrowingCreated())
                .expectDispatchedCommands(new RejectBorrowingCommand(
                        "borrowing-1", "Book is not ready for borrowing"));
    }

    @Test
    void availableBookReservesBook() {
        stubQueries(true, false);

        fixture.givenNoPriorActivity()
                .whenPublishingA(borrowingCreated())
                .expectDispatchedCommands(new ReserveBookCommand(
                        "book-1", false, "employee-1", "borrowing-1"));
    }

    @Test
    void reservedBookAndValidEmployeeApprovesBorrowing() {
        stubQueries(true, false);

        fixture.givenAPublished(borrowingCreated())
                .whenPublishingA(bookReserved())
                .expectDispatchedCommands(
                        new MarkBookReservedForBorrowingCommand("borrowing-1"),
                        new ApproveBorrowingCommand("borrowing-1"));
    }

    @Test
    void reservedBookAndDisciplinedEmployeeReleasesBook() {
        stubQueries(true, true);

        fixture.givenAPublished(borrowingCreated())
                .whenPublishingA(bookReserved())
                .expectDispatchedCommands(
                        new MarkBookReservedForBorrowingCommand("borrowing-1"),
                        new ReleaseBookCommand("book-1", true, "employee-1", "borrowing-1"));
    }

    @Test
    void releasedBookCompensatesBorrowing() {
        stubQueries(true, true);

        fixture.givenAPublished(borrowingCreated())
                .whenPublishingA(new BookReleasedEvent("book-1", true, "employee-1", "borrowing-1"))
                .expectDispatchedCommands(new CompensateBorrowingCommand(
                        "borrowing-1",
                        "Book released because borrowing failed validation"));
    }

    private void stubQueries(boolean bookReady, boolean employeeDisciplined) {
        when(queryGateway.query(any(), any(ResponseType.class))).thenAnswer(invocation -> {
            Object query = invocation.getArgument(0);
            if (query instanceof GetBookDetailQuery) {
                return CompletableFuture.completedFuture(new BookResponseCommonModel(
                        "book-1", "Clean Architecture", "Robert Martin", bookReady));
            }
            if (query instanceof GetDetailEmployeeQuery) {
                return CompletableFuture.completedFuture(new EmployeeResponseCommonModel(
                        "employee-1", "Jane", "Doe", "KIN-1", employeeDisciplined));
            }
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unexpected query: " + query));
        });
    }

    private BorrowingCreatedEvent borrowingCreated() {
        return new BorrowingCreatedEvent(
                "borrowing-1", "book-1", "employee-1", BORROWING_DATE, BorrowingStatus.PENDING);
    }

    private BookReservedEvent bookReserved() {
        return new BookReservedEvent("book-1", false, "employee-1", "borrowing-1");
    }
}
