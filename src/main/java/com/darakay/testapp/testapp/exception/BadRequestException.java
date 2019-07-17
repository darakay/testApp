package com.darakay.testapp.testapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Authorization header value")
public class BadRequestException extends Exception {
    public BadRequestException() {
        super();
    }
}
