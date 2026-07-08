package com.microservices.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequestDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String name;

    private String password;
}