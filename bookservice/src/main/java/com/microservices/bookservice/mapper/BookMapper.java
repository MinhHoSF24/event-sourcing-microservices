package com.microservices.bookservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;
import com.microservices.commonservice.model.BookResponseCommonModel;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookCreatedEvent toBookCreatedEvent(CreateBookCommand command);

    BookUpdatedEvent toBookUpdatedEvent(UpdateBookCommand command);

    BookReservedEvent toBookReservedEvent(ReserveBookCommand command);

    BookReleasedEvent toBookReleasedEvent(ReleaseBookCommand command);

    Book toBook(BookCreatedEvent event);

    void updateBookFromEvent(BookUpdatedEvent event, @MappingTarget Book book);

    void updateStatusBookFromEvent(BookReservedEvent event, @MappingTarget Book book);

    void rollBackStatusBookFromEvent(BookReleasedEvent event, @MappingTarget Book book);

    BookResponseModel toBookResponseModel(Book book);

    BookResponseCommonModel toBookResponseCommonModel(Book book);
}
