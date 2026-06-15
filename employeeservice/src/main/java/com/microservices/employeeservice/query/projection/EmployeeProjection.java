package com.microservices.employeeservice.query.projection;

import com.microservices.commonservice.model.EmployeeResponseCommonModel;
import com.microservices.employeeservice.command.data.Employee;
import com.microservices.employeeservice.command.data.EmployeeRepository;
import com.microservices.employeeservice.mapper.EmployeeMapper;
import com.microservices.employeeservice.query.model.EmployeeResponseModel;
import com.microservices.employeeservice.query.queries.GetAllEmployeeQuery;
import com.microservices.commonservice.queries.GetDetailEmployeeQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeProjection {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeProjection(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @QueryHandler
    public List<EmployeeResponseModel> handle(GetAllEmployeeQuery query){
        List<Employee> listEmployee = employeeRepository.findAllByIsDisciplined(query.getIsDisciplined());
        return listEmployee.stream().map(employeeMapper::toEmployeeResponseModel).collect(Collectors.toList());
    }

    @QueryHandler
    public EmployeeResponseCommonModel handle(GetDetailEmployeeQuery query) throws Exception{
        Employee employee = employeeRepository.findById(query.getId()).orElseThrow(() -> new Exception("Employee not found"));
        return employeeMapper.toEmployeeResponseCommonModel(employee);
    }
}