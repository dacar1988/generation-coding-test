package com.example.demo.dao;

import com.example.demo.dao.domain.GenerationFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Dao interface to interact with the database for managing the uploaded file.
 */
public interface GenerationDao extends JpaRepository<GenerationFileEntity, Long> {
}
