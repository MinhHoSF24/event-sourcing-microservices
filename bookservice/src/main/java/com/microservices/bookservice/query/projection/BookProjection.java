package com.microservices.bookservice.query.projection;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.microservices.bookservice.mapper.BookMapper;
import com.microservices.bookservice.query.model.BookResponseModel;
import com.microservices.bookservice.query.queries.GetAllBookQuery;
import com.microservices.bookservice.query.readmodel.BookReadModel;
import com.microservices.bookservice.query.readmodel.BookReadModelRepository;
import com.microservices.commonservice.model.BookResponseCommonModel;
import com.microservices.commonservice.queries.GetBookDetailQuery;

@Component
public class BookProjection {
    private final BookReadModelRepository bookRepository;
    private final BookMapper bookMapper;

    public BookProjection(BookReadModelRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @QueryHandler
    public List<BookResponseModel> handle(GetAllBookQuery query) {
        List<BookReadModel> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toBookResponseModel).collect(Collectors.toList());
    }

    @QueryHandler
    public BookResponseCommonModel handle(GetBookDetailQuery query) throws Exception {

        BookReadModel book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with BookId: "+ query.getId()));
        return bookMapper.toBookResponseCommonModel(book);
    }
}
