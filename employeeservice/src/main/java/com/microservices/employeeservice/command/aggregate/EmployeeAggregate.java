package com.microservices.employeeservice.command.aggregate;

import com.microservices.employeeservice.command.command.CreateEmployeeCommand;
import com.microservices.employeeservice.command.command.DeleteEmployeeCommand;
import com.microservices.employeeservice.command.command.UpdateEmployeeCommand;
import com.microservices.employeeservice.command.event.EmployeeCreatedEvent;
import com.microservices.employeeservice.command.event.EmployeeDeletedEvent;
import com.microservices.employeeservice.command.event.EmployeeUpdatedEvent;
import com.microservices.employeeservice.mapper.EmployeeMapper;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Aggregate
public class EmployeeAggregate {
    private static final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    @AggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDisciplined;

    @CommandHandler
    public EmployeeAggregate(CreateEmployeeCommand command) {
        AggregateLifecycle.apply(employeeMapper.toEmployeeCreatedEvent(command));
    }

    @CommandHandler
    public void handle(UpdateEmployeeCommand command) {
       AggregateLifecycle.apply(employeeMapper.toEmployeeUpdatedEvent(command));
    }

    @CommandHandler
    public void handle(DeleteEmployeeCommand command) {
        EmployeeDeletedEvent event = new EmployeeDeletedEvent(command.getId());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(EmployeeCreatedEvent event) {
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @EventSourcingHandler
    public void on(EmployeeUpdatedEvent event) {
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @EventSourcingHandler
    public void on(EmployeeDeletedEvent event) {
        this.id = event.getId();
    }
}