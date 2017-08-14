package com.cloud.common.components.metadata_holder;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class MetadataHolder<T extends Metadata> {
    protected final Map<String, T> metaMap;

    public MetadataHolder() {
        this.metaMap = Collections.synchronizedMap(new HashMap<>());
    }

    public void add(T metaData) {
        metaMap.put(metaData.getMetadataId(), metaData);
    }

    public T get(String metaDataId) {
        return metaMap.get(metaDataId);
    }

    public T remove(String metaDataId) {
        return metaMap.remove(metaDataId);
    }
}
