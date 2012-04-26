package com.androidhelpers.tests;

import android.test.InstrumentationTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: ap4y
 * Date: 4/25/12
 * Time: 4:07 PM
 */
public class AsyncStubbedTest extends InstrumentationTestCase {

    public void runAsyncTestMainThread(long timeout, final TestHelper.AsyncTest test) throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                test.performTest(latch);
            }
        });
        assertTrue("async test timeout.", latch.await(timeout, TimeUnit.SECONDS));
    }

    public void runAsyncTestMainThreadTillTimeout(long timeout, final TestHelper.AsyncTest test) throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                test.performTest(latch);
            }
        });
        latch.await(timeout, TimeUnit.SECONDS);
    }

    public void runAsyncTestMainThread(final TestHelper.AsyncTest test) throws Throwable {
        runAsyncTestMainThread(5, test);
    }
}
