package com.androidhelpers.networking.http;

import com.androidhelpers.common.MinPriorityThreadFactory;
import com.androidhelpers.networking.http.callbacks.HttpCallback;
import com.androidhelpers.networking.http.callbacks.PostProcessCallback;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private PostProcessCallback postProcessCallback;

    public HttpApiClient(String baseUrl) {
        this(baseUrl, defaultThreadsNumber);
    }

    public HttpApiClient(String baseUrl, int threadsNumber) {
        this.baseUrl = baseUrl;
        if (threadsNumber > 0)
            pool = Executors.newFixedThreadPool(threadsNumber, new MinPriorityThreadFactory());
    }

    public void setPostProcessCallback(PostProcessCallback postProcessCallback) {
        this.postProcessCallback = postProcessCallback;
    }

    public void getPath(String path, Map<String, String> params, HttpCallback callback) throws IOException {
        HttpRequestAsync httpRequest = new HttpRequestAsync(baseUrl + path, params, HttpMethods.GET);
        httpRequest.getHandler().setCallback(callback);
        httpRequest.getHandler().setPostProcessCallback(postProcessCallback);
        pool.submit(httpRequest.create());
    }

    public String getPathSync(String path, Map<String, String> params) throws IOException {
        HttpRequest httpRequest = new HttpRequest(baseUrl + path, params, HttpMethods.GET);
        return httpRequest.start();
    }

    public void postPath(String path, Map<String, String> params, HttpCallback callback) throws IOException {
        HttpRequestAsync httpRequest = new HttpRequestAsync(baseUrl + path, params, HttpMethods.POST);
        httpRequest.getHandler().setCallback(callback);
        httpRequest.getHandler().setPostProcessCallback(postProcessCallback);
        pool.submit(httpRequest.create());
    }
}
