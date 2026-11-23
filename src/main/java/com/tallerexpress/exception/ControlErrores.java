package com.tallerexpress.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/** Convierte las excepciones en respuestas JSON con el código HTTP correcto. */
@RestControllerAdvice
public class ControlErrores {

    private static final Logger LOG = LoggerFactory.getLogger(ControlErrores.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> negocio(ApiException e) {
        return cuerpo(e.getEstado(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumento(IllegalArgumentException e) {
        return cuerpo(400, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        String detalle = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return cuerpo(400, "Datos inválidos: " + detalle);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoIlegible(HttpMessageNotReadableException e) {
        return cuerpo(400, "El cuerpo de la petición no es un JSON válido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception e) {
        LOG.error("Error inesperado", e);
        return cuerpo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> cuerpo(int estado, String mensaje) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fecha", LocalDateTime.now(java.time.ZoneId.of("America/Bogota")).toString());
        m.put("estado", estado);
        m.put("error", mensaje);
        return ResponseEntity.status(estado).body(m);
    }
}
