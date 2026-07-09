package com.microservices.userservice.query.projection;

import com.microservices.userservice.mapper.UserMapper;
import com.microservices.userservice.query.model.UserResponseModel;
import com.microservices.userservice.query.query.GetAllUsersQuery;
import com.microservices.userservice.query.query.GetUserByIdQuery;
import com.microservices.userservice.query.readmodel.UserReadModel;
import com.microservices.userservice.query.readmodel.UserReadModelRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProjection {
    private final UserReadModelRepository userRepository;
    private final UserMapper userMapper;

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUsersQuery query) {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseModel)
                .toList();
    }

    @QueryHandler
    public UserResponseModel handle(GetUserByIdQuery query) {
        UserReadModel user = userRepository.findById(query.id())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + query.id()));
        return userMapper.toUserResponseModel(user);
    }
}
