package com.microservices.borrowingservice.command.controller;

import java.util.Date;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.borrowingservice.command.command.CreateBorrowingCommand;
import com.microservices.borrowingservice.command.model.BorrowingRequestModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/borrowings")
public class BorrowingCommandController {
    private final CommandGateway commandGateway;

    public BorrowingCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String addBorrowing(@Valid @RequestBody BorrowingRequestModel model) {
        CreateBorrowingCommand command = new CreateBorrowingCommand(
                UUID.randomUUID().toString(),
                model.getBookId(),
                model.getEmployeeId(),
                new Date());
        return commandGateway.sendAndWait(command);
    }
}
