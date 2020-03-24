package com.c3.fastdownload;

public abstract class DownloadProgress {

    private long loaded = 0;
    private long total = 0;

    private int loadCount = 0;
    private int totalCount = 0;

    public DownloadProgress(){}

    public DownloadProgress(long loaded, long total) {
        this.loaded = loaded;
        this.total = total;
    }
    public void addTotal(long totalIncrement) {
        this.total += totalIncrement;
        this.totalCount += 1;
    }

    public long getLoaded() {
        return loaded;
    }

    public void setLoaded(long loaded) {
        this.loaded = loaded;
        onDownloadChange();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    private synchronized void finishOne(Resource resource){
        loadCount++;
        //Log.i("c3",loadCount+"/"+totalCount);
        if(loadCount==totalCount){
            onDownloadFinish();
        }
    }

    public void downloadFailed(Resource resource){
        if(!resource.isNoticeFail()){
            resource.setNoticeFail(true);
            onDownloadFailed(resource);
        }
        finishOne(resource);
    }

    public void downloadSuccess(Resource resource){
        if(DownloadBreakPoint.checkfinish(resource.getPath())){
            onDownloadSuccess(resource);
        }
        finishOne(resource);
    }

    protected  void addLoaded(long loadedIncrement){
        this.setLoaded(this.getLoaded()+loadedIncrement);
    }

    protected abstract void onDownloadChange();

    protected abstract void onDownloadFailed(Resource resource);

    protected abstract void onDownloadSuccess(Resource resource);

    protected abstract void onDownloadFinish();

}
