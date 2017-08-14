package com.cloud.file_share.blo;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.components.metadata_holder.Metadata;

public class SharedFileMetadata extends Metadata {
    private final SharedFile file;

    public SharedFileMetadata(String metadataId, SharedFile file) {
        super(metadataId);
        this.file = file;
    }

    public SharedFile getFile() {
        return file;
    }
}
