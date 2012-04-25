package com.androidhelpers.tests;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: ap4y
 * Date: 4/25/12
 * Time: 3:48 PM
 */
public class TestHelper {

    public static String handshakeFromTXTFileName(String fileName, Context context) throws IOException {
        InputStream is = context.getAssets().open(fileName + ".txt");
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, "UTF-8");
        try {
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read>0) {
                    out.append(buffer, 0, read);
                }
            } while (read>=0);
        } finally {
            in.close();
        }

        return out.toString();
    }

    public interface AsyncTest {
        void performTest(final CountDownLatch latch);
    }
    public static void runAsyncTest(long timeout, AsyncTest test) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        test.performTest(latch);
        assertTrue("async test timeout.", latch.await(timeout, TimeUnit.SECONDS));
    }
}
