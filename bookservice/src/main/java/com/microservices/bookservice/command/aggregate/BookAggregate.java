package com.microservices.bookservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.DeleteBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookDeletedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.domain.model.AuthorName;
import com.microservices.bookservice.domain.model.BookId;
import com.microservices.bookservice.domain.model.BookTitle;
import com.microservices.bookservice.domain.policy.BookAvailabilityPolicy;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;

import lombok.NoArgsConstructor;

@Aggregate
@NoArgsConstructor
public class BookAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String author;
    private Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateBookCommand command) {
        addToCatalog(command);
    }

    @CommandHandler
    public void handle(UpdateBookCommand command) {
        updateCatalogDetails(command);
    }

    @CommandHandler
    public void handle(DeleteBookCommand command) {
        removeFromCatalog(command.getId());
    }

    @CommandHandler
    public void handle(ReserveBookCommand command) {
        reserveForBorrowing(command);
    }

    @CommandHandler
    public void handle(ReleaseBookCommand command) {
        releaseAfterFailedBorrowing(command);
    }

    @EventSourcingHandler
    public void on(BookReleasedEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookReservedEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookUpdatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }


    @EventSourcingHandler
    public void on(BookDeletedEvent event) {
        this.id = event.getId();
    }

    private void addToCatalog(CreateBookCommand command) {
        BookId bookId = BookId.of(command.getId());
        BookTitle title = BookTitle.of(command.getName());
        AuthorName author = AuthorName.of(command.getAuthor());
        BookAvailabilityPolicy.ensureAvailabilityKnown(command.getIsReady());

        AggregateLifecycle.apply(new BookCreatedEvent(
                bookId.value(),
                title.value(),
                author.value(),
                command.getIsReady()));
    }

    private void updateCatalogDetails(UpdateBookCommand command) {
        BookId bookId = BookId.of(command.getId());
        BookTitle title = BookTitle.of(command.getName());
        AuthorName author = AuthorName.of(command.getAuthor());

        // Availability changes are owned by reserve/release commands, not catalog editing.
        AggregateLifecycle.apply(new BookUpdatedEvent(bookId.value(), title.value(), author.value(), this.isReady));
    }

    private void removeFromCatalog(String bookId) {
        BookAvailabilityPolicy.ensureCanDelete(this.isReady);
        AggregateLifecycle.apply(new BookDeletedEvent(BookId.of(bookId).value()));
    }

    private void reserveForBorrowing(ReserveBookCommand command) {
        BookAvailabilityPolicy.ensureCanReserve(this.isReady);
        AggregateLifecycle.apply(new BookReservedEvent(
                BookId.of(command.getBookId()).value(),
                false,
                command.getEmployeeId(),
                command.getBorrowingId()));
    }

    private void releaseAfterFailedBorrowing(ReleaseBookCommand command) {
        BookAvailabilityPolicy.ensureCanRelease(this.isReady);
        AggregateLifecycle.apply(new BookReleasedEvent(
                BookId.of(command.getBookId()).value(),
                true,
                command.getEmployeeId(),
                command.getBorrowingId()));
    }
}
