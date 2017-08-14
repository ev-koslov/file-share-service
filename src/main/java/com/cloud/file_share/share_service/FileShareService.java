package com.cloud.file_share.share_service;


import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.file_share.blo.SharedFileMetadata;

import java.io.IOException;
import java.io.InputStream;

public interface FileShareService {
    SharedRecord create(long ttl);
    SharedRecord get(String recordId);
    SharedRecord update(SharedRecord target);

    SharedFileMetadata prepareUpload(SharedFile file);
    SharedFile processUpload(String uploadId, InputStream dataStream) throws IOException;
    SharedRecord detachFileFromRecord(String recordId, long fileId) throws IOException;

    SharedFile getFile(String recordId, long fileId);
    SharedFile loadFile(String recordId, long fileId) throws IOException;
    SharedFile update(SharedFile target);
}
