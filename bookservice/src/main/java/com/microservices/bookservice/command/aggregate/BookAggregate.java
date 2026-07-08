package com.microservices.bookservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.DeleteBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookDeletedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.mapper.BookMapper;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;

import org.mapstruct.factory.Mappers;

import lombok.NoArgsConstructor;

@Aggregate
@NoArgsConstructor
public class BookAggregate {
    private static final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @AggregateIdentifier
    private String id;
    private String name;
    private String author;
    private Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateBookCommand command) {
        AggregateLifecycle.apply(bookMapper.toBookCreatedEvent(command));
    }

    @CommandHandler
    public void handle(UpdateBookCommand command) {
        AggregateLifecycle.apply(bookMapper.toBookUpdatedEvent(command));
    }

    @CommandHandler
    public void handle(DeleteBookCommand command) {
        if (Boolean.FALSE.equals(this.isReady)) {
            throw new IllegalStateException("Cannot delete a reserved book");
        }
        BookDeletedEvent bookDeletedEvent = new BookDeletedEvent(command.getId());
        AggregateLifecycle.apply(bookDeletedEvent);
    }

    @CommandHandler
    public void handle(ReserveBookCommand command) {
        if (Boolean.FALSE.equals(this.isReady)) {
            throw new IllegalStateException("Book is not ready for borrowing");
        }
        AggregateLifecycle.apply(bookMapper.toBookReservedEvent(command));
    }

    @CommandHandler
    public void handle(ReleaseBookCommand command) {
        if (Boolean.TRUE.equals(this.isReady)) {
            throw new IllegalStateException("Book is already available");
        }
        AggregateLifecycle.apply(bookMapper.toBookReleasedEvent(command));
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
}
