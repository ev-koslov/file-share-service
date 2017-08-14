package com.cloud.common.blo;

import java.io.InputStream;

public class ServerFile {
    protected long id;
    protected String originalName;
    protected String mimeType;
    protected long size;
    protected String storedName;
    protected long bytesTransfered;
    protected InputStream dataStream;

    public ServerFile() {
    }

    public ServerFile(String originalName, String mimeType, long size) {
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.size = size;
    }

    public ServerFile(String originalName, String mimeType, long size, InputStream dataStream) {
        this(originalName, mimeType, size);
        this.dataStream = dataStream;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public long getBytesTransfered() {
        return bytesTransfered;
    }

    public void setBytesTransfered(long bytesTransfered) {
       this.bytesTransfered = bytesTransfered;
    }

    public InputStream getDataStream() {
        return dataStream;
    }

    public void setDataStream(InputStream dataStream) {
        this.dataStream = dataStream;
    }
}
