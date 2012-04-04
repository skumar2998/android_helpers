package com.androidhelpers.common;

import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ap4y
 * Date: 4/4/12
 * Time: 3:40 PM
 */
public class MinPriorityThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    }
}
