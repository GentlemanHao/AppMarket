package com.greatest.appmarket.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by WangHao on 2017.04.21  0021.
 */

public class InstallApp {
    private String name;
    private String packageName;
    private Drawable icon;
    private long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
