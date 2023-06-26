package com.example.demo.web;

import com.example.demo.error.EmptyFileException;
import com.example.demo.error.ErrorResponse;
import com.example.demo.error.GenerationFileNotFoundException;
import com.example.demo.error.WrongContentFileFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GlobalControllerAdviceTest {

    private GlobalControllerAdvice underTest;

    @Mock
    private ConstraintViolationException constraintViolationException;
    @Mock
    private ConstraintViolation constraintViolation;
    @BeforeEach
    public void beforeTest() {
        openMocks(this);
        underTest = new GlobalControllerAdvice();
    }

    @Test
    void handleMaxUploadSizeExceeded() {
        MaxUploadSizeExceededException maxUploadSizeExceededException = new MaxUploadSizeExceededException(1000L);
        ErrorResponse errorResponse = new ErrorResponse(maxUploadSizeExceededException.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleMaxUploadSizeExceeded(maxUploadSizeExceededException);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleIOException() {
        IOException ioException = new IOException("Generic IOException");
        ErrorResponse errorResponse = new ErrorResponse(ioException.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleIOException(ioException);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleEmptyFileException() {
        EmptyFileException emptyFileException = new EmptyFileException("The file is empty");
        ErrorResponse errorResponse = new ErrorResponse(emptyFileException.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleEmptyFileException(emptyFileException);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleWrongContentFileFormatException() {
        WrongContentFileFormatException wrongContentFileFormatException = new WrongContentFileFormatException("The file content is wrong");
        ErrorResponse errorResponse = new ErrorResponse(wrongContentFileFormatException.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleWrongContentFileFormatException(wrongContentFileFormatException);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleGenerationFileNotFoundException() {
        GenerationFileNotFoundException generationFileNotFoundException = new GenerationFileNotFoundException("Generation File Not Found");
        ErrorResponse errorResponse = new ErrorResponse(generationFileNotFoundException.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleGenerationFileNotFoundException(generationFileNotFoundException);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleConstraintViolationExceptions_ShouldReturnErrorResponse() {
        ConstraintViolationException ex = constraintViolationException;

        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);
        constraintViolations.add(constraintViolation);
        constraintViolations.add(constraintViolation);

        when(ex.getConstraintViolations()).thenReturn(constraintViolations);

        List<String> errorMessages = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        String expectedErrorMessage = String.join(",", errorMessages);

        ErrorResponse expectedErrorResponse = new ErrorResponse(expectedErrorMessage);

        GlobalControllerAdvice controllerAdvice = new GlobalControllerAdvice();

        ResponseEntity<ErrorResponse> responseEntity = controllerAdvice.handleConstraintViolationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expectedErrorResponse, responseEntity.getBody());
    }

    @Test
    void handleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "parameter", String.class, "invalid-value", null, new RuntimeException());
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleMethodArgumentTypeMismatch(exception);

        assertEquals(expectedErrorResponseEntity, actual);
    }

    @Test
    void handleGenericException() {
        Exception exception = new Exception("Generic Exception");
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

        ResponseEntity<ErrorResponse> expectedErrorResponseEntity = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);

        ResponseEntity<ErrorResponse> actual = underTest.handleGenericException(exception);

        assertEquals(expectedErrorResponseEntity, actual);
    }
}