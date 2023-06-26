package com.example.demo.service;

import com.example.demo.dao.GenerationDao;
import com.example.demo.dao.domain.GenerationFileContent;
import com.example.demo.dao.domain.GenerationFileEntity;
import com.example.demo.dao.domain.GenerationFileContentResult;
import com.example.demo.error.GenerationFileNotFoundException;
import com.example.demo.error.WrongContentFileFormatException;
import com.example.demo.web.GenerationFileResponse;
import com.example.demo.service.domain.GetGenerationFileResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.util.Assert.notNull;

/**
 * Service implementation to manage the uploaded file.
 */
@AllArgsConstructor
@Service
@Slf4j
public class GenerationServiceImpl implements GenerationService {

    private GenerationDao generationDao;
    private ObjectMapper objectMapper;

    /**
     * Upload a json file with a given format and saves it in a database.
     * @param file file to be saved.
     * @return a GenerationFileResponse with the info about the file uploaded.
     * @throws IOException
     */
    public GenerationFileResponse uploadGenerationFile(MultipartFile file) throws IOException {
        notNull(file, "The file cannot be null");
        String fileName = file.getOriginalFilename();
        log.info("Saving file: {}", fileName);
        GenerationFileEntity generationFileEntity = new GenerationFileEntity(fileName, file.getBytes());
        generationFileEntity = generationDao.save(generationFileEntity);

        return new GenerationFileResponse(
                generationFileEntity.getId(),
                generationFileEntity.getName(),
                generationFileEntity.getData()
        );
    }

    /**
     * Retrieve the uploaded file with, in addition, the result calculated from the info in the uploaded file.
     * @param id the id of the file to be retrieved.
     * @return the uploaded file with, in addition, the result calculated from the info in the uploaded file.
     * @throws IOException
     */
    @Override
    public GetGenerationFileResponse getGenerationFile(Long id) throws IOException {
        notNull(id, "The file id cannot be null");
        log.info("Retrieve file with id: {}", id);

        GenerationFileEntity generationFileEntity = getGenerationFileEntityByIdOrElseThrow(id);

        GenerationFileContent generationFileContent = getGenerationFileContentFrom(generationFileEntity.getData());

        GenerationFileContentResult generationFileContentResult = getGenerationFileContentResult(generationFileContent);

        Resource generationFileContentResultResource = getGenerationFileContentResultResourceFrom(generationFileContentResult);

        return new GetGenerationFileResponse(id, generationFileEntity.getName(), generationFileContentResultResource);
    }

    /**
     * Update the content of the uploaded file.
     * @param fileId id of the file to be modified.
     * @param file new file that will update the existing one.
     * @return a Response Entity with the info of the updated file.
     * @throws Exception
     */
    @Override
    public GenerationFileResponse updateGenerationFile(Long fileId, MultipartFile file) throws IOException {
        notNull(fileId, "The file id cannot be null");
        notNull(file, "The file cannot be null");

        String fileName = file.getOriginalFilename();
        log.info("Updating file: {} with id: {}", fileName, fileId);

        getGenerationFileEntityByIdOrElseThrow(fileId);

        GenerationFileEntity generationFileEntity = new GenerationFileEntity(fileId, file.getOriginalFilename(), file.getBytes());

        generationFileEntity = generationDao.save(generationFileEntity);

        return new GenerationFileResponse(
                generationFileEntity.getId(),
                generationFileEntity.getName(),
                generationFileEntity.getData()
        );
    }

    /**
     * Delete the file with a given file id. If the file is not found an exception is thrown.
     * @param fileId id of the file to be deleted.
     */
    @Override
    public void deleteGenerationFile(Long fileId) {
        log.info("Deleting file with id: {}", fileId);

        GenerationFileEntity generationFileEntity = getGenerationFileEntityByIdOrElseThrow(fileId);

        generationDao.delete(generationFileEntity);
        log.info("Successfully deleted file with id: {}", fileId);
    }

    private GenerationFileEntity getGenerationFileEntityByIdOrElseThrow(Long fileId) {
        return generationDao.findById(fileId)
                .orElseThrow(() -> new GenerationFileNotFoundException("File with id: " + fileId + " not found"));
    }

    private GenerationFileContentResult getGenerationFileContentResult(GenerationFileContent generationFileContent) {
        return new GenerationFileContentResult(
                generationFileContent.getValueX(),
                generationFileContent.getValueY(),
                calculateResult(generationFileContent)
        );
    }

    private Resource getGenerationFileContentResultResourceFrom(GenerationFileContentResult generationFileContentResult) throws JsonProcessingException {
        String jsonGenerationFileContentResult = objectMapper.writeValueAsString(generationFileContentResult);
        return new ByteArrayResource(jsonGenerationFileContentResult.getBytes());
    }

    private static Long calculateResult(GenerationFileContent generationFileContent) {
        return generationFileContent.getValueX() + generationFileContent.getValueY();
    }

    private GenerationFileContent getGenerationFileContentFrom(byte[] bytes) throws IOException {
        GenerationFileContent generationFileContent;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            generationFileContent = objectMapper.readValue(bytes, GenerationFileContent.class);
        } catch (UnrecognizedPropertyException ex) {
            throw new WrongContentFileFormatException("Error deserializing the file");
        }
        return generationFileContent;
    }
}
