package com.microservices.borrowingservice.query.projection;

import com.microservices.borrowingservice.mapper.BorrowingMapper;
import com.microservices.borrowingservice.query.model.BorrowingResponseModel;
import com.microservices.borrowingservice.query.queries.GetAllBorrowingsQuery;
import com.microservices.borrowingservice.query.queries.GetBorrowingDetailQuery;
import com.microservices.borrowingservice.query.readmodel.BorrowingReadModel;
import com.microservices.borrowingservice.query.readmodel.BorrowingReadModelRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BorrowingProjection {
    private final BorrowingReadModelRepository borrowingRepository;
    private final BorrowingMapper borrowingMapper;

    public BorrowingProjection(BorrowingReadModelRepository borrowingRepository, BorrowingMapper borrowingMapper) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingMapper = borrowingMapper;
    }

    @QueryHandler
    public List<BorrowingResponseModel> handle(GetAllBorrowingsQuery query) {
        List<BorrowingReadModel> borrowings = borrowingRepository.findAll();
        return borrowings.stream().map(borrowingMapper::toBorrowingResponseModel).collect(Collectors.toList());
    }

    @QueryHandler
    public BorrowingResponseModel handle(GetBorrowingDetailQuery query) throws Exception {
        BorrowingReadModel borrowing = borrowingRepository.findById(query.getId())
                .orElseThrow(() -> new Exception("Borrowing not found with borrowingId: " + query.getId()));
        return borrowingMapper.toBorrowingResponseModel(borrowing);
    }
}
