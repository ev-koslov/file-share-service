package com.cloud.file_share.web_service.vo;


public class RecordInfo {
    protected String id;
    protected long expiryTime;

    public RecordInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
