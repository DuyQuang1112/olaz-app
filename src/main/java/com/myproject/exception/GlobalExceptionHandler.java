package com.myproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle specific exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        ResponseCustom<?> responseCustom = new ResponseCustom<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(responseCustom, HttpStatus.BAD_REQUEST);
    }

    // Handle IOException exception
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOExceptionException(IOException ex) {
        ResponseCustom<?> responseCustom = new ResponseCustom<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(responseCustom, HttpStatus.BAD_REQUEST);
    }

    // Handle specific exception
    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(CustomIllegalArgumentException ex) {
        ResponseCustom<?> responseCustom = new ResponseCustom<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(responseCustom, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseCustom<?>> handleGlobalException(Exception ex, WebRequest request) {
        ResponseCustom<?> responseCustom = new ResponseCustom<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(responseCustom, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseCustom<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ResponseCustom<?> responseCustom = new ResponseCustom<>(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(responseCustom, HttpStatus.BAD_REQUEST);
    }
}
