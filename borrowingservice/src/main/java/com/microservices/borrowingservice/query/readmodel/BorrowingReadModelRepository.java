package com.microservices.borrowingservice.query.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingReadModelRepository extends JpaRepository<BorrowingReadModel, String> {
}
