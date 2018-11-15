package com.licursi.rest.transferservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throws on any attempt of using negative value on Account operations
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotFoundException extends Exception {

    private static final long serialVersionUID = -3828143694667187727L;

    private Long accountId;

    public AccountNotFoundException(String message, Long accountId) {
        super(message + ": " + accountId);
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }
}
