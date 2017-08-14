package com.cloud.file_share.web_service.vo.populator;

import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.file_share.web_service.vo.DownloadableSharedFile;
import com.cloud.file_share.web_service.vo.RecordInfo;
import com.cloud.file_share.web_service.vo.RecordInfoWithFiles;

public class RecordInfoPopulator {

    public static void populateRecordInfo(SharedRecord record, RecordInfo recordInfo) {
        //populating view object
        recordInfo.setId(record.getId());
        recordInfo.setExpiryTime(record.getExpiryTime());
    }

    public static void populateRecordInfoWithFiles(SharedRecord record, RecordInfoWithFiles recordInfo) {
        //populating view object
        populateRecordInfo(record, recordInfo);

        record.getFiles().stream().forEach(file -> {
            DownloadableSharedFile fileInfo = new DownloadableSharedFile();

            fileInfo.setId(file.getId());
            fileInfo.setRecordId(record.getId());
            fileInfo.setOriginalName(file.getOriginalName());
            fileInfo.setMimeType(file.getMimeType());
            fileInfo.setSize(file.getSize());
            fileInfo.setDownloadLink(String.format("/api/files.download?recordId=%s&fileIds=%d", record.getId(), file.getId()));

            recordInfo.getFiles().add(fileInfo);
        });

    }
}
