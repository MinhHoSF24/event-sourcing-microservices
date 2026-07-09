package com.microservices.borrowingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.query.model.BorrowingResponseModel;
import com.microservices.borrowingservice.query.readmodel.BorrowingReadModel;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {
    BorrowingCreatedEvent toBorrowingCreatedEvent(CreateBorrowingCommand command);

    @Mapping(target = "returnDate", ignore = true)
    BorrowingReadModel toBorrowingReadModel(BorrowingCreatedEvent event);

    BorrowingResponseModel toBorrowingResponseModel(BorrowingReadModel borrowing);
}
