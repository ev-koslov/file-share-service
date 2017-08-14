package com.cloud.common.blo.file_share;


import com.cloud.common.blo.ServerFile;

public class SharedFile extends ServerFile {
    protected String recordId;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
