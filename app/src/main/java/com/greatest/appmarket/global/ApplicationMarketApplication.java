package com.greatest.appmarket.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by WangHao on 2017.2.15  0015.
 */

public class ApplicationMarketApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        handler = new Handler();
        //当前线程id，当前是主线程
        mainThreadId = android.os.Process.myTid();
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
