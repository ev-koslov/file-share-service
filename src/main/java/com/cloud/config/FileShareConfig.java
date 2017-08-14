package com.cloud.config;

import com.cloud.database.dao.SharedFilesDAO;
import com.cloud.database.dao.SharedRecordsDAO;
import com.cloud.file_share.components.SharedFileMetadataHolder;
import com.cloud.file_share.components.SharedFileNotifierMetadataHolder;
import com.cloud.file_share.informer_service.FileShareActionsWatcher;
import com.cloud.file_share.informer_service.FileShareNotifier;
import com.cloud.file_share.share_service.FileShareService;
import com.cloud.file_share.share_service.FileShareServiceImpl;
import com.cloud.storage.services.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@ComponentScan("com.cloud.file_share")
public class FileShareConfig {


    //  Bean of primary file share service
    @Bean
    public FileShareService fileShareService(SharedRecordsDAO recordsDAO,
                                             SharedFilesDAO filesDAO,
                                             StorageService storageService,
                                             SharedFileMetadataHolder metadataHolder) {
        return new FileShareServiceImpl(recordsDAO, filesDAO, storageService, metadataHolder);
    }


//  Bean of file share notifier service
    @Bean
    public FileShareNotifier fileShareNotifier(SimpMessagingTemplate messagingTemplate){
        return new FileShareNotifier("/topic/", messagingTemplate);
    }

    //bead enables notification service watcher (aspectJ)
    @Bean
    public FileShareActionsWatcher fileShareActionsWatcher(FileShareNotifier notifier,
                                                           SharedFileNotifierMetadataHolder metadataHolder){
        return new FileShareActionsWatcher(notifier, metadataHolder);
    }
}
