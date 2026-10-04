package com.polarbookshop.orderservice.order.web;

import java.util.HashMap;
import java.util.Map;

import com.polarbookshop.orderservice.book.BookClientException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class OrderControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBindException(WebExchangeBindException ex) {
        Map<String, String> errorsMap = new HashMap<>();
        ex.getFieldErrors().forEach(error -> {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorsMap;
    }

    @ExceptionHandler(BookClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookClientException(BookClientException ex) {
        Map<String, String> error = new HashMap<>();
        String reason = ex.getErrorResponse() != null ? ex.getErrorResponse().getFirstErrorMessage()
                : ex.getResponseBody();
        error.put("message", reason != null ? reason : ex.getMessage());
        return error;
    }

}
