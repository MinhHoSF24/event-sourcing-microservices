package com.microservices.userservice.query.query;

public record GetUserByIdQuery(Long id) {
    public GetUserByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}
