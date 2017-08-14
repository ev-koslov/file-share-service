package com.cloud.database.dao;


import com.cloud.common.blo.file_share.SharedRecord;

public interface SharedRecordsDAO {
    SharedRecord create(long ttl);
    SharedRecord get(String recordId);
    SharedRecord update(SharedRecord record);
    void delete(String recordId);
}
