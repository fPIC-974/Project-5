package com.safetynet.alerts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

// FIXME

@ControllerAdvice
public class RestErrorHandler {
    @ExceptionHandler(AlertsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object processValidationError(AlertsException ex) {
        String result = ex.getErrorMessage();
        System.out.println("###########"+result);
//        return ex;
        return ex.toString();
    }
}
