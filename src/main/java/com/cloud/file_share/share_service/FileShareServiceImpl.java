package com.cloud.file_share.share_service;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.common.utils.Randomizer;
import com.cloud.database.dao.SharedFilesDAO;
import com.cloud.database.dao.SharedRecordsDAO;
import com.cloud.file_share.blo.SharedFileMetadata;
import com.cloud.file_share.components.SharedFileMetadataHolder;
import com.cloud.storage.services.StorageService;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class FileShareServiceImpl implements FileShareService {
    private final SharedRecordsDAO recordsDAO;
    private final SharedFilesDAO filesDAO;

    private final StorageService storageService;

    private final SharedFileMetadataHolder metadataHolder;

    public FileShareServiceImpl(SharedRecordsDAO recordsDAO,
                                SharedFilesDAO filesDAO,
                                StorageService storageService,
                                SharedFileMetadataHolder metadataHolder) {
        this.recordsDAO = recordsDAO;
        this.filesDAO = filesDAO;
        this.storageService = storageService;
        this.metadataHolder = metadataHolder;
    }

    @Override
    public SharedRecord create(long ttl) {
        return recordsDAO.create(ttl);
    }

    @Override
    public SharedRecord get(String recordId) {
        return recordsDAO.get(recordId);
    }

    @Override
    @Transactional
    public SharedRecord update(SharedRecord target) {
        recordsDAO.update(target);

        return target;
    }

    @Override
    public SharedFileMetadata prepareUpload(SharedFile file) {
        //TODO: add auto file rename if exists in a record

        SharedFileMetadata sharedFileMetadata = new SharedFileMetadata(Randomizer.randomString(16), file);

        metadataHolder.add(sharedFileMetadata);

        return sharedFileMetadata;
    }

    @Override
    @Transactional
    public SharedFile processUpload(String metadataId, InputStream dataStream) throws IOException {
        //retrieving uploadInfo from uploadsMap
        SharedFileMetadata sharedFileMetadata = metadataHolder.get(metadataId);

        SharedFile file = sharedFileMetadata.getFile();

        //attaching data stream to uploadInfo file
        file.setDataStream(dataStream);

        //performing pre-store checks
        //TODO: checks here

        //saving file to storage service
        storageService.store(sharedFileMetadata.getFile());

        //saving file to database
        filesDAO.create(file);

        //removing uploadInfo
        metadataHolder.remove(metadataId);

        //updating file and returning it
        return file;
    }

    @Override
    public SharedRecord detachFileFromRecord(String recordId, long fileId) throws IOException {
        SharedFile file = filesDAO.get(fileId);

        //if file does not belong to specified record, throwing exception
        if (!file.getRecordId().equals(recordId)) {
            throw new NoSuchElementException(String.format("File #%d does not belong to record #%s", fileId, recordId));
        }

        //deleting file from storage service
        storageService.delete(file);

        //only if file was deleted, removing it from database
        filesDAO.delete(file.getId());

        //returning record with changes
        return recordsDAO.get(recordId);
    }

    @Override
    public SharedFile getFile(String recordId, long fileId) {
        SharedFile file = filesDAO.get(fileId);

        //if file does not belong to specified record, throwing exception
        if (!file.getRecordId().equals(recordId)) {
            throw new NoSuchElementException(String.format("File #%d does not belong to record #%s", fileId, recordId));
        }

        return file;
    }

    @Override
    public SharedFile loadFile(String recordId, long fileId) throws IOException {
        //get file info
        SharedFile file = getFile(recordId, fileId);

        storageService.load(file);

        return file;
    }

    @Override
    public SharedFile update(SharedFile file) {

//        if (!entity.getRecord().getId().equals(file.getRecordId())) {
//            throw new NoSuchElementException(String.format("File #%d does not belong to record #%s", file.getId(), file.getRecordId()));
//        }

        filesDAO.update(file);

        return file;
    }
}
