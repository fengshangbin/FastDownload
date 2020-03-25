package com.c3.fastdownload;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDownload {

    private ExecutorService fixedExecutorService;
    //private ArrayList<Resource> resources;
    private DownloadProgress downloadProgress;

    private Long chunkSize;

    public BatchDownload(ExecutorService fixedExecutorService, ArrayList<Resource> resources, DownloadProgress downloadProgress, Long chunkSize) {
        this.fixedExecutorService = fixedExecutorService;
        //this.resources = resources;
        this.downloadProgress = downloadProgress;
        this.chunkSize = chunkSize;

        long oldTotal = downloadProgress.getTotal();
        //calcTotal();
        if(resources.size()>0){
            for(int i=0; i<resources.size(); i++){
                download(resources.get(i));
            }
        }
        //Log.i("c3", "total: "+downloadProgress.getTotal());
        if(downloadProgress.getTotal() == oldTotal){//没有需要下载的
            downloadProgress.onDownloadFinish();
        }
    }

    public static BatchDownload build(ArrayList<Resource> resources, DownloadProgress downloadProgress, int nThreads, Long chunkSize) {
        return new BatchDownload(Executors.newFixedThreadPool(nThreads), resources, downloadProgress, chunkSize);
    }

    public void download(Resource resource){
        File file = new File(resource.getPath());
        if(file.exists()){
            String serverMD5 = resource.getMd5();
            String localMD5 = DownloadBreakPoint.readMD5(resource.getPath());
            String fileMD5 = MD5Util.getFileMD5(file);
            if(serverMD5 == fileMD5){
                return;
            }else{
                if(localMD5==null)localMD5=fileMD5;
                if(serverMD5!=localMD5){
                    //file.delete();
                    deleteFile(file);
                    DownloadBreakPoint.deleteCache(resource.getPath());
                }
            }
        }
        long size = resource.getSize();
        double  chunkCount = Math.ceil((double)size/(double )chunkSize);
        //Log.i("c3","resource "+size+"-"+chunkSize+"-"+chunkCount);
        for(int i=0; i<chunkCount; i++){
            long start = i*chunkSize;
            long end = i==chunkCount-1?size:(i+1)*chunkSize-1;
            downloadProgress.addTotal(end-start);
            fixedExecutorService.submit(new DownloadTask(resource, downloadProgress, start, end, (int)chunkCount));
        }
    }

    private void deleteFile(File file){
        final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(to);
        to.delete();
    }

    public void destroy(){
        fixedExecutorService.shutdown();
    }
}
