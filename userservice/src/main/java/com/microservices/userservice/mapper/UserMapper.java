package com.microservices.userservice.mapper;

import com.microservices.userservice.command.event.UserCreatedEvent;
import com.microservices.userservice.command.event.UserUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.microservices.userservice.domain.model.UserRegistrationProfile;
import com.microservices.userservice.query.model.UserResponseModel;
import com.microservices.userservice.query.readmodel.UserReadModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "displayName", target = "name")
    UserReadModel toUserReadModel(UserRegistrationProfile profile);

    @Mapping(target = "id", ignore = true)
    UserReadModel toUserReadModel(UserCreatedEvent event);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromEvent(UserUpdatedEvent event, @MappingTarget UserReadModel user);

    UserResponseModel toUserResponseModel(UserReadModel user);
}
