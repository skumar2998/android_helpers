package com.androidhelpers.networking.http;

import com.androidhelpers.networking.http.callbacks.HttpCallback;
import com.androidhelpers.networking.http.callbacks.PostProcessCallback;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 5:36 PM
 */
public class HttpApiClient {

    private static final int defaultThreadsNumber = 3;
    
    private String baseUrl;
    private ExecutorService pool;

    class LowPriorityThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }

    public HttpApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        pool = Executors.newFixedThreadPool(defaultThreadsNumber, new LowPriorityThreadFactory());
    }

    public HttpApiClient(String baseUrl, int threadsNumber, ThreadFactory factory) {
        this.baseUrl = baseUrl;
        if (factory == null)
            pool = Executors.newFixedThreadPool(threadsNumber, new LowPriorityThreadFactory());
        else
            pool = Executors.newFixedThreadPool(threadsNumber, factory);
    }

    public PostProcessCallback getPostProcessCallback() {
        return new PostProcessCallback() {
            @Override
            public Object postProcessResponse(HttpHandler handler,  String response) {
                return response;
            }
        };
    }

    public void getPath(String path, Map<String, String> params, HttpCallback callback) {
        try {
            HttpRequestAsync httpRequest = new HttpRequestAsync(baseUrl + path, params, HttpMethods.GET);
            httpRequest.getHandler().setCallback(callback);
            httpRequest.getHandler().setPostProcessCallback(getPostProcessCallback());
            pool.submit(httpRequest.create());
        } catch (IOException e) {
            callback.failed(e);
        }
    }

    public void postPath(String path, Map<String, String> params, HttpCallback callback) {
        try {
            HttpRequestAsync httpRequest = new HttpRequestAsync(baseUrl + path, params, HttpMethods.POST);
            httpRequest.getHandler().setCallback(callback);
            httpRequest.getHandler().setPostProcessCallback(getPostProcessCallback());
            pool.submit(httpRequest.create());
        } catch (IOException e) {
            callback.failed(e);
        }
    }
}
