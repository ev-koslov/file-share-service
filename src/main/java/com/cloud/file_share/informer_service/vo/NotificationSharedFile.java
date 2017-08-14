package com.cloud.file_share.informer_service.vo;


import com.cloud.file_share.web_service.vo.DownloadableSharedFile;

public class NotificationSharedFile extends DownloadableSharedFile {
    protected long uploaded;
    protected byte percentage;
    protected long uploadSpeed;
    protected String metadataId;

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(long uploaded) {
        if (uploaded > size) {
            this.uploaded = size;
        } else {
            this.uploaded = uploaded;
        }

        this.percentage = (byte) ((1.0 * uploaded / size) * 100);
    }

    public byte getPercentage() {
        return percentage;
    }

    public String getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public long getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(long uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }
}
