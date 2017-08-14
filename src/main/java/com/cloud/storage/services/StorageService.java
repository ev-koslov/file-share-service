package com.cloud.storage.services;

import com.cloud.common.blo.ServerFile;

import java.io.IOException;

public interface StorageService {
    /**
     * Saves given file to datastore and returns populated {@link ServerFile} object
     * @param file file to store. NOTE: {@link ServerFile#dataStream} must not be null
     * @return populated file object
     */
    ServerFile store(ServerFile file) throws IOException;

    /**
     * Retrieves given file from datastore and populates {@link ServerFile#dataStream} field with saved data
     * @param file file to retrieve
     * @return
     */
    ServerFile load(ServerFile file) throws IOException;

    /**
     * deletes file from datastore
     * @param file
     */
    void delete(ServerFile file) throws IOException;
}
