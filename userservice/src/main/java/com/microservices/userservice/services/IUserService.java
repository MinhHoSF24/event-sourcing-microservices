package com.microservices.userservice.services;

import com.microservices.userservice.dto.CreateUserRequestDTO;
import com.microservices.userservice.dto.LoginRequestDTO;
import com.microservices.userservice.dto.UpdateUserRequestDTO;
import com.microservices.userservice.dto.UserResponseDTO;
import com.microservices.userservice.dto.identities.TokenExchangeResponse;

import java.util.List;

public interface IUserService {
    UserResponseDTO createUser(CreateUserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto);
    void deleteUser(Long id);
    TokenExchangeResponse login(LoginRequestDTO dto);
}