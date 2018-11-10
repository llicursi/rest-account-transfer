package com.licursi.rest.transferservice.exceptions;

/**
 * Throws whenever the balance reaches an unexpected condition.
 */
public class NegativeConstraintViolationException extends RuntimeException {
    public NegativeConstraintViolationException(String message) {
        super(message);
    }
}
