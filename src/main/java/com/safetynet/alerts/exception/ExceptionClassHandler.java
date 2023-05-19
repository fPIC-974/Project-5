package com.safetynet.alerts.exception;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class ExceptionClassHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(HttpServletRequest request,
                                                                     NotFoundException exception) {

        return new ResponseEntity<>(new JsonObject().toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsException(HttpServletRequest request,
                                                          AlreadyExistsException exception) {

        return new ResponseEntity<>(new JsonObject().toString(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidObjectParameterException.class)
    public ResponseEntity<String> handleInvalidObjectException(HttpServletRequest request,
                                                               InvalidObjectParameterException exception) {

        return new ResponseEntity<>(new JsonObject().toString(), HttpStatus.NOT_ACCEPTABLE);
    }
}
