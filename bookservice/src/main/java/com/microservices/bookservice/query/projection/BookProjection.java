package com.microservices.bookservice.query.projection;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.data.BookRepository;
import com.microservices.bookservice.mapper.BookMapper;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.bookservice.query.queries.GetAllBookQuery;
import com.microservices.bookservice.query.queries.GetBookDetailQuery;

@Component
public class BookProjection {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookProjection(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @QueryHandler
    public List<BookResponseModel> handle(GetAllBookQuery query) {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toBookResponseModel).collect(Collectors.toList());
    }

    @QueryHandler
    public BookResponseModel handle(GetBookDetailQuery query) throws Exception {

        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with BookId: "+ query.getId()));
        return bookMapper.toBookResponseModel(book);
    }
}
