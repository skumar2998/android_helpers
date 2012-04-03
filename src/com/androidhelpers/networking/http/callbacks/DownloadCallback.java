package com.androidhelpers.networking.http.callbacks;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 5:51 PM
 */
public interface DownloadCallback {
    void connected(long contentLength);
    void downloadProgressChanged(long bytesDownloaded);
}
