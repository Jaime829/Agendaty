package com.proyecto.agendaty.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void validationExceptionMapeaCamposYResumen() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "registerRequest");
        bindingResult.addError(new FieldError("registerRequest", "correo", "El correo es obligatorio"));
        bindingResult.addError(new FieldError("registerRequest", "documento", "El documento es obligatorio"));

        MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidationExceptions(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().errorsDetails().contains("Validation failed for object='registerRequest'"));
        assertTrue(response.getBody().errorsDetails().contains("Error count: 2"));
        assertTrue(response.getBody().errorsDetails().contains("correo: El correo es obligatorio"));
        assertTrue(response.getBody().errorsDetails().contains("documento: El documento es obligatorio"));
        assertTrue(response.getBody().errorMessage().contains("registerRequest"));
    }

    @Test
    void resourceNotFoundException_devuelve404() {
        var response = handler.handleResourceNotFoundException(new ResourceNotFoundException("Turno 1 no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Turno 1 no encontrado", response.getBody().errorsDetails());
    }

    @Test
    void badRequestException_devuelve400() {
        var response = handler.handleBadRequestException(new BadRequestException("Algo inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("Algo inválido", response.getBody().errorsDetails());
    }

    @Test
    void dataIntegrityViolation_devuelve409() {
        var response = handler.handleDataIntegrityViolation(
                new org.springframework.dao.DataIntegrityViolationException("duplicate key"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
    }
}
