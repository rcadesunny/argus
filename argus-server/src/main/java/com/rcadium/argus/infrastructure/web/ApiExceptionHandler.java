package com.rcadium.argus.infrastructure.web;

import com.rcadium.argus.application.exception.ArgusException;
import com.rcadium.argus.client.ErrorCode;
import com.rcadium.argus.client.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ArgusException.class)
    public ResponseEntity<ErrorResponse> handleArgus(ArgusException exception) {
        String traceId = newTraceId();
        HttpStatus status = statusFor(exception.code());

        log.warn("[{}] {} - {}", traceId, exception.code(), exception.getMessage());

        return ResponseEntity.status(status)
                .body(body(exception.code(), exception.getMessage(), traceId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String traceId = newTraceId();

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        log.warn("[{}] validation failed - {}", traceId, message);

        return ResponseEntity.badRequest()
                .body(body(ErrorCode.VALIDATION_FAILED, message, traceId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        String traceId = newTraceId();

        log.error("[{}] unexpected failure", traceId, exception);

        return ResponseEntity.internalServerError()
                .body(body(ErrorCode.INTERNAL_ERROR, "Something went wrong", traceId));
    }

    private HttpStatus statusFor(ErrorCode code) {
        return switch (code) {
            case INVALID_CREDENTIALS, INVALID_REFRESH_TOKEN -> HttpStatus.UNAUTHORIZED;
            case ACCOUNT_NOT_ACTIVE -> HttpStatus.FORBIDDEN;
            case EMAIL_ALREADY_REGISTERED -> HttpStatus.CONFLICT;
            case VALIDATION_FAILED -> HttpStatus.BAD_REQUEST;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ErrorResponse body(ErrorCode code, String message, String traceId) {
        return new ErrorResponse(code.name(), message, traceId, clock.instant());
    }

    private String newTraceId() {
        return UUID.randomUUID().toString();
    }
}
