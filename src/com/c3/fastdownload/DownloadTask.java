package com.c3.fastdownload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable{
    private final Resource resource;
    private final DownloadProgress downloadProgress;
    private long start;
    private long end;
    private int chunkCount;

    public DownloadTask(Resource resource, DownloadProgress downloadProgress, long start, long end, int chunkCount) {
        this.resource = resource;
        this.downloadProgress = downloadProgress;
        this.start=start;
        this.end=end;
        this.chunkCount = chunkCount;
        resource.setNoticeFail(false);
        //Log.i("c3","Instance DownloadTask");
    }

    private void downloadFile(String url, String path){

        File file = new File(path);
        File dir = file.getParentFile();//储存下载文件的目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //Log.i("c3", "1");
        long realStart = DownloadBreakPoint.read(path, start);
        downloadProgress.addLoaded(realStart-start);
        if (realStart == end){
            // 说明文件已经下载完
            //DownloadBreakPoint.finish(path, start);
            downloadProgress.downloadSuccess(resource);
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + realStart + "-"+end)
                .build();
        Response response;
        try{
            response = OkhttpProxy.getInstance().getClient().newCall(request).execute();
            if(!response.isSuccessful()){
                downloadProgress.downloadFailed(resource);
                //Log.e("c3", e.toString());
                return;
            }
        }catch (IOException e){
            downloadProgress.downloadFailed(resource);
            //Log.e("c3", e.toString());
            return;
        }

        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;

        byte[] buff = new byte[2048];
        int len = 0;
        try {
            is = response.body().byteStream();
            bis  =new BufferedInputStream(is);

            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile =  new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(realStart);
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
                downloadProgress.addLoaded(len);
                DownloadBreakPoint.write(path,start,start+len, resource.getMd5(), chunkCount);
            }

            // 下载完成
            DownloadBreakPoint.finish(path,start);
            downloadProgress.downloadSuccess(resource);
        } catch (Exception e) {
            e.printStackTrace();
            downloadProgress.downloadFailed(resource);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null){
                    bis.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e("c3", e.toString());
            }
        }
    }

    public void run() {
        //Log.i("c3","start download "+resource.getUrl()+"-"+start+"-"+end);
        downloadFile(resource.getUrl(), resource.getPath());
        //Log.i("c3","finish download "+resource.getUrl());
    }
}