package com.licursi.rest.transferservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throws whenever the balance reaches an unexpected condition.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BalanceConstraintViolationException extends Exception {
    private static final long serialVersionUID = -5483777273652169678L;

    public BalanceConstraintViolationException(String message) {
        super(message);
    }
}
