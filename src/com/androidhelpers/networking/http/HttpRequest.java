package com.androidhelpers.networking.http;

import android.os.Message;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/15/12
 * Time: 4:23 PM
 */
public class HttpRequest {

    //states
    public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;
    public static final int IS_UPLOADING = 3;
    public static final int IS_DOWNLOADING = 4;
    public static final int IS_CACHING = 5;

    private static final int MAX_BUFFER_SIZE = 4096;

    protected HttpHandler handler;

    private HttpMethods method;
    private HttpURLConnection connection;
    private String params;
    private boolean shouldStop;
    private long downloadFrom;
    private boolean willCache;

    public HttpRequest(String url, Map<String, String> params, HttpMethods method) throws IOException {
        create(method, params);

        String finishedUrl = url;
        if(method == HttpMethods.GET)
            finishedUrl += "?" + this.params;

        URL urlData = new URL(finishedUrl);
        connection = (HttpURLConnection) urlData.openConnection();
        connection.setUseCaches(false);
    }

    private void sendStateMessage(Message message) {
        if (handler != null)
            handler.sendMessage(message);
    }

    private void create(HttpMethods method, Map<String, String> params) {
        this.method = method;

        if (params != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String key : params.keySet()) {
                if (i > 0)
                    sb.append("&");

                sb.append(key);
                sb.append("=");
                sb.append(URLEncoder.encode(params.get(key)));
                i++;
            }
            this.params = sb.toString();
            }
        else
            this.params = "";
    }

    private boolean isValidResponceCode(int responseCode) {
        return responseCode >= 200 && responseCode <= 299;
    }

    private String getRun(HttpURLConnection connection) throws IOException {
        InputStream in;
        String response;

        if (downloadFrom != 0)
            connection.setRequestProperty("Range", "bytes=" + downloadFrom + "-");

        connection.setRequestMethod("GET");
        connection.connect();

        if (!isValidResponceCode(connection.getResponseCode())) {

            sendStateMessage(Message.obtain(handler, DID_ERROR,
                    new Exception("HTTP response code " + connection.getResponseCode())));
            return "";
        }

        int contentLength = connection.getContentLength();
        sendStateMessage(Message.obtain(handler, DID_START, contentLength));

        long downloaded = 0;
        in = connection.getInputStream();

        BufferedReader inR = new BufferedReader(new InputStreamReader(in), 100*1024);
        StringBuilder sb = new StringBuilder("");
        String line;
        while ((line = inR.readLine()) != null) {

            downloaded += line.length();

            if(!willCache)
                sb.append(line);
            else
                sendStateMessage(Message.obtain(handler, IS_CACHING, line.getBytes()));

            sendStateMessage(Message.obtain(handler, IS_DOWNLOADING, downloaded + downloadFrom));
        }
        response = sb.toString();
        return response;
    }

    private String postRun(HttpURLConnection connection) throws IOException {
        String response = "";
        OutputStream out;
        InputStream in;

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", "" + params.length());
        connection.setDoOutput(true);
        connection.connect();

        sendStateMessage(Message.obtain(handler, DID_START));

        out = connection.getOutputStream();

        ByteArrayInputStream bodyBuffer = new ByteArrayInputStream(params.getBytes());
        int bytesAvailable, bufferSize, bytesRead;
        long totalRead = 0;

        while ((bytesAvailable = bodyBuffer.available()) > 0)
        {
            bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];
            bytesRead = bodyBuffer.read(buffer, 0, bufferSize);
            totalRead += bytesRead;

            out.write(buffer, 0, bytesRead);
            sendStateMessage(Message.obtain(handler, IS_UPLOADING, totalRead));
        }

        if (!isValidResponceCode(connection.getResponseCode())) {
            in = new BufferedInputStream(connection.getInputStream());

            while (!shouldStop) {
                byte buffer[] = new byte[MAX_BUFFER_SIZE];

                int read = in.read(buffer);
                if (read == -1)
                    break;

                byte outBuffer[] = new byte[read];
                System.arraycopy(buffer, 0, outBuffer, 0, read);
                response += new String(outBuffer);
            }
        }

        return response;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler handler) {
        this.handler = handler;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void stop() {
        shouldStop = true;
    }

    public String start() throws IOException {
        shouldStop = false;
        String response = "";

        switch (method) {
            case GET:
                response = getRun(connection);
                break;
            case POST:
                response = postRun(connection);
                break;
        }

        if (!response.equals("") || willCache) {
            Object result = response;
            if (handler != null)
                result = handler.postProcessResponse(response);

            if (result != null)
                sendStateMessage(Message.obtain(handler, DID_SUCCEED, result));
        }
        else
            sendStateMessage(Message.obtain(handler, DID_ERROR, new Exception("Empty response.")));

        return response;
    }
}
