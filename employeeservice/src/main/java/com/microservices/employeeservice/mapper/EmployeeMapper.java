package com.microservices.employeeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.microservices.employeeservice.command.command.CreateEmployeeCommand;
import com.microservices.employeeservice.command.command.UpdateEmployeeCommand;
import com.microservices.employeeservice.command.data.Employee;
import com.microservices.employeeservice.command.event.EmployeeCreatedEvent;
import com.microservices.employeeservice.command.event.EmployeeUpdatedEvent;
import com.microservices.employeeservice.query.model.EmployeeResponseModel;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeCreatedEvent toEmployeeCreatedEvent(CreateEmployeeCommand command);

    EmployeeUpdatedEvent toEmployeeUpdatedEvent(UpdateEmployeeCommand command);

    Employee toEmployee(EmployeeCreatedEvent event);

    void updateEmployeeFromEvent(EmployeeUpdatedEvent event, @MappingTarget Employee employee);

    EmployeeResponseModel toEmployeeResponseModel(Employee employee);
}
