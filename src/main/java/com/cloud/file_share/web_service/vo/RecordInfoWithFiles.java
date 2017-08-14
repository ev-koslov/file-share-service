package com.cloud.file_share.web_service.vo;

import java.util.ArrayList;
import java.util.List;

public class RecordInfoWithFiles extends RecordInfo {
    protected List<DownloadableSharedFile> files = new ArrayList<>();

    public RecordInfoWithFiles() {

    }

    public List<DownloadableSharedFile> getFiles() {
        return files;
    }
}
