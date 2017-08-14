package com.cloud.database.repositories;


import com.cloud.database.dto.SharedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedFilesRepository extends JpaRepository<SharedFileEntity, Long> {
}
