package com.androidhelpers.networking.http.callbacks;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 5:41 PM
 */
public interface HttpCallback {
    void success(Object result);
    void failed(Exception e);
}
