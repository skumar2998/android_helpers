package com.androidhelpers.adapters;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private List<? extends View> viewList;

    public ViewPagerAdapter(List<? extends View> viewList)
    {
        this.viewList = viewList;

        if (viewList == null)
            this.viewList = new ArrayList<View>();
    }

    @Override
    public int getCount()
    {
        return viewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup pager, int position) {

        View v = viewList.get(position);
        pager.addView(v, 0);
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
}
