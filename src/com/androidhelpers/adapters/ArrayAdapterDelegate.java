package com.androidhelpers.adapters;

import android.view.View;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 5:09 PM
 */
public abstract class ArrayAdapterDelegate<T>
{
    protected List<T> items;

    protected ArrayAdapterDelegate(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public abstract int sectionCount();
    public abstract int rowsInSectionAtPosition(int position);
    public abstract View headerViewForSection(View header, int section);
    public abstract View viewForPosition(View header, int section, int postion);
}
