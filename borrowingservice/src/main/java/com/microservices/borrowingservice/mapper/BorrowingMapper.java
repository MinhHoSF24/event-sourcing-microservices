package com.microservices.borrowingservice.mapper;

import org.mapstruct.Mapper;

import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.data.Borrowing;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {
    BorrowingCreatedEvent toBorrowingCreatedEvent(CreateBorrowingCommand command);

    Borrowing toBorrowing(BorrowingCreatedEvent event);
}
