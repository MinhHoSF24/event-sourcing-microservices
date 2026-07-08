package com.microservices.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.userservice.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
}