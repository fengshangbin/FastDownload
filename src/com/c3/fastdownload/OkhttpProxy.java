package com.c3.fastdownload;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpProxy {
    private static class SingletonHolder{
        private static OkhttpProxy instance = new OkhttpProxy();
    }
    public static  OkhttpProxy getInstance(){
        return SingletonHolder.instance;
    }

    private final OkHttpClient client;
    private OkhttpProxy(){
        client = new OkHttpClient.Builder()
                //.hostnameVerifier(createInsecureHostnameVerifier())
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClient getClient() {
        return client;
    }

}
