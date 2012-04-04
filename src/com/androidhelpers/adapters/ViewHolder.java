package com.androidhelpers.adapters;

import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 5:09 PM
 */
public interface ViewHolder<T>
{
    public void create(View rowView);
    public void formatHolder(ReusableArrayAdapter<T> adapter, int position);
}
