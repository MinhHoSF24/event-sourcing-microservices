package com.microservices.bookservice.command.event;

import java.util.Optional;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.data.BookRepository;
import com.microservices.bookservice.mapper.BookMapper;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BookEventHandle {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookEventHandle(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @EventHandler
    public void on(BookCreatedEvent event) {
        log.info("event=BookCreatedEvent bookId={} isReady={}", event.getId(), event.getIsReady());
        bookRepository.save(bookMapper.toBook(event));
    }

    @EventHandler
    public void on(BookUpdatedEvent event) throws Exception {
        Optional<Book> oldBook = bookRepository.findById(event.getId());
        Book book = oldBook.orElseThrow(() -> new Exception("Book not found"));
        bookMapper.updateBookFromEvent(event, book);
        bookRepository.save(book);
        log.info("event=BookUpdatedEvent bookId={} isReady={}", event.getId(), event.getIsReady());
    }

    @EventHandler
    public void on(BookReservedEvent event) {
        Optional<Book> oldBook = bookRepository.findById(event.getBookId());
        Book book = oldBook.orElseThrow(() -> new RuntimeException("Book not found"));
        bookMapper.updateStatusBookFromEvent(event, book);
        bookRepository.save(book);
        log.info("event=BookReservedEvent bookId={} borrowingId={} employeeId={} isReady={}",
                event.getBookId(), event.getBorrowingId(), event.getEmployeeId(), event.getIsReady());
    }

    @EventHandler
    public void on(BookDeletedEvent event) throws Exception {
        try {
            bookRepository.findById(event.getId()).orElseThrow(() -> new Exception("Book not found"));
            bookRepository.deleteById(event.getId());
            log.info("event=BookDeletedEvent bookId={}", event.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @EventHandler
    public void on(BookReleasedEvent event) {
        Optional<Book> oldBook = bookRepository.findById(event.getBookId());
        Book book = oldBook.orElseThrow(() -> new RuntimeException("Book not found"));
        bookMapper.rollBackStatusBookFromEvent(event, book);
        bookRepository.save(book);
        log.info("event=BookReleasedEvent bookId={} borrowingId={} employeeId={} isReady={}",
                event.getBookId(), event.getBorrowingId(), event.getEmployeeId(), event.getIsReady());
    }
}
