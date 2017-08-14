package com.cloud.database.dao.impl;



import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.database.dao.SharedFilesDAO;
import com.cloud.database.dto.SharedFileEntity;
import com.cloud.database.dto.SharedRecordEntity;
import com.cloud.database.repositories.SharedFilesRepository;
import com.cloud.database.repositories.SharedRecordsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SharedFilesDAOImpl implements SharedFilesDAO {
    private final SharedFilesRepository sharedFilesRepository;
    private final SharedRecordsRepository sharedRecordsRepository;

    public SharedFilesDAOImpl(SharedFilesRepository sharedFilesRepository, SharedRecordsRepository sharedRecordsRepository) {
        this.sharedFilesRepository = sharedFilesRepository;
        this.sharedRecordsRepository = sharedRecordsRepository;
    }

    @Override
    public SharedFile create(SharedFile file) {
        //retrieving record from database. If there is no record in DB, throwing exception
        SharedRecordEntity recordEntity = Optional.ofNullable(sharedRecordsRepository.findOne(file.getRecordId())).get();

        SharedFileEntity fileEntity = new SharedFileEntity();
        
        fileEntity.setOriginalName(file.getOriginalName());
        fileEntity.setStoredName(file.getStoredName());
        fileEntity.setMimeType(file.getMimeType());
        fileEntity.setSize(file.getSize());
        fileEntity.setRecord(recordEntity);
        
        //persisting file entity
        sharedFilesRepository.saveAndFlush(fileEntity);        
        
        //updating BLO object
        file.setId(fileEntity.getId());
        
        return file;
    }

    @Override
    public SharedFile get(long id) {
        SharedFileEntity fileEntity = Optional.ofNullable(sharedFilesRepository.findOne(id)).get();

        SharedFile file = new SharedFile();

        file.setId(fileEntity.getId());
        file.setOriginalName(fileEntity.getOriginalName());
        file.setStoredName(fileEntity.getStoredName());
        file.setMimeType(fileEntity.getMimeType());
        file.setSize(fileEntity.getSize());
        file.setRecordId(fileEntity.getRecord().getId());
        
        return file;
    }

    @Override
    public SharedFile update(SharedFile file) {
        SharedFileEntity fileEntity = Optional.ofNullable(sharedFilesRepository.findOne(file.getId())).get();

        fileEntity.setOriginalName(file.getOriginalName());

        return file;
    }

    @Override
    public void delete(long id) {
        sharedFilesRepository.delete(id);
    }
}
