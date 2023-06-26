package com.example.demo.web.validator;

import com.example.demo.dao.domain.GenerationFileContent;
import com.example.demo.error.EmptyFileException;
import com.example.demo.error.WrongContentFileFormatException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Class that validates if the file is empty or if the format of the file in input is not valid.
 */
@Component
@AllArgsConstructor
public class FileValidator {
    private ObjectMapper objectMapper;

    /**
     * Validate if the file is empty or if the format of the file in input is not valid.
     * @param file to be validated.
     * @throws Exception
     */
    public void validateFile(MultipartFile file) throws Exception {
        validateEmptyFile(file);
        validateGenerationFileContent(file);
    }
    private void validateEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("The file is empty");
        }
    }
    private void validateGenerationFileContent(MultipartFile file) throws IOException {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            objectMapper.readValue(file.getBytes(), GenerationFileContent.class);
        } catch (UnrecognizedPropertyException ex) {
            throw new WrongContentFileFormatException("Error deserializing the file");
        }
    }
}
