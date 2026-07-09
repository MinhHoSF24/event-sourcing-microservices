package com.microservices.bookservice.command.aggregate;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.DeleteBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookAggregateTest {
    private AggregateTestFixture<BookAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(BookAggregate.class);
    }

    @Test
    void createBookEmitsBookCreatedEvent() {
        fixture.givenNoPriorActivity()
                .when(new CreateBookCommand("book-1", "Domain-Driven Design", "Eric Evans", true))
                .expectEvents(new BookCreatedEvent("book-1", "Domain-Driven Design", "Eric Evans", true));
    }

    @Test
    void reserveAvailableBookEmitsBookReservedEvent() {
        fixture.given(new BookCreatedEvent("book-1", "Clean Architecture", "Robert Martin", true))
                .when(new ReserveBookCommand("book-1", false, "employee-1", "borrowing-1"))
                .expectEvents(new BookReservedEvent("book-1", false, "employee-1", "borrowing-1"));
    }

    @Test
    void reserveUnavailableBookFails() {
        fixture.given(new BookCreatedEvent("book-1", "Clean Architecture", "Robert Martin", false))
                .when(new ReserveBookCommand("book-1", false, "employee-1", "borrowing-1"))
                .expectException(IllegalStateException.class);
    }

    @Test
    void releaseReservedBookEmitsBookReleasedEvent() {
        fixture.given(
                        new BookCreatedEvent("book-1", "Clean Architecture", "Robert Martin", true),
                        new BookReservedEvent("book-1", false, "employee-1", "borrowing-1"))
                .when(new ReleaseBookCommand("book-1", true, "employee-1", "borrowing-1"))
                .expectEvents(new BookReleasedEvent("book-1", true, "employee-1", "borrowing-1"));
    }

    @Test
    void deleteReservedBookFails() {
        fixture.given(
                        new BookCreatedEvent("book-1", "Clean Architecture", "Robert Martin", true),
                        new BookReservedEvent("book-1", false, "employee-1", "borrowing-1"))
                .when(new DeleteBookCommand("book-1"))
                .expectException(IllegalStateException.class);
    }

    @Test
    void catalogUpdateCannotChangeAvailability() {
        fixture.given(new BookCreatedEvent("book-1", "Clean Architecture", "Robert Martin", true))
                .when(new UpdateBookCommand("book-1", "Clean Architecture 2", "Robert C. Martin", false))
                .expectEvents(new BookUpdatedEvent("book-1", "Clean Architecture 2", "Robert C. Martin", true));
    }
}
