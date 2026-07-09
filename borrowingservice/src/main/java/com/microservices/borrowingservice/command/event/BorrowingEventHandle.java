package com.microservices.borrowingservice.command.event;

import java.util.Optional;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.microservices.borrowingservice.domain.model.BorrowingStatus;
import com.microservices.borrowingservice.mapper.BorrowingMapper;
import com.microservices.borrowingservice.query.readmodel.BorrowingReadModel;
import com.microservices.borrowingservice.query.readmodel.BorrowingReadModelRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BorrowingEventHandle {
    private final BorrowingReadModelRepository borrowingRepository;
    private final BorrowingMapper borrowingMapper;

    public BorrowingEventHandle(BorrowingReadModelRepository borrowingRepository, BorrowingMapper borrowingMapper) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingMapper = borrowingMapper;
    }

    @EventHandler
    public void on(BorrowingCreatedEvent event) {
        BorrowingReadModel borrowing = borrowingMapper.toBorrowingReadModel(event);
        borrowingRepository.save(borrowing);
        log.info("event=BorrowingCreatedEvent borrowingId={} bookId={} employeeId={} status={}",
                event.getId(), event.getBookId(), event.getEmployeeId(), event.getStatus());
    }

    @EventHandler
    public void on(BorrowingDeletedEvent event) {
        Optional<BorrowingReadModel> oldEntity = borrowingRepository.findById(event.getId());
        oldEntity.ifPresent(borrowing -> borrowingRepository.delete(borrowing));
        log.info("event=BorrowingDeletedEvent borrowingId={}", event.getId());
    }

    @EventHandler
    public void on(BorrowingBookReservedEvent event) {
        updateStatus(event.getId(), BorrowingStatus.BOOK_RESERVED, "BorrowingBookReservedEvent");
    }

    @EventHandler
    public void on(BorrowingApprovedEvent event) {
        updateStatus(event.getId(), BorrowingStatus.APPROVED, "BorrowingApprovedEvent");
    }

    @EventHandler
    public void on(BorrowingRejectedEvent event) {
        updateStatus(event.getId(), BorrowingStatus.REJECTED, "BorrowingRejectedEvent");
    }

    @EventHandler
    public void on(BorrowingCompensatedEvent event) {
        updateStatus(event.getId(), BorrowingStatus.COMPENSATED, "BorrowingCompensatedEvent");
    }

    @EventHandler
    public void on(BorrowingReturnedEvent event) {
        BorrowingReadModel borrowing = borrowingRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));
        borrowing.setReturnDate(event.getReturnDate());
        borrowing.setStatus(BorrowingStatus.RETURNED);
        borrowingRepository.save(borrowing);
        log.info("event=BorrowingReturnedEvent borrowingId={} status={} returnDate={}",
                event.getId(), BorrowingStatus.RETURNED, event.getReturnDate());
    }

    private void updateStatus(String borrowingId, BorrowingStatus status, String eventName) {
        BorrowingReadModel borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));
        borrowing.setStatus(status);
        borrowingRepository.save(borrowing);
        log.info("event={} borrowingId={} status={}", eventName, borrowingId, status);
    }
}
