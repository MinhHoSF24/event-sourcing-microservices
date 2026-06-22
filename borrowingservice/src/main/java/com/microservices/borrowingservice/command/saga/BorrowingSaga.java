package com.microservices.borrowingservice.command.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;

import com.microservices.borrowingservice.command.command.DeleteBorrowingCommand;
import com.microservices.borrowingservice.command.event.BorrowingCreatedEvent;
import com.microservices.borrowingservice.command.event.BorrowingDeletedEvent;
import com.microservices.commonservice.command.RollbackStatusBookCommand;
import com.microservices.commonservice.command.UpdateStatusBookCommand;
import com.microservices.commonservice.event.BookRollbackStatusEvent;
import com.microservices.commonservice.event.BookUpdateStatusEvent;
import com.microservices.commonservice.model.BookResponseCommonModel;
import com.microservices.commonservice.model.EmployeeResponseCommonModel;
import com.microservices.commonservice.queries.GetBookDetailQuery;
import com.microservices.commonservice.queries.GetDetailEmployeeQuery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Saga
public class BorrowingSaga {
    // Axon's GenericSagaFactory instantiates the saga via a no-arg constructor,
    // then SpringResourceInjector wires these fields (it cannot do constructor
    // injection). Keep them as @Autowired fields and do NOT add a parameterized
    // constructor, or Axon throws SagaCreationException at @StartSaga.
    private transient final CommandGateway commandGateway;
    private transient final QueryGateway queryGateway;

    BorrowingSaga(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    void handle(BorrowingCreatedEvent event) {
        log.info("BorrowingCreatedEvent in saga for BookId: " + event.getBookId() + " : EmployeeId: "
                + event.getEmployeeId());
        try {
            GetBookDetailQuery getBookDetailQuery = new GetBookDetailQuery(event.getBookId());
            BookResponseCommonModel bookResponseCommonModel = queryGateway.query(getBookDetailQuery,
                    ResponseTypes.instanceOf(BookResponseCommonModel.class)).join();
            if (!bookResponseCommonModel.getIsReady()) {
                throw new Exception("Book is not ready for borrowing");
            } else {
                SagaLifecycle.associateWith("bookId", event.getBookId());
                UpdateStatusBookCommand command = new UpdateStatusBookCommand(event.getBookId(), false,
                        event.getEmployeeId(), event.getId());
                commandGateway.sendAndWait(command);
            }
        } catch (Exception ex) {
            rollbackBorrowingRecord(event.getId());
            log.error(ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    void handler(BookUpdateStatusEvent event) {
        log.info("BookUpdateStatusEvent in Saga for BookId : " + event.getBookId());
        try {
            GetDetailEmployeeQuery query = new GetDetailEmployeeQuery(event.getEmployeeId());
            EmployeeResponseCommonModel employeeModel = queryGateway
                    .query(query, ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)).join();
            if (employeeModel.getIsDisciplined()) {
                throw new Exception("Employee is disciplined and cannot borrow books");
            } else {
                log.info("Book borrowed successfully for BookId : " + event.getBookId() + " and EmployeeId : " + event.getEmployeeId());
                SagaLifecycle.end();
            }

        } catch (Exception ex) {
            rollBackBookStatus(event.getBookId(), event.getEmployeeId(), event.getBorrowingId());
            log.error(ex.getMessage());
        }

    }

    private void rollbackBorrowingRecord(String id) {
        DeleteBorrowingCommand command = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(command);
    }

    private void rollBackBookStatus(String bookId, String employeeId, String borrowingId) {
        SagaLifecycle.associateWith("bookId", bookId);
        RollbackStatusBookCommand command = new RollbackStatusBookCommand(bookId, true, employeeId, borrowingId);
        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = "bookId")
    void handle(BookRollbackStatusEvent event) {
        log.info("BookRollbackStatusEvent in Saga for book Id : {} " + event.getBookId());
        rollbackBorrowingRecord(event.getBorrowingId());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    void handle(BorrowingDeletedEvent event) {
        log.info("BorrowDeletedEvent in Saga for Borrowing Id : {} " +
                event.getId());
    }
}
