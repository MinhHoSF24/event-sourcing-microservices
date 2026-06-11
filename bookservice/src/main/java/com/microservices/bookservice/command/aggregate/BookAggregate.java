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
import com.microservices.bookservice.mapper.BookMapper;
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
        // Implement delete logic here, e.g., apply a BookDeletedEvent
        BookDeletedEvent bookDeletedEvent = new BookDeletedEvent(command.getId());
        AggregateLifecycle.apply(bookDeletedEvent);
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
