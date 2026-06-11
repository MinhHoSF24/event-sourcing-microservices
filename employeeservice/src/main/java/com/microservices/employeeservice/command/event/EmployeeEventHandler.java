package com.microservices.employeeservice.command.event;

import com.microservices.employeeservice.command.data.Employee;
import com.microservices.employeeservice.command.data.EmployeeRepository;
import com.microservices.employeeservice.mapper.EmployeeMapper;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class EmployeeEventHandler {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeEventHandler(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @EventHandler
    public void on(EmployeeCreatedEvent event){
        Employee employee = employeeMapper.toEmployee(event);
        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeUpdatedEvent event) throws Exception{
        Optional<Employee> oldEmployee = employeeRepository.findById(event.getId());
        Employee employee = oldEmployee.orElseThrow(() -> new Exception("Employee not found"));
        employeeMapper.updateEmployeeFromEvent(event, employee);
        employeeRepository.save(employee);
    }

    @EventHandler
    //Prevent replaying of delete events to avoid accidental deletion of employees when rebuilding the read model.
    @DisallowReplay
    public void on(EmployeeDeletedEvent event) throws Exception {
        try {
            employeeRepository.findById(event.getId()).orElseThrow(() -> new Exception("Employee not found"));
            employeeRepository.deleteById(event.getId());
        }catch (Exception ex){
            log.error(ex.getMessage());
        }

    }
}