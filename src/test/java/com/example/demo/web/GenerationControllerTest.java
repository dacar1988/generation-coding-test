package com.example.demo.web;

import com.example.demo.service.GenerationServiceImpl;
import com.example.demo.service.domain.GetGenerationFileResponse;
import com.example.demo.web.validator.FileValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

class GenerationControllerTest {

    private GenerationController underTest;

    private static final Long FILE_ID = 1L;
    @Mock
    private GenerationServiceImpl generationServiceImpl;
    private byte[] dummyByteArray;

    @BeforeEach
    public void beforeTest() {
        openMocks(this);
        dummyByteArray = new byte[1];
        underTest = new GenerationController(generationServiceImpl, new FileValidator(new ObjectMapper()));
    }

    @Test
    void shouldPostGenerationJsonFile() throws Exception {
        Path filePath = Path.of("src/test/java/resources/test.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(filePath)
        );
        GenerationFileResponse generationFileResponse = new GenerationFileResponse(FILE_ID, "generation_file", dummyByteArray);

        when(generationServiceImpl.uploadGenerationFile(multipartFile)).thenReturn(generationFileResponse);

        ResponseEntity<GenerationFileResponse> actual = underTest.uploadGenerationFile(multipartFile);

        assertEquals(CREATED, actual.getStatusCode());
        assertEquals(generationFileResponse, actual.getBody());
    }

    @Test
    void shouldGetGenerationJsonFile() throws Exception {
        Path filePath = Path.of("src/test/java/resources/test.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(filePath)
        );
        Long id = 1L;
        Resource resource = new ByteArrayResource(dummyByteArray);
        GetGenerationFileResponse getGenerationFileResponse = new GetGenerationFileResponse(FILE_ID, "generation_file", resource);

        when(generationServiceImpl.getGenerationFile(id)).thenReturn(getGenerationFileResponse);

        ResponseEntity<Resource> actual = underTest.getGenerationFile(id);
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentDispositionFormData("attachment", getGenerationFileResponse.getName());
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedHeaders.add("file-id", FILE_ID.toString());

        assertEquals(OK, actual.getStatusCode());
        assertEquals(resource, actual.getBody());
        assertEquals(expectedHeaders, actual.getHeaders());
    }

    @Test
    void shouldPutGenerationJsonFile() throws Exception {
        Path filePath = Path.of("src/test/java/resources/put_test.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(filePath)
        );
        GenerationFileResponse generationFileResponse = new GenerationFileResponse(FILE_ID, "generation_file", dummyByteArray);

        when(generationServiceImpl.updateGenerationFile(FILE_ID, multipartFile)).thenReturn(generationFileResponse);

        ResponseEntity<GenerationFileResponse> actual = underTest.updateGenerationFile(FILE_ID, multipartFile);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(generationFileResponse, actual.getBody());
    }

    @Test
    void shouldDeleteGenerationJsonFile() {
        doNothing().when(generationServiceImpl).deleteGenerationFile(FILE_ID);

        ResponseEntity<String> actual = underTest.deleteGenerationFile(FILE_ID);

        assertEquals(OK, actual.getStatusCode());
        assertEquals("File with id 1, deleted successfully", actual.getBody());
    }
}