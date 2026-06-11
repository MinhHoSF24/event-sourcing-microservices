package com.microservices.bookservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.query.model.BookResponseModel;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookCreatedEvent toBookCreatedEvent(CreateBookCommand command);

    BookUpdatedEvent toBookUpdatedEvent(UpdateBookCommand command);

    Book toBook(BookCreatedEvent event);

    void updateBookFromEvent(BookUpdatedEvent event, @MappingTarget Book book);

    BookResponseModel toBookResponseModel(Book book);
}