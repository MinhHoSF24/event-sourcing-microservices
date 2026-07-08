package com.microservices.borrowingservice.query.controller;

import com.microservices.borrowingservice.query.model.BorrowingResponseModel;
import com.microservices.borrowingservice.query.queries.GetAllBorrowingsQuery;
import com.microservices.borrowingservice.query.queries.GetBorrowingDetailQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowings")
public class BorrowingQueryController {
    private final QueryGateway queryGateway;

    public BorrowingQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<BorrowingResponseModel> getAllBorrowings() {
        return queryGateway.query(
                new GetAllBorrowingsQuery(),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)).join();
    }

    @GetMapping("/{borrowingId}")
    public BorrowingResponseModel getBorrowingDetail(@PathVariable String borrowingId) {
        return queryGateway.query(
                new GetBorrowingDetailQuery(borrowingId),
                ResponseTypes.instanceOf(BorrowingResponseModel.class)).join();
    }
}
