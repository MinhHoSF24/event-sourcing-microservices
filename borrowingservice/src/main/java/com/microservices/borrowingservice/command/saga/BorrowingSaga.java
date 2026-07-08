package com.microservices.borrowingservice.command.saga;

import com.microservices.borrowingservice.command.command.ApproveBorrowingCommand;
import com.microservices.borrowingservice.command.command.CompensateBorrowingCommand;
import com.microservices.borrowingservice.command.command.MarkBookReservedForBorrowingCommand;
import com.microservices.borrowingservice.command.command.RejectBorrowingCommand;
import com.microservices.borrowingservice.command.event.BorrowingApprovedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCompensatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingRejectedEvent;
import com.microservices.commonservice.command.ReleaseBookCommand;
import com.microservices.commonservice.command.ReserveBookCommand;
import com.microservices.commonservice.event.BookReleasedEvent;
import com.microservices.commonservice.event.BookReservedEvent;
import com.microservices.commonservice.model.BookResponseCommonModel;
import com.microservices.commonservice.model.EmployeeResponseCommonModel;
import com.microservices.commonservice.queries.GetBookDetailQuery;
import com.microservices.commonservice.queries.GetDetailEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Saga
public class BorrowingSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    public BorrowingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    void handle(BorrowingCreatedEvent event) {
        log.info("sagaStep=START event=BorrowingCreatedEvent borrowingId={} bookId={} employeeId={} status={}",
                event.getId(), event.getBookId(), event.getEmployeeId(), event.getStatus());
        try {
            BookResponseCommonModel book = queryGateway.query(
                    new GetBookDetailQuery(event.getBookId()),
                    ResponseTypes.instanceOf(BookResponseCommonModel.class)).join();

            if (!book.getIsReady()) {
                rejectBorrowing(event.getId(), "Book is not ready for borrowing");
                return;
            }

            SagaLifecycle.associateWith("bookId", event.getBookId());
            commandGateway.sendAndWait(new ReserveBookCommand(
                    event.getBookId(), false, event.getEmployeeId(), event.getId()));
        } catch (Exception ex) {
            rejectBorrowing(event.getId(), ex.getMessage());
            log.error("sagaStep=REJECT borrowingId={} reason={}", event.getId(), ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    void handle(BookReservedEvent event) {
        log.info("sagaStep=BOOK_RESERVED event=BookReservedEvent borrowingId={} bookId={} employeeId={}",
                event.getBorrowingId(), event.getBookId(), event.getEmployeeId());
        try {
            commandGateway.sendAndWait(new MarkBookReservedForBorrowingCommand(event.getBorrowingId()));

            EmployeeResponseCommonModel employee = queryGateway.query(
                    new GetDetailEmployeeQuery(event.getEmployeeId()),
                    ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)).join();

            if (employee.getIsDisciplined()) {
                releaseBook(event.getBookId(), event.getEmployeeId(), event.getBorrowingId(),
                        "Employee is disciplined and cannot borrow books");
                return;
            }

            commandGateway.sendAndWait(new ApproveBorrowingCommand(event.getBorrowingId()));
            log.info("sagaStep=APPROVE borrowingId={} bookId={} employeeId={}",
                    event.getBorrowingId(), event.getBookId(), event.getEmployeeId());
        } catch (Exception ex) {
            releaseBook(event.getBookId(), event.getEmployeeId(), event.getBorrowingId(), ex.getMessage());
            log.error("sagaStep=COMPENSATE borrowingId={} reason={}", event.getBorrowingId(), ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    void handle(BookReleasedEvent event) {
        log.info("sagaStep=BOOK_RELEASED event=BookReleasedEvent borrowingId={} bookId={} employeeId={}",
                event.getBorrowingId(), event.getBookId(), event.getEmployeeId());
        commandGateway.sendAndWait(new CompensateBorrowingCommand(
                event.getBorrowingId(),
                "Book released because borrowing failed validation"));
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    void handle(BorrowingApprovedEvent event) {
        log.info("sagaStep=END event=BorrowingApprovedEvent borrowingId={}", event.getId());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    void handle(BorrowingRejectedEvent event) {
        log.info("sagaStep=END event=BorrowingRejectedEvent borrowingId={} reason={}",
                event.getId(), event.getReason());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    void handle(BorrowingCompensatedEvent event) {
        log.info("sagaStep=END event=BorrowingCompensatedEvent borrowingId={} reason={}",
                event.getId(), event.getReason());
    }

    private void rejectBorrowing(String borrowingId, String reason) {
        commandGateway.sendAndWait(new RejectBorrowingCommand(borrowingId, reason));
    }

    private void releaseBook(String bookId, String employeeId, String borrowingId, String reason) {
        log.info("sagaStep=RELEASE_BOOK borrowingId={} bookId={} employeeId={} reason={}",
                borrowingId, bookId, employeeId, reason);
        SagaLifecycle.associateWith("bookId", bookId);
        commandGateway.sendAndWait(new ReleaseBookCommand(bookId, true, employeeId, borrowingId));
    }
}
