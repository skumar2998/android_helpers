package com.androidhelpers.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 4:47 PM
 */
public class ReusableArrayAdapter2<T> extends ArrayAdapter<T> {

    private ArrayAdapterDelegate<T> delegate;

    private int curSection;
    private int curPosition;

    public ReusableArrayAdapter2(Context context, ArrayAdapterDelegate<T> delegate) {
        super(context, 0, delegate.getItems());
        this.delegate = delegate;
    }

    private boolean isSectionForPosition(int position) {
        if (delegate == null)
            return false;

        int j = 0;
        int sections = delegate.sectionCount();
        curPosition = position;
        while(curPosition >= 0 && j < sections) {
            curSection = j;
            curPosition--;

            if (curPosition < 0)
                return true;

            int inSection = delegate.rowsInSectionAtPosition(j);
            if (curPosition - inSection < 0)
                return false;

            curPosition -= inSection;
            j++;
        }

        return false;
    }

    @Override
    public int getCount() {
        if (delegate == null)
            return 0;

        int count = delegate.sectionCount();
        for (int i = 0; i < delegate.sectionCount(); i++) {
            count += delegate.rowsInSectionAtPosition(i);
        }

        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (isSectionForPosition(position)) {
            rowView = delegate.headerViewForSection(rowView, curSection);
        }
        else {
            rowView = delegate.viewForPosition(rowView, curSection, curPosition);
        }

        return rowView;
    }
}
