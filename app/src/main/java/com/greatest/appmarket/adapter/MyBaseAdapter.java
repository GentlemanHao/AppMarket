package com.greatest.appmarket.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by WangHao on 2017.02.25  0025.
 */

public class MyBaseAdapter<T> extends BaseAdapter {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MORE = 1;
    private ArrayList<T> data;

    public MyBaseAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getCount() - 1) ? TYPE_MORE : TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
