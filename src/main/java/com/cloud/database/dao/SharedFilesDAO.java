package com.cloud.database.dao;


import com.cloud.common.blo.file_share.SharedFile;

public interface SharedFilesDAO {
    SharedFile create(SharedFile file);
    SharedFile get(long id);
    SharedFile update(SharedFile file);
    void delete(long id);
}
