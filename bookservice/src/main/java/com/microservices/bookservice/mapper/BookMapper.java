package com.microservices.bookservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.microservices.bookservice.command.command.CreateBookCommand;
import com.microservices.bookservice.command.command.UpdateBookCommand;
import com.microservices.bookservice.command.event.BookCreatedEvent;
import com.microservices.bookservice.command.event.BookUpdatedEvent;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.bookservice.query.readmodel.BookReadModel;
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

    @Mapping(target = "version", ignore = true)
    BookReadModel toBookReadModel(BookCreatedEvent event);

    @Mapping(target = "version", ignore = true)
    void updateBookFromEvent(BookUpdatedEvent event, @MappingTarget BookReadModel book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateStatusBookFromEvent(BookReservedEvent event, @MappingTarget BookReadModel book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "version", ignore = true)
    void rollBackStatusBookFromEvent(BookReleasedEvent event, @MappingTarget BookReadModel book);

    BookResponseModel toBookResponseModel(BookReadModel book);

    BookResponseCommonModel toBookResponseCommonModel(BookReadModel book);
}
