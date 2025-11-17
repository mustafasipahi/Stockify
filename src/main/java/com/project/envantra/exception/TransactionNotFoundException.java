package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.TRANSACTION_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TransactionNotFoundException extends EnvantraRuntimeException {

    public TransactionNotFoundException() {
        super(TRANSACTION_NOT_FOUND, NOT_FOUND, "Transaction not found!");
    }

    public TransactionNotFoundException(Long transactionId) {
        super(TRANSACTION_NOT_FOUND, NOT_FOUND, "Transaction " + transactionId + " not found!");
    }
}
