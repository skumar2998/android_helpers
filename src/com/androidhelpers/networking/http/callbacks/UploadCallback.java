package com.androidhelpers.networking.http.callbacks;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 5:50 PM
 */
public interface UploadCallback {
    void uploadProgressChanged(long bytesUploaded);
}
