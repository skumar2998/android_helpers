package com.androidhelpers.networking.http;

import android.os.Handler;
import android.os.Message;
import com.androidhelpers.networking.http.callbacks.*;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 5:38 PM
 */
public class HttpHandler extends Handler {

    private HttpCallback callback;
    private UploadCallback uploadCallback;
    private DownloadCallback downloadCallback;
    private CachingCallback cachingCallback;
    private PostProcessCallback postProcessCallback;

    public HttpHandler() {}

    public HttpHandler(HttpCallback callback, PostProcessCallback postProcessCallback) {
        this.callback = callback;
        this.postProcessCallback = postProcessCallback;
    }

    public void setPostProcessCallback(PostProcessCallback postProcessCallback) {
        this.postProcessCallback = postProcessCallback;
    }

    public void setCallback(HttpCallback callback) {
        this.callback = callback;
    }

    public void setUploadCallback(UploadCallback uploadCallback) {
        this.uploadCallback = uploadCallback;
    }

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public void setCachingCallback(CachingCallback cachingCallback) {
        this.cachingCallback = cachingCallback;
    }

    public Object postProcessResponse(String response) {
        if (postProcessCallback != null)
            return postProcessCallback.postProcessResponse(this, response);

        return response;
    }

    public void postException(Exception e) {
        if (callback != null)
            callback.failed(e);
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case HttpRequest.DID_START:
                if (downloadCallback != null)
                    downloadCallback.connected((Long) msg.obj);
                break;
            case HttpRequest.DID_SUCCEED:
                if (callback != null)
                    callback.success(msg.obj);
                break;
            case HttpRequest.IS_UPLOADING:
                if (uploadCallback != null)
                    uploadCallback.uploadProgressChanged((Long) msg.obj);
                break;
            case HttpRequest.IS_DOWNLOADING:
                if (downloadCallback != null)
                    downloadCallback.downloadProgressChanged((Long) msg.obj);
                break;
            case HttpRequest.IS_CACHING:
                if (cachingCallback != null)
                    cachingCallback.cachingProgressChanged((byte[]) msg.obj);
                break;
            case HttpRequest.DID_ERROR:
                postException((Exception) msg.obj);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
