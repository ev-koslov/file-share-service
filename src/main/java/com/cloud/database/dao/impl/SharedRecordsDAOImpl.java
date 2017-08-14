package com.cloud.database.dao.impl;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.common.utils.Randomizer;
import com.cloud.database.dao.SharedRecordsDAO;
import com.cloud.database.dto.SharedRecordEntity;
import com.cloud.database.repositories.SharedRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SharedRecordsDAOImpl implements SharedRecordsDAO {
    private SharedRecordsRepository sharedRecordsRepository;

    @Autowired
    public SharedRecordsDAOImpl(SharedRecordsRepository sharedRecordsRepository) {
        this.sharedRecordsRepository = sharedRecordsRepository;
    }

    @Override
    public SharedRecord create(long ttl) {
        SharedRecordEntity recordEntity = new SharedRecordEntity();

        recordEntity.setExpiryTime(System.currentTimeMillis() + ttl);
        recordEntity.setId(Randomizer.randomString(6));

        sharedRecordsRepository.saveAndFlush(recordEntity);

        SharedRecord record = new SharedRecord();

        record.setId(recordEntity.getId());
        record.setExpiryTime(recordEntity.getExpiryTime());

        return record;
    }

    @Override
    public SharedRecord get(String recordId) {
        SharedRecordEntity recordEntity = Optional.ofNullable(sharedRecordsRepository.findOne(recordId)).get();

        SharedRecord record = new SharedRecord();

        record.setId(recordEntity.getId());
        record.setExpiryTime(recordEntity.getExpiryTime());

        recordEntity.getFiles().stream().forEach(fileEntity -> {
            SharedFile file = new SharedFile();

            file.setId(fileEntity.getId());
            file.setOriginalName(fileEntity.getOriginalName());
            file.setStoredName(fileEntity.getStoredName());
            file.setMimeType(fileEntity.getMimeType());
            file.setSize(fileEntity.getSize());
            file.setRecordId(fileEntity.getRecord().getId());

            record.getFiles().add(file);
        });

        return record;
    }

    @Override
    public SharedRecord update(SharedRecord record) {
        SharedRecordEntity targetEntity = Optional.ofNullable(sharedRecordsRepository.findOne(record.getId())).get();

        targetEntity.setExpiryTime(record.getExpiryTime());

        return record;
    }

    @Override
    public void delete(String recordId) {
        sharedRecordsRepository.delete(recordId);
    }
}
