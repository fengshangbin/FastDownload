package com.c3.fastdownload;

public class Resource {
    private String url;
    private String path;
    private String md5;
    private long size;
    private Object obj;
    private boolean noticeFail;

    public Resource(String url, String path, String md5, long size, Object obj) {
        this.url = url;
        this.path = path;
        this.md5 = md5;
        this.size = size;
        this.obj = obj;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isNoticeFail() {
        return noticeFail;
    }
    public void setNoticeFail(boolean noticeFail) {
        this.noticeFail = noticeFail;
    }
}
