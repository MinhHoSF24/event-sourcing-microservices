package com.microservices.borrowingservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.microservices.borrowingservice.domain.model.BorrowingStatus;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingCreatedEvent {
    private String id;
    private String bookId;
    private String employeeId;
    private Date borrowingDate;
    private BorrowingStatus status;
}
