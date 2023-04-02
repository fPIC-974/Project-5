package com.safetynet.alerts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends Exception {
    private final String msg;
    public AlreadyExistsException(String message) {
        super(message);
        this.msg = message;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }
}
