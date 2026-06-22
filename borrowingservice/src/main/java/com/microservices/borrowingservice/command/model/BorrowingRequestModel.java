package com.microservices.borrowingservice.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequestModel {
    private String id;

    @NotBlank(message = "Book Id is mandatory")
    private String bookId;

    @NotBlank(message = "Employee Id is mandatory")
    private String employeeId;
}
