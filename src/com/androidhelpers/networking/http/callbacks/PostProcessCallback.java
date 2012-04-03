package com.androidhelpers.networking.http.callbacks;

import com.androidhelpers.networking.http.HttpHandler;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/16/12
 * Time: 3:16 PM
 */
public interface PostProcessCallback {
    Object postProcessResponse(HttpHandler handler, String response);
}
