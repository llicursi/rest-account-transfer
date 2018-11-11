package com.licursi.rest.transferservice.exceptions;

/**
 *  Throws whenever the balance reaches an unexpected condition.
 */
public class BalanceConstraintViolationException extends Exception {
    public BalanceConstraintViolationException(String message) {
        super(message);
    }
}
