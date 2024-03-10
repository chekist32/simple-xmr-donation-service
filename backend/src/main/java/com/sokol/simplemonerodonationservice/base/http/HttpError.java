package com.sokol.simplemonerodonationservice.base.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class HttpError {
    private HttpError() { }

    public static Map<String, Object> createInvalidArgumentErrorResponseBody(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = createDefaultErrorResponseBody(ex, request, HttpStatus.BAD_REQUEST);
        Map<String, String> message = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            message.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        body.put("message", message);
        return body;
    }

    public static Map<String, Object> createDefaultErrorResponseBody(Exception ex, WebRequest request, HttpStatus httpStatus) {
        String message = ex.getMessage();
        String path = request.getDescription(false).split("=")[1];
        return createDefaultErrorResponseBody(message, path, httpStatus);
    }

    public static Map<String, Object> createDefaultErrorResponseBody(String message, String path, HttpStatus httpStatus) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Timestamp(System.currentTimeMillis()));
        body.put("message", message);
        body.put("path", path);
        body.put("status", httpStatus.value());
        body.put("error", httpStatus.getReasonPhrase());
        return body;
    }
}
