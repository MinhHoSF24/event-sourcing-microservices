package com.microservices.bookservice.query.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReadModelRepository extends JpaRepository<BookReadModel, String> {
}
