package com.cloud.common.vo;


public class DownloadableFile extends File {
    protected String downloadLink;

    public DownloadableFile() {

    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
