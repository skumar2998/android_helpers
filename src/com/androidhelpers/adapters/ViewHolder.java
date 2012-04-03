package com.androidhelpers.adapters;

import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 5:09 PM
 */
public abstract class ViewHolder
{
    public abstract void create(View rowView);
    public abstract void formatHolder(ReusableArrayAdapter adapter, int position);
}
