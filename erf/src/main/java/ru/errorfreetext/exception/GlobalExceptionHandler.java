package ru.errorfreetext.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.errorfreetext.dto.ErrorResponse;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFound(TaskNotFoundException ex, HttpServletRequest request) {
        return response(ex.getMessage(), 40401, request);
    }

    @ExceptionHandler({InvalidTextException.class, MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse badRequest(Exception ex, HttpServletRequest request) {
        String message;
        if (ex instanceof MethodArgumentNotValidException validation) {
            message = validation.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            message = "Invalid value for parameter '" + mismatch.getName() + "'";
        } else if (ex instanceof HttpMessageNotReadableException) {
            message = "Malformed JSON request";
        } else {
            message = ex.getMessage();
        }
        return response(message, 40001, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse internal(Exception ex, HttpServletRequest request) {
        return response("Internal server error", 50001, request);
    }

    private ErrorResponse response(String message, int code, HttpServletRequest request) {
        return new ErrorResponse(message, code, OffsetDateTime.now(), request.getRequestURI());
    }
}
