package com.cloud.common.components.metadata_holder;


public abstract class Metadata {
    private final String metadataId;

    public Metadata(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getMetadataId() {
        return metadataId;
    }
}
