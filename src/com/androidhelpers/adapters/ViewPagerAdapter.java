package com.androidhelpers.adapters;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter<T> extends PagerAdapter {

    private List<T> itemsList;
    private ViewPagerHolder<T> holder;

    public ViewPagerAdapter(List<T> itemsList, ViewPagerHolder<T> holder)
    {
        this.itemsList = itemsList;
        this.holder = holder;

        if (itemsList == null)
            this.itemsList = new ArrayList<T>();
    }

    @Override
    public int getCount()
    {
        return itemsList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup pager, int position) {

        View v = null;
        if (holder != null) {
            v = holder.createView(itemsList.get(position));

            pager.addView(v, 0);
        }

        return v;
    }

    @Override
    public void destroyItem(ViewGroup pager, int position, Object view) {
        pager.removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setItem(int position, T item) {
        itemsList.set(position, item);
    }
}
