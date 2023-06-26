package com.example.demo.service;

import com.example.demo.dao.GenerationDao;
import com.example.demo.dao.domain.GenerationFileEntity;
import com.example.demo.dao.domain.GenerationFileContentResult;
import com.example.demo.error.GenerationFileNotFoundException;
import com.example.demo.error.WrongContentFileFormatException;
import com.example.demo.web.GenerationFileResponse;
import com.example.demo.service.domain.GetGenerationFileResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class GenerationServiceImplTest {

    private GenerationService underTest;

    @Mock
    private GenerationDao generationDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long FILE_ID = 1L;
    private byte[] dummyByteArray;


    @BeforeEach
    public void beforeTest() {
        openMocks(this);
        dummyByteArray = new byte[1];
        objectMapper = new ObjectMapper();
        underTest = new GenerationServiceImpl(generationDao, objectMapper);
    }

    @Test
    void shouldUploadGenerationFile() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/test.json");
        MockMultipartFile fileToUpload = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(fileToUploadPath)
        );

        GenerationFileEntity generationFileEntity = new GenerationFileEntity(FILE_ID, "generation_file", dummyByteArray);
        GenerationFileResponse generationFileResponse = new GenerationFileResponse(FILE_ID, "generation_file", dummyByteArray);

        when(generationDao.save(any())).thenReturn(generationFileEntity);

        GenerationFileResponse actual = underTest.uploadGenerationFile(fileToUpload);

        verify(generationDao).save(any());
        assertEquals(generationFileResponse, actual);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfTheFileIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () -> underTest.uploadGenerationFile(null));
        assertEquals("The file cannot be null", exception.getMessage());
        verify(generationDao, never()).save(any());
    }

    @Test
    void shouldRetrieveGenerationFile() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/test.json");
        GenerationFileEntity getGenerationFileEntity = new GenerationFileEntity(FILE_ID, "generation_file", Files.readAllBytes(fileToUploadPath));

        GenerationFileContentResult expectedGenerationFileContentResult = getExpectedGenerationFileResultFromResource();

        when(generationDao.findById(FILE_ID)).thenReturn(Optional.of(getGenerationFileEntity));

        GetGenerationFileResponse actual = underTest.getGenerationFile(FILE_ID);

        GenerationFileContentResult actualGenerationFileContentResult = getActualGenerationFileResult(actual);

        verify(generationDao).findById(FILE_ID);
        assertEquals(expectedGenerationFileContentResult, actualGenerationFileContentResult);
        assertEquals(FILE_ID, actual.getId());
        assertEquals(getGenerationFileEntity.getName(), actual.getName());
    }

    @Test
    void shouldThrowWrongContentFileFormatExceptionWhenRetrievedGenerationFileIsWrong() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/wrong_format.json");
        GenerationFileEntity getGenerationFileEntity = new GenerationFileEntity(FILE_ID, "generation_file", Files.readAllBytes(fileToUploadPath));

        when(generationDao.findById(FILE_ID)).thenReturn(Optional.of(getGenerationFileEntity));

        WrongContentFileFormatException exception = assertThrows(
                WrongContentFileFormatException.class,
                () -> underTest.getGenerationFile(FILE_ID)
        );

        assertEquals("Error deserializing the file", exception.getMessage());
    }

    @Test
    void getGenerationFileshouldThrowFileNotFoundExceptionWhenFileIsNotPresent() {
        when(generationDao.findById(FILE_ID)).thenReturn(Optional.empty());

        GenerationFileNotFoundException exception = assertThrows(GenerationFileNotFoundException.class, () -> underTest.getGenerationFile(FILE_ID));

        assertEquals("File with id: " + FILE_ID + " not found", exception.getMessage());
    }

    @Test
    void postFileShouldThrowIllegalArgumentExceptionIfTheFileIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () -> underTest.getGenerationFile(null));
        assertEquals("The file id cannot be null", exception.getMessage());
        verify(generationDao, never()).findById(any());
    }

    @Test
    void shouldUpdateGenerationFile() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/put_test.json");
        MockMultipartFile fileToUpload = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(fileToUploadPath)
        );

        GenerationFileEntity generationFileEntity = new GenerationFileEntity(FILE_ID, "generation_file", dummyByteArray);
        GenerationFileResponse generationFileResponse = new GenerationFileResponse(FILE_ID, "generation_file", dummyByteArray);

        when(generationDao.findById(FILE_ID)).thenReturn(Optional.of(generationFileEntity));
        when(generationDao.save(any())).thenReturn(generationFileEntity);

        GenerationFileResponse actual = underTest.updateGenerationFile(FILE_ID, fileToUpload);

        verify(generationDao).findById(FILE_ID);
        verify(generationDao).save(any());
        assertEquals(generationFileResponse, actual);
    }

    @Test
    void updateGenerationFileShouldThrowFileNotFoundExceptionWhenFileIsNotPresent() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/put_test.json");
        MockMultipartFile fileToUpload = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(fileToUploadPath)
        );
        when(generationDao.findById(FILE_ID)).thenReturn(Optional.empty());

        GenerationFileNotFoundException exception = assertThrows(
                GenerationFileNotFoundException.class,
                () -> underTest.updateGenerationFile(FILE_ID, fileToUpload)
        );

        assertEquals("File with id: " + FILE_ID + " not found", exception.getMessage());
        verify(generationDao, never()).delete(any());
    }

    @Test
    void updateFileShouldThrowIllegalArgumentExceptionIfTheFileIdIsNull() throws IOException {
        Path fileToUploadPath = Path.of("src/test/java/resources/put_test.json");
        MockMultipartFile fileToUpload = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                Files.readAllBytes(fileToUploadPath)
        );
        var exception = assertThrows(IllegalArgumentException.class, () -> underTest.updateGenerationFile(null, fileToUpload));
        assertEquals("The file id cannot be null", exception.getMessage());
        verify(generationDao, never()).findById(any());
    }

    @Test
    void updateFileShouldThrowIllegalArgumentExceptionIfTheFileIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () -> underTest.updateGenerationFile(FILE_ID, null));
        assertEquals("The file cannot be null", exception.getMessage());
        verify(generationDao, never()).findById(any());
    }

    @Test
    void shouldDeleteGenerationFile() {
        GenerationFileEntity generationFileEntity = new GenerationFileEntity();
        when(generationDao.findById(FILE_ID)).thenReturn(Optional.of(generationFileEntity));

        doNothing().when(generationDao).delete(generationFileEntity);

        underTest.deleteGenerationFile(FILE_ID);

        verify(generationDao).delete(generationFileEntity);
    }

    @Test
    void shouldThrowFileNotFoundExceptionWhenDeletingGenerationFileIfFileNotFound() {
        when(generationDao.findById(FILE_ID)).thenReturn(Optional.empty());

        GenerationFileNotFoundException exception = assertThrows(
                GenerationFileNotFoundException.class,
                () -> underTest.deleteGenerationFile(FILE_ID)
        );

        assertEquals("File with id: " + FILE_ID + " not found", exception.getMessage());
        verify(generationDao, never()).delete(new GenerationFileEntity());
    }

    private GenerationFileContentResult getActualGenerationFileResult(GetGenerationFileResponse actual) throws IOException {
        String actualGenerationFileResultJsonContent = new String(actual.getResource().getContentAsByteArray(), StandardCharsets.UTF_8);
        return objectMapper.readValue(actualGenerationFileResultJsonContent, GenerationFileContentResult.class);
    }

    private GenerationFileContentResult getExpectedGenerationFileResultFromResource() throws IOException {
        Resource resultResource = new ByteArrayResource(Files.readAllBytes(Path.of("src/test/java/resources/result.json")));

        return objectMapper.readValue(
                resultResource.getContentAsByteArray(),
                GenerationFileContentResult.class
        );
    }
}