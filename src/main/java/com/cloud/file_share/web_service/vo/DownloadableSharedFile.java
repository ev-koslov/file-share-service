package com.cloud.file_share.web_service.vo;

import com.cloud.common.vo.DownloadableFile;

public class DownloadableSharedFile extends DownloadableFile {
    protected String recordId;



    public DownloadableSharedFile() {
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
