package com.cloud.common.blo.file_share;


import java.util.ArrayList;
import java.util.List;

public class SharedRecord {
    protected String id;
    protected long expiryTime;
    protected List<SharedFile> files = new ArrayList<>();

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

    public List<SharedFile> getFiles() {
        return files;
    }

    public void setFiles(List<SharedFile> files) {
        this.files = files;
    }
}
