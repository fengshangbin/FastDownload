# FastDownload
java多线程队列下载，支持断点续传，大文件切割下载  
GitHub Pages: [https://github.com/fengshangbin/FastDownload](https://github.com/fengshangbin/FastDownload)

# 功能
- 多线程队列下载
- 断点续传
- 大文件切割块下载

# 设计理念
专注下载核心功能，提供UI状态回调，功能全面，使用简单

# 依赖
okhttp3(com.squareup.okhttp3), json(org.json.json)

# 初始化
实现持久化接口，以便能缓存下载进度，下面以Android为例
```
CacheInterface cache = new CacheInterface() {
    @Override
    public String read() {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("downloadBreakPoint","");
    }

    @Override
    public void write(String data) {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("downloadBreakPoint", data);
        editor.commit();
    }
};
DownloadBreakPoint.setCacheInterface(cache);
```

# 调用
设置需要下载的资源列表
```
ArrayList<Resource> resources = new ArrayList<>();
String sourceURL = "https://csdnimg.cn/public/common/libs/jquery/jquery-1.9.1.min.js";
String savePath = IOUtils.getAppDir() + "/jq.js";
String md5 = "ODdx7xaSv8w/K2kXyphXeA==";
long size = 92633L;
/*
@sourceURL 资源下载地址
@savePath 资源本地保存路径
@md5 资源哈希md5值
@size 资源大小
*/
Resource resource = new Resource(sourceURL, savePath, md5, size, null);
resources.add(resource);
```
监听下载状态
```
DownloadProgress downloadProgress = new DownloadProgress() {
	@Override
    protected synchronized void onDownloadChange() {
        //Message.sent(SynchronousHandler.CHANGE_LOADING, new long[]{this.getLoaded(), this.getTotal()});
    }

    @Override
    protected synchronized void onDownloadFailed(Resource resource) {
        //Log.i("c3","failed: "+resource.getPath());
    }

    @Override
    protected synchronized void onDownloadSuccess(Resource resource) {
        //Log.i("c3","success: "+resource.getPath());
    }

    @Override
    protected void onDownloadFinish() { 
    	//下载队列执行完成 包括成功和失败的任务
        //Log.i("c3","download all finish.");
    }
};
```
构建下载任务
```
int nThreads = 5;
Long chunkSize = 5*1024*1024L;
/*
@nThreads 多线程数量上限
@chunkSize 资源切割块大小
*/
BatchDownload batchDownload = BatchDownload.build(resources, downloadProgress, nThreads, chunkSize);
```
OK, 这样就完成了调用  
如果需要动态执行下载任务可以
```
batchDownload.download(resource);
```