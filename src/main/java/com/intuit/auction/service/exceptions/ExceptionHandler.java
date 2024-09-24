package com.intuit.auction.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    public ResponseEntity<String> handleAccountException(AccountException accountException) {
        return new ResponseEntity<>(accountException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> handlerGenericException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
