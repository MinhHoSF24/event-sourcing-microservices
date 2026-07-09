package com.microservices.userservice.query.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReadModelRepository extends JpaRepository<UserReadModel, Long> {
    Optional<UserReadModel> findByEmail(String email);

    Optional<UserReadModel> findByUserId(String userId);
}
