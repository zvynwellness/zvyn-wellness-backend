package com.homycarev1.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validationError(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), readableMessage(error.getField(), error.getDefaultMessage())));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "The submitted information is invalid");
        response.put("fieldErrors", fieldErrors);
        return response;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> duplicateValue() {
        return Map.of("status", HttpStatus.CONFLICT.value(),
                "message", "That service slug is already in use. Enter a different slug or edit the existing service.");
    }

    private String readableMessage(String field, String fallback) {
        return switch (field) {
            case "fullName" -> "Please enter your full name.";
            case "email" -> "Please enter a valid email address.";
            case "password" -> "Password must contain between 10 and 72 characters.";
            default -> fallback == null ? "This field is invalid." : fallback;
        };
    }
}
