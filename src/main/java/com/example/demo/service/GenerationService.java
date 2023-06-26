package com.example.demo.service;

import com.example.demo.web.GenerationFileResponse;
import com.example.demo.service.domain.GetGenerationFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface to manage the uploaded file.
 */
public interface GenerationService {

    /**
     * Upload a json file with a given format and saves it in a database.
     * @param multipartFile
     * @return
     * @throws IOException
     */
    GenerationFileResponse uploadGenerationFile(MultipartFile multipartFile) throws IOException;

    /**
     * Retrieve the uploaded file with, in addition, the result calculated from the info in the uploaded file.
     * @param id the id of the file to be retrieved.
     * @return the uploaded file with, in addition, the result calculated from the info in the uploaded file.
     * @throws IOException
     */
    GetGenerationFileResponse getGenerationFile(Long id) throws IOException;

    /**
     * Update the content of the uploaded file.
     * @param fileId id of the file to be modified.
     * @param file new file that will update the existing one.
     * @return a Response with the info of the updated file.
     * @throws Exception
     */
    GenerationFileResponse updateGenerationFile(Long fileId, MultipartFile file) throws IOException;

    /**
     * Delete the file with a given file id. If the file is not found an exception is thrown.
     * @param fileId id of the file to be deleted.
     */
    void deleteGenerationFile(Long fileId);
}
