package com.licursi.rest.transferservice.account;

/**
 *  Throws whenever the balance reaches an unexpected condition.
 */
public class BalanceConstraintViolationException extends RuntimeException {
    public BalanceConstraintViolationException(String message) {
        super(message);
    }
}
