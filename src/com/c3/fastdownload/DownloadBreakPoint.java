package com.c3.fastdownload;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadBreakPoint {

    private static JSONObject pointDatas = new JSONObject();
    private static ThrottleTask cacheTask;

    private static CacheInterface cache;

    public static void setCacheInterface(CacheInterface _cache){
        cache = _cache;
        readCache();

        cacheTask = ThrottleTask.build(new Runnable() {
            public void run() {
                storeCache();
            }
        },3000L);
    }

    public static void readCache(){
        String downloadBreakPoint = cache.read();
        if(!downloadBreakPoint.equals("")){
            try {
                pointDatas = new JSONObject(downloadBreakPoint);
                //Log.i("c3", pointDatas.toString());
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        pointDatas= new JSONObject();
    }

    public static void storeCache(){
        cache.write(pointDatas.toString());
    }
    public static String readMD5(String key){
        JSONObject item = pointDatas.optJSONObject(key);
        if(item==null)return null;
        return item.optString("md5", null);
    }

    public static long read(String key){
        return read(key, 0);
    }
    public static long read(String key, long start){
        JSONObject item = pointDatas.optJSONObject(key);
        if(item==null)return start;
        return item.optLong("i"+start, start);
    }

    public synchronized static void deleteCache(String key){
        if(pointDatas.has(key)){
            pointDatas.remove(key);
            storeCache();
        }
    }

    public static void write(String key, long value, String md5) {
        write(key, 0, value, md5, 1);
    }
    public synchronized static void write(String key, long start, long value, String md5, int chunkCount) {
        JSONObject item = pointDatas.optJSONObject(key);
        if(item==null)item = new JSONObject();
        try {
            if(item.length()==0){
                pointDatas.put(key, item);
                item.put("md5", md5);
                item.put("chunkCount", chunkCount);
                item.put("finishCount", 0);
            }
            item.put("i"+start, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cacheTask.run();
    }
    public static void finish(String key) {
        finish(key,0);
    }
    public synchronized static void finish(String key, long start) {
        JSONObject item = pointDatas.optJSONObject(key);
        if(item==null)return;
        int finishCount = item.optInt("finishCount",0);
        int chunkCount = item.optInt("chunkCount",0);
        finishCount++;
        try {
            item.put("finishCount", finishCount);
            if(finishCount == chunkCount){
                pointDatas.remove(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cacheTask.run();
    }

    public synchronized static boolean checkfinish(String key) {
        return !pointDatas.has(key);
    }
}
