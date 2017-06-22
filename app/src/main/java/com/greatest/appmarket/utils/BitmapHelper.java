package com.greatest.appmarket.utils;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by WangHao on 2017.03.05  0005.
 */

public class BitmapHelper {
    private static BitmapUtils bitmapUtils = null;

    //单例设计模式，懒汉式
    public static BitmapUtils getBitmapUtils() {
        if (bitmapUtils == null) {
            synchronized (BitmapHelper.class) {
                bitmapUtils = new BitmapUtils(UIUtils.getContext());
            }
        }
        return bitmapUtils;
    }
}
