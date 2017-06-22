package com.greatest.appmarket.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by WangHao on 2017.03.17  0017.
 */

public class DrawableUtils {

    public static GradientDrawable getGradientDrawable(int radius, int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    public static StateListDrawable getSelector(Drawable normal, Drawable press) {
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_pressed}, press);
        selector.addState(new int[]{}, normal);
        return selector;
    }
}
