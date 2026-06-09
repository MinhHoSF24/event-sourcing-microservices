package com.microservices.bookservice.query.projection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.microservices.bookservice.command.data.Book;
import com.microservices.bookservice.command.data.BookRepository;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.bookservice.query.queries.GetAllBookQuery;
import com.microservices.bookservice.query.queries.GetBookDetailQuery;

@Component
public class BookProjection {
    private final BookRepository bookRepository;

    public BookProjection(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @QueryHandler
    public List<BookResponseModel> handle(GetAllBookQuery query) {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(book -> {
            BookResponseModel responseModel = new BookResponseModel();
            BeanUtils.copyProperties(book, responseModel);
            return responseModel;
        }).collect(Collectors.toList());
    }

    @QueryHandler
    public BookResponseModel handle(GetBookDetailQuery query) throws Exception {

        BookResponseModel bookResponseModel = new BookResponseModel();
        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with BookId: "+ query.getId()));
        BeanUtils.copyProperties(book,bookResponseModel);
        return bookResponseModel;
    }
}
