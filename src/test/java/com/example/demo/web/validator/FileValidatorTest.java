package com.example.demo.web.validator;

import com.example.demo.error.EmptyFileException;
import com.example.demo.error.WrongContentFileFormatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private FileValidator underTest;

    @BeforeEach
    public void beforeTest() {
        underTest = new FileValidator(new ObjectMapper());
    }

    @Test
    void shouldThrowWrongFileFormatExceptionIfTheFileHasTheWrongFormat() throws IOException {
        Path filePath = Path.of("src/test/java/resources/wrong_format.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(filePath)
        );

        var exception = assertThrows(WrongContentFileFormatException.class, () -> underTest.validateFile(multipartFile));
        assertEquals("Error deserializing the file", exception.getMessage());
    }

    @Test
    void shouldThrowEmptyFileExceptionIfTheFileIsEmpty() throws IOException {
        Path filePath = Path.of("src/test/java/resources/empty.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(filePath)
        );

        var exception = assertThrows(EmptyFileException.class, () -> underTest.validateFile(multipartFile));
        assertEquals("The file is empty", exception.getMessage());
    }
}