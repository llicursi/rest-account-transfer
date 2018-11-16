package com.licursi.rest.transferservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throws on any attempt of using negative value or zero value on Account operations
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PositiveValueViolationException extends Exception {

    private static final long serialVersionUID = 4760963348906594536L;

    public PositiveValueViolationException(String message) {
        super(message);
    }
}
