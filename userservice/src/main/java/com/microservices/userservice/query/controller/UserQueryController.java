package com.microservices.userservice.query.controller;

import com.microservices.userservice.query.model.UserResponseModel;
import com.microservices.userservice.query.query.GetAllUsersQuery;
import com.microservices.userservice.query.query.GetUserByIdQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ResponseEntity<List<UserResponseModel>> getAllUsers() {
        return ResponseEntity.ok(queryGateway
                .query(new GetAllUsersQuery(), ResponseTypes.multipleInstancesOf(UserResponseModel.class))
                .join());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseModel> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(queryGateway
                .query(new GetUserByIdQuery(id), ResponseTypes.instanceOf(UserResponseModel.class))
                .join());
    }
}
