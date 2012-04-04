package com.androidhelpers.networking.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import com.androidhelpers.common.MinPriorityThreadFactory;

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
    private final ExecutorService savePool;

    public interface OnBitmapGet {
        void bitmapRecieved(Bitmap bitmap, Integer urlHash);
    }

    public RemoteBitmap(int threadsNumber, int maxCachedCount, String cachedDirPath) throws IOException {
        pool = Executors.newFixedThreadPool(threadsNumber);
        savePool = Executors.newFixedThreadPool(threadsNumber, new MinPriorityThreadFactory());
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

    private Bitmap putBitmapToCache(Bitmap bitmap, Integer urlHash, int width, int height) {

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
                    onBitmapGet.bitmapRecieved((Bitmap) msg.obj, msg.what);
                }
            });
    }

    private void SetHandlersMessage(Integer urlHash) {

        synchronized (handlers) {
            if (handlers.containsKey(urlHash)) {
                ArrayList<Handler> handlerList = handlers.get(urlHash);
                for(Handler onCoverGet : handlerList) {
                    onCoverGet.sendMessage(Message.obtain(onCoverGet, urlHash, getBitmapFromCache(urlHash)));
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

        Bitmap cachedBitmap = getBitmapFromCache(hashKey);
        if (cachedBitmap != null) {
            SetHandlersMessage(hashKey);
            return;
        }

        pool.submit(new Runnable() {
            @Override
            public void run() {

                final File newFile = new File(cachedDirPath, hashKey.toString());
                if (newFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                    if (bitmap != null) {
                        putBitmapToCache(bitmap, hashKey, width, height);
                        SetHandlersMessage(hashKey);

                        return;
                    }
                }

                try {
                    final FileOutputStream fos = new FileOutputStream(newFile);
                    InputStream inputStream = getInputStream(url);

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null) {

                        putBitmapToCache(bitmap, hashKey, width, height);
                        SetHandlersMessage(hashKey);

                        savePool.submit(new Runnable() {
                            @Override
                            public void run() {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            }
                        });
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
