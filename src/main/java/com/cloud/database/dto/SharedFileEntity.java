package com.cloud.database.dto;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class SharedFileEntity {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_name", nullable = false, updatable = false)
    private String storedName;

    @ManyToOne(targetEntity = SharedRecordEntity.class, optional = false)
    private SharedRecordEntity record;

    @Column(name = "mime_type", updatable = false, nullable = false)
    private String mimeType;

    @Column(updatable = false)
    private long size;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public SharedRecordEntity getRecord() {
        return record;
    }

    public void setRecord(SharedRecordEntity record) {
        this.record = record;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
