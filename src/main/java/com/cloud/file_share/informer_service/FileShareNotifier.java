package com.cloud.file_share.informer_service;

import com.cloud.common.components.notifier.Notifier;
import com.cloud.file_share.informer_service.vo.FileShareNotifierPayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class FileShareNotifier implements Notifier<FileShareNotifierPayload> {
    private final String sharedRecordSubscriptionPrefix;

    private final SimpMessagingTemplate messagingTemplate;

    public FileShareNotifier(String sharedRecordSubscriptionPrefix, SimpMessagingTemplate messagingTemplate) {
        this.sharedRecordSubscriptionPrefix = sharedRecordSubscriptionPrefix;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notify(String destination, FileShareNotifierPayload payload) {
        messagingTemplate.convertAndSend(String.format("%s%s/", sharedRecordSubscriptionPrefix, destination), payload);
    }
}
