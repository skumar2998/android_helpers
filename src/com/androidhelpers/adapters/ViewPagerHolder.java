package com.androidhelpers.adapters;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: ap4y
 * Date: 4/4/12
 * Time: 3:54 PM
 */
public interface ViewPagerHolder<T> {

    public View createView(T item);
}
