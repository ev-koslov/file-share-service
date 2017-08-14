package com.cloud.file_share.components;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.components.metadata_holder.MetadataHolder;
import com.cloud.file_share.blo.SharedFileNotifierMetadata;
import org.springframework.stereotype.Component;

@Component
public class SharedFileNotifierMetadataHolder extends MetadataHolder<SharedFileNotifierMetadata> {
    public SharedFileNotifierMetadata getBySharedFile(SharedFile file) {
        return this.metaMap.values().stream().filter(sharedFileNotifierMetadata -> sharedFileNotifierMetadata.getFile() == file).findFirst().get();
    }
}
