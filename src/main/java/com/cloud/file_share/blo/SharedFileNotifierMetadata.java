package com.cloud.file_share.blo;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.file_share.informer_service.vo.NotificationSharedFile;

public class SharedFileNotifierMetadata extends SharedFileMetadata {
    private final NotificationSharedFile notificationSharedFile;
    private long prevBytesUploaded;
    private long prevInformTime;

    public SharedFileNotifierMetadata(String metadataId, SharedFile file, NotificationSharedFile notificationSharedFile) {
        super(metadataId, file);
        this.notificationSharedFile = notificationSharedFile;
    }

    public NotificationSharedFile getNotificationSharedFile() {
        return notificationSharedFile;
    }

    public long getPrevBytesUploaded() {
        return prevBytesUploaded;
    }

    public void setPrevBytesUploaded(long prevBytesUploaded) {
        this.prevBytesUploaded = prevBytesUploaded;
    }

    public long getPrevInformTime() {
        return prevInformTime;
    }

    public void setPrevInformTime(long prevInformTime) {
        this.prevInformTime = prevInformTime;
    }
}
