package com.microservices.borrowingservice.command.event;

import java.util.Optional;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.microservices.borrowingservice.command.data.Borrowing;
import com.microservices.borrowingservice.command.data.BorrowingRepository;
import com.microservices.borrowingservice.mapper.BorrowingMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BorrowingEventHandle {
    private final BorrowingRepository borrowingRepository;
    private final BorrowingMapper borrowingMapper;

    public BorrowingEventHandle(BorrowingRepository borrowingRepository, BorrowingMapper borrowingMapper) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingMapper = borrowingMapper;
    }

    @EventHandler
    public void on(BorrowingCreatedEvent event) {
        Borrowing borrowing = borrowingMapper.toBorrowing(event);
        borrowingRepository.save(borrowing);
    }

    @EventHandler
    public void on(BorrowingDeletedEvent event) {
        Optional<Borrowing> oldEntity = borrowingRepository.findById(event.getId());
        oldEntity.ifPresent(borrowing -> borrowingRepository.delete(borrowing));
    }
}
