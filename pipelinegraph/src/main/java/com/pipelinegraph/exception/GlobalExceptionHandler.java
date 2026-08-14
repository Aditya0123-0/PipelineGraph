package com.pipelinegraph.exception;

import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleDbUnavailable(ServiceUnavailableException ex) {
        ErrorResponse body = new ErrorResponse(
                "The graph database is currently unreachable. Please try again shortly.",
                "DB_UNAVAILABLE",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(DatabaseUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleWrappedDbUnavailable(DatabaseUnavailableException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), "DB_UNAVAILABLE", Instant.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NodeNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), "NOT_FOUND", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                "Something went wrong processing your request.",
                "INTERNAL_ERROR",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
