package com.microservices.borrowingservice.query.readmodel;

import com.microservices.borrowingservice.domain.model.BorrowingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "borrowing")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingReadModel {
    @Id
    private String id;

    private String bookId;
    private String employeeId;
    private Date borrowingDate;
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;
}
