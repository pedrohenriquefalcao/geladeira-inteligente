package com.independente.geladeira_inteligente.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ApiValidationException exception) {
        log.warn("Validação rejeitada: {}", exception.getMessage());
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage());
    }

    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ApiNotFoundException exception) {
        log.warn("Recurso não encontrado: {}", exception.getMessage());
        return response(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(HttpMessageNotReadableException exception) {
        String message = "JSON inválido. Confira nome, categoria, quantidade e validade.";
        if (exception.getCause() instanceof InvalidFormatException invalidFormat
                && invalidFormat.getTargetType().isEnum()) {
            message = "Categoria inválida. Use uma das categorias disponíveis no formulário.";
        }

        log.warn("Requisição JSON inválida: {}", exception.getMostSpecificCause().getMessage());
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Parâmetro inválido '{}': {}", exception.getName(), exception.getValue());
        return response(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER",
                "O parâmetro '" + exception.getName() + "' possui um formato inválido.");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "code", code,
                "message", message
        ));
    }
}
