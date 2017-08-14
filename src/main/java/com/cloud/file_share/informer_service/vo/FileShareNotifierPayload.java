package com.cloud.file_share.informer_service.vo;

import com.cloud.common.components.notifier.NotifierPayload;

public class FileShareNotifierPayload extends NotifierPayload {
    public final NotificationStatus action;
    public final Object object;

    public FileShareNotifierPayload(NotificationStatus action, Object object) {
        this.action = action;
        this.object = object;
    }
}
