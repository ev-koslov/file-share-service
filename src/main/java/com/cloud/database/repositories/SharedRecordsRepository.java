package com.cloud.database.repositories;


import com.cloud.database.dto.SharedRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedRecordsRepository extends JpaRepository<SharedRecordEntity, String> {

}
