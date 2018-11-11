package com.licursi.rest.transferservice.exceptions;

/**
 * Throws on any attempt of using negative value on Account operations
 */
public class NegativeConstraintViolationException extends Exception {

    public NegativeConstraintViolationException(String message) {
        super(message);
    }
}
