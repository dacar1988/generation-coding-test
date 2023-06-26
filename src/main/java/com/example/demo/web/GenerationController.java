package com.example.demo.web;

import com.example.demo.service.GenerationServiceImpl;
import com.example.demo.service.domain.GetGenerationFileResponse;
import com.example.demo.web.validator.FileValidator;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Controller with endpoints that manages the Generation file.
 */
@AllArgsConstructor
@Slf4j
@RestController
@Validated
@OpenAPIDefinition(info = @Info(title = "Generation API", version = "1.0", description = "Generation Endpoints"))
public class GenerationController {

    private GenerationServiceImpl generationServiceImpl;
    private FileValidator fileValidator;

    /**
     * Upload a json file with a given format and saves it in a database.
     * @param file to upload.
     * @return a Response Entity with the info of the uploaded file.
     * @throws Exception
     */
    @PostMapping(path = "v1/api/generation/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenerationFileResponse> uploadGenerationFile(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Uploading {}", file.getOriginalFilename());
        fileValidator.validateFile(file);

        GenerationFileResponse generationFileResponse = generationServiceImpl.uploadGenerationFile(file);

        log.info("File {} with id {} uploaded successfully", file.getOriginalFilename(), generationFileResponse.getId());

        return ResponseEntity
                .status(CREATED)
                .body(generationFileResponse);
    }

    /**
     * Retrieve the uploaded file with, in addition, the result calculated from the info in the uploaded file.
     * @param fileId id of the file to be retrieved
     * @return a file with the result calculated from the info in the uploaded file.
     * @throws IOException
     */
    @GetMapping(path = "v1/api/generation/{fileId}")
    public ResponseEntity<Resource> getGenerationFile(@Valid @PathVariable @NotNull @Positive Long fileId) throws IOException {
        log.info("Retrieving {}", fileId);

        GetGenerationFileResponse getGenerationFileResponse = generationServiceImpl.getGenerationFile(fileId);

        HttpHeaders headers = httpHeaders(getGenerationFileResponse);

        log.info("Retrieved file {} with id: {}", getGenerationFileResponse.getName(), fileId);

        return ResponseEntity
                .status(OK)
                .headers(headers)
                .body(getGenerationFileResponse.getResource());
    }

    /**
     * Update the content of the uploaded file.
     * @param fileId id of the file to be modified.
     * @param file new file that will update the existing one.
     * @return a Response Entity with the info of the updated file.
     * @throws Exception
     */
    @PutMapping(path = "v1/api/generation/update/{fileId}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenerationFileResponse> updateGenerationFile(
            @Valid
            @PathVariable
            @NotNull
            @Positive
            Long fileId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        log.info("Uploading {}", file.getOriginalFilename());
        fileValidator.validateFile(file);

        GenerationFileResponse generationFileResponse = generationServiceImpl.updateGenerationFile(fileId, file);

        log.info("File {} with id {} updated successfully", file.getOriginalFilename(), generationFileResponse.getId());

        return ResponseEntity
                .status(OK)
                .body(generationFileResponse);
    }

    /**
     * Delete the file with a given file id. If the file is not found an exception is thrown.
     * @param fileId id of the file to be deleted.
     * @return a Response entity with a successful message.
     */
    @DeleteMapping(path = "v1/api/generation/delete/{fileId}")
    public ResponseEntity<String> deleteGenerationFile(
            @Valid
            @PathVariable
            @NotNull
            @Positive
            Long fileId) {
        log.info("Deleting {}", fileId);

        generationServiceImpl.deleteGenerationFile(fileId);

        log.info("Deleted file with id: {}", fileId);

        return ResponseEntity
                .status(OK)
                .body("File with id " + fileId + ", deleted successfully");
    }

    private static HttpHeaders httpHeaders(GetGenerationFileResponse getGenerationFileResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", getGenerationFileResponse.getName());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("file-id", getGenerationFileResponse.getId().toString());
        return headers;
    }
}
