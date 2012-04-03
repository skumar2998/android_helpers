package com.androidhelpers.networking.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 3:53 PM

 */
public class RemoteBitmap {

    private final HashMap<Integer, ArrayList<Handler>> handlers;
    private final Map<Integer, SoftReference<Bitmap>> cache;
    private final LinkedList<Bitmap> mChacheController = new LinkedList <Bitmap> ();

    private final int maxCacheSize;
    private final String cachedDirPath;
    private final ExecutorService pool;

    public interface OnBitmapGet {
        void bitmapRecieved(Bitmap bitmap);
    }

    public RemoteBitmap(int threadsNumber, int maxCachedCount, String cachedDirPath) throws IOException {
        pool = Executors.newFixedThreadPool(threadsNumber);
        cache = new HashMap<Integer, SoftReference<Bitmap>>();
        handlers = new HashMap<Integer, ArrayList<Handler>>();

        this.maxCacheSize = maxCachedCount;
        this.cachedDirPath = cachedDirPath;

        File cacheDir = new File(cachedDirPath);
        if ( !cacheDir.exists() ) {
            if ( !cacheDir.mkdirs() )
                throw new IOException("Unable to create cache folder");
        }
    }

    private Bitmap getBitmapFromCache(Integer urlHash) {
        if (cache.containsKey(urlHash) && cache.get(urlHash) != null) {
            return cache.get(urlHash).get();
        }

        return null;
    }

    private Bitmap putBitmapToCache(Integer urlHash, int width, int height) {

        Bitmap bitmap = BitmapFactory.decodeFile(cachedDirPath + urlHash);

        if (width != 0 && height != 0)
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        if (mChacheController.size() > maxCacheSize)
            mChacheController.subList(0, maxCacheSize /2).clear();
        mChacheController.addLast(bitmap);

        cache.put(urlHash, new SoftReference<Bitmap>(bitmap));

        return bitmap;
    }

    public void getCover(String url, OnBitmapGet onBitmapGet) {
        getCover(url, 0, 0, onBitmapGet);
    }

    public void getCover(String url, int width, int height, final OnBitmapGet onBitmapGet) {
        if (url != null && url.length() > 0)
            queueJob(url, width, height, new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    onBitmapGet.bitmapRecieved((Bitmap) msg.obj);
                }
            });
    }

    private void SetHandlersMessage(Integer urlHash) {

        synchronized (handlers) {
            if (handlers.containsKey(urlHash)) {
                ArrayList<Handler> handlerList = handlers.get(urlHash);
                for(Handler onCoverGet : handlerList) {
                    onCoverGet.sendMessage(Message.obtain(onCoverGet, 0, getBitmapFromCache(urlHash)));
                }

                handlers.remove(urlHash);
            }
        }
    }

    private void queueJob(final String url, final int width, final int height, final Handler onCoverGet) {

        final Integer hashKey = url.hashCode();
        
        synchronized (handlers) {
            if (handlers.containsKey(hashKey)) {
                ArrayList<Handler> handlerList = handlers.get(hashKey);
                handlerList.add(onCoverGet);
                return;
            }
            else {
                ArrayList<Handler> handlerList = new ArrayList<Handler>();
                handlerList.add(onCoverGet);
                handlers.put(hashKey, handlerList);
            }
        }

        pool.submit(new Runnable() {
            @Override
            public void run() {

                Bitmap cachedBitmap = getBitmapFromCache(hashKey);
                if (cachedBitmap != null) {
                    SetHandlersMessage(hashKey);
                    return;
                }

                boolean shouldDownload = true;

                final File newFile = new File(cachedDirPath, hashKey.toString());

                if (newFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                    if (bitmap != null) {
                        putBitmapToCache(hashKey, width, height);
                        SetHandlersMessage(hashKey);

                        shouldDownload = false;
                    }
                }

                if (shouldDownload) {

                    try {
                        final FileOutputStream fos = new FileOutputStream(newFile);
                        InputStream inputStream = getInputStream(url);

                        Bitmap drawable = BitmapFactory.decodeStream(inputStream);
                        if (drawable != null) {
                            drawable.compress(Bitmap.CompressFormat.PNG, 100, fos);

                            putBitmapToCache(hashKey, width, height);
                            SetHandlersMessage(hashKey);
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private InputStream getInputStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(true);

        return connection.getInputStream();
    }
}
