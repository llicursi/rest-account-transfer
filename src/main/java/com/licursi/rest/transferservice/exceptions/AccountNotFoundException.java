package com.licursi.rest.transferservice.exceptions;

/**
 * Throws on any attempt of using negative value on Account operations
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
