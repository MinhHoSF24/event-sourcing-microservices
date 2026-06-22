package com.microservices.bookservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.commonservice.command.RollbackStatusBookCommand;
import com.microservices.commonservice.command.UpdateStatusBookCommand;
import com.microservices.commonservice.event.BookRollbackStatusEvent;
import com.microservices.commonservice.event.BookUpdateStatusEvent;
import com.microservices.commonservice.model.BookResponseCommonModel;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookCreatedEvent toBookCreatedEvent(CreateBookCommand command);

    BookUpdatedEvent toBookUpdatedEvent(UpdateBookCommand command);

    BookUpdateStatusEvent toBookUpdateStatusEvent(UpdateStatusBookCommand command);

    BookRollbackStatusEvent toBookRollbackStatusEvent(RollbackStatusBookCommand command);

    Book toBook(BookCreatedEvent event);

    void updateBookFromEvent(BookUpdatedEvent event, @MappingTarget Book book);

    void updateStatusBookFromEvent(BookUpdateStatusEvent event, @MappingTarget Book book);

    void rollBackStatusBookFromEvent(BookRollbackStatusEvent event, @MappingTarget Book book);

    BookResponseModel toBookResponseModel(Book book);

    BookResponseCommonModel toBookResponseCommonModel(Book book);
}