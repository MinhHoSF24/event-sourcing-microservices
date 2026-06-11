package com.microservices.bookservice.command.event;

import java.util.Optional;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.data.BookRepository;
import com.microservices.bookservice.mapper.BookMapper;

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
        bookRepository.save(bookMapper.toBook(event));
    }

    @EventHandler
    public void on(BookUpdatedEvent event) throws Exception {
        Optional<Book> oldBook = bookRepository.findById(event.getId());
        Book book = oldBook.orElseThrow(() -> new Exception("Book not found"));
        bookMapper.updateBookFromEvent(event, book);
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookDeletedEvent event) throws Exception {
        try {
            bookRepository.findById(event.getId()).orElseThrow(() -> new Exception("Book not found"));
            bookRepository.deleteById(event.getId());
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }
}
