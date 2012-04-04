package com.androidhelpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ap4y
 * Date: 3/17/12
 * Time: 4:47 PM
 */
public class ReusableArrayAdapter<T> extends ArrayAdapter<T> {

    private int layoutId;
    private Class<? extends ViewHolder<T>> holderClass;

    public ReusableArrayAdapter(Context context, int layoutResourceId,
                                Class<? extends ViewHolder<T>> holderClass, List<T> items) {
        super(context, 0, items);

        this.layoutId = layoutResourceId;
        this.holderClass = holderClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layoutId, null, true);

            try {
                if (holderClass != null) {
                    holder = holderClass.newInstance();
                    holder.create(rowView);
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            rowView.setTag(holder);
        }
        else
            holder = (ViewHolder)rowView.getTag();

        if (holder != null)
            holder.formatHolder(this, position);

        return rowView;
    }
}
