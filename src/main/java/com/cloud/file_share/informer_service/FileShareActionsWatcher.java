package com.cloud.file_share.informer_service;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.file_share.blo.SharedFileMetadata;
import com.cloud.file_share.blo.SharedFileNotifierMetadata;
import com.cloud.file_share.components.SharedFileNotifierMetadataHolder;
import com.cloud.file_share.informer_service.vo.FileShareNotifierPayload;
import com.cloud.file_share.informer_service.vo.NotificationSharedFile;
import com.cloud.file_share.informer_service.vo.NotificationStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.*;


@Aspect
public class FileShareActionsWatcher {
    private Logger logger = Logger.getLogger(this.getClass());
    private ObjectMapper mapper = new ObjectMapper();

    private final FileShareNotifier notifier;

    private final SharedFileNotifierMetadataHolder metadataHolder;

    public FileShareActionsWatcher(FileShareNotifier notifier, SharedFileNotifierMetadataHolder metadataHolder) {
        this.notifier = notifier;
        this.metadataHolder = metadataHolder;
    }

    @AfterReturning(
            value = "execution(* com.cloud.file_share.share_service.FileShareService+.prepareUpload(com.cloud.common.blo.file_share.SharedFile+))",
            returning = "sharedFileMetadata"
    )
    protected void watchNewFileAdded(SharedFileMetadata sharedFileMetadata) {
        try {
            SharedFile file = sharedFileMetadata.getFile();
            logger.info("Pending file: " + mapper.writeValueAsString(file));

            NotificationSharedFile notificationSharedFile = new NotificationSharedFile();
            notificationSharedFile.setId(file.getId());
            notificationSharedFile.setRecordId(file.getRecordId());
            notificationSharedFile.setOriginalName(file.getOriginalName());
            notificationSharedFile.setMimeType(file.getMimeType());
            notificationSharedFile.setSize(file.getSize());
            notificationSharedFile.setMetadataId(sharedFileMetadata.getMetadataId());

            SharedFileNotifierMetadata sharedFileNotifierMetadata = new SharedFileNotifierMetadata(notificationSharedFile.getMetadataId(), file, notificationSharedFile);

            metadataHolder.add(sharedFileNotifierMetadata);

            notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.UPLOAD_PENDING, notificationSharedFile));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Before(value = "execution(* com.cloud.storage.services.StorageService+.store(*)) && args(file)")
    protected void watchFileUploadStarted(SharedFile file) {
        try {
            logger.info("Started upload file: " + mapper.writeValueAsString(file));

            SharedFileNotifierMetadata metadata = metadataHolder.getBySharedFile(file);

            metadata.setPrevInformTime(System.currentTimeMillis());

            notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.UPLOAD_STARTED, metadata.getNotificationSharedFile()));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Before("execution(* com.cloud.common.components.AspectWatcherBean.watch(*)) && args(file)")
    protected void watchFileUploadProgress(SharedFile file) {
        SharedFileNotifierMetadata metadata = metadataHolder.getBySharedFile(file);

        long currTime = System.currentTimeMillis();

        if ((currTime - metadata.getPrevInformTime()) >= 2000 || (file.getBytesTransfered() - metadata.getPrevBytesUploaded()) >= 52428800) {

            metadata.getNotificationSharedFile().setUploaded(file.getBytesTransfered());
            metadata.getNotificationSharedFile().setUploadSpeed((long) ((file.getBytesTransfered() - metadata.getPrevBytesUploaded()) / ((currTime - metadata.getPrevInformTime()) / 1000.0)));

            notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.UPLOAD_PROGRESS, metadata.getNotificationSharedFile()));

            metadata.setPrevInformTime(currTime);
            metadata.setPrevBytesUploaded(file.getBytesTransfered());

            try {
                logger.info("Progress file: " + mapper.writeValueAsString(file));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


    @AfterReturning(value = "execution(* com.cloud.file_share.share_service.FileShareService+.processUpload(String, java.io.InputStream))", returning = "file")
    protected void watchFileSaved(SharedFile file) {
        try {
            logger.info("Saved file: " + mapper.writeValueAsString(file));

            SharedFileNotifierMetadata metadata = metadataHolder.getBySharedFile(file);

            metadata.getNotificationSharedFile().setId(file.getId());
            metadata.getNotificationSharedFile().setUploaded(file.getSize());
            metadata.getNotificationSharedFile().setDownloadLink(String.format("/api/files.download?recordId=%s&fileIds=%d", file.getRecordId(), file.getId()));

            notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.UPLOAD_COMPLETE, metadata.getNotificationSharedFile()));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @AfterThrowing(value = "execution(* com.cloud.storage.services.StorageService+.store(*)) && args(file))", throwing = "exception")
    protected void watchFileUploadCancelled(SharedFile file, Exception exception) {
        if (SharedFile.class.isAssignableFrom(file.getClass())) {
            try {
                logger.info("CANCELLED file: " + mapper.writeValueAsString(file));

                SharedFileNotifierMetadata metadata = metadataHolder.getBySharedFile(file);

                metadataHolder.remove(metadata.getMetadataId());

                notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.FILE_REMOVED, metadata.getNotificationSharedFile()));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @After("execution(* com.cloud.storage.services.StorageService+.delete(*)) && args(file))")
    protected void watchFileDeleted(SharedFile file) {
            try {
                logger.info("Deleted file: " + mapper.writeValueAsString(file));


                NotificationSharedFile notificationSharedFile = new NotificationSharedFile();
                notificationSharedFile.setId(file.getId());
                notificationSharedFile.setRecordId(file.getRecordId());
                notificationSharedFile.setOriginalName(file.getOriginalName());
                notificationSharedFile.setMimeType(file.getMimeType());
                notificationSharedFile.setSize(file.getSize());


                notifier.notify(file.getRecordId(), new FileShareNotifierPayload(NotificationStatus.FILE_REMOVED, notificationSharedFile));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
    }
}
