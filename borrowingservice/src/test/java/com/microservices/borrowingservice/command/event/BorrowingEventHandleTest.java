package com.microservices.borrowingservice.command.event;

import com.microservices.borrowingservice.command.data.Borrowing;
import com.microservices.borrowingservice.command.data.BorrowingRepository;
import com.microservices.borrowingservice.command.data.BorrowingStatus;
import com.microservices.borrowingservice.mapper.BorrowingMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BorrowingEventHandleTest {

    @Test
    void approvedEventUpdatesBorrowingReadModelStatus() {
        BorrowingRepository borrowingRepository = mock(BorrowingRepository.class);
        BorrowingMapper borrowingMapper = mock(BorrowingMapper.class);
        Borrowing borrowing = new Borrowing();
        borrowing.setId("borrowing-1");
        borrowing.setStatus(BorrowingStatus.BOOK_RESERVED);
        when(borrowingRepository.findById("borrowing-1")).thenReturn(Optional.of(borrowing));

        BorrowingEventHandle handler = new BorrowingEventHandle(borrowingRepository, borrowingMapper);
        handler.on(new BorrowingApprovedEvent("borrowing-1"));

        assertThat(borrowing.getStatus()).isEqualTo(BorrowingStatus.APPROVED);
        verify(borrowingRepository).save(borrowing);
    }
}
