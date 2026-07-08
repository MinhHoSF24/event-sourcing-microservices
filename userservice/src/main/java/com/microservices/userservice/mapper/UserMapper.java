package com.microservices.userservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.microservices.userservice.dto.CreateUserRequestDTO;
import com.microservices.userservice.dto.UpdateUserRequestDTO;
import com.microservices.userservice.dto.UserResponseDTO;
import com.microservices.userservice.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // DTO -> Entity
    User toUser(CreateUserRequestDTO dto);

    // Partial update: ignore null fields so unset DTO fields keep the existing entity value.
    // id and userId are identity/managed fields and must never be overwritten by an update.
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequestDTO dto);

    // Entity -> DTO
    UserResponseDTO toUserResponseDTO(User user);
}
