package com.cloud.database.dto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "shared_records")
public class SharedRecordEntity {
    @Id
    @Column(updatable = false, nullable = false, unique = true)
    private String id;

    @Column(name = "expires")
    private long expiryTime;

    @OneToMany(targetEntity = SharedFileEntity.class, mappedBy = "record")
    private List<SharedFileEntity> files = new ArrayList<SharedFileEntity>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public List<SharedFileEntity> getFiles() {
        return files;
    }
}
