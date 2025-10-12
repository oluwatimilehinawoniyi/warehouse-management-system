package com.warehouse.common.exceptions;

import com.warehouse.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(
            RuntimeException ex,
            WebRequest request
    ) {
        log.warn("Not Found Exception: {} - {}", ex.getMessage(), request.getDescription(false));
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(
                new ErrorResponse(
                        ex.getMessage(),
                        request.getDescription(false),
                        status.value()),
                status
        );
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex, WebRequest request) {
        log.warn("Unauthorized Access Exception: {} - {}", ex.getMessage(),
                request.getDescription(false));
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(),
                        request.getDescription(false), status.value()),
                status
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation Exception: {} - {}", errors,
                request.getDescription(false));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse("Validation failed: " + errors,
                        request.getDescription(false), status.value()),
                status
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            WebRequest request
    ) {
        String message = String.format("Missing required parameter: '%s' of type %s",
                ex.getParameterName(),
                ex.getParameterType());

        log.warn("Missing parameter: {} - {}", message, request.getDescription(false));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(message, request.getDescription(false), status.value()),
                status
        );
    }

    @ExceptionHandler(InvalidCapacityException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCapacityExceptions(
            InvalidCapacityException ex,
            WebRequest request
    ) {
        String message = "Capacity must be an integer and must be positive";

        log.warn("Invalid argument capacity");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(message,
                        request.getDescription(false)
                        , status.value()),
                status
        );
    }

    @ExceptionHandler(CapacityReductionNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleCapacityReductionExceptions(
            CapacityReductionNotAllowedException ex,
            WebRequest request
    ) {
        String message = "Illegal capacity reduction";

        log.warn("Illegal capacity reduction");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(message,
                        request.getDescription(false)
                        , status.value()),
                status
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {} - {}", ex.getMessage(),
                request.getDescription(false), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(
                new ErrorResponse(
                        "An unexpected error occurred. Please try again later.",
                        request.getDescription(false), status.value()),
                status
        );
    }
}
