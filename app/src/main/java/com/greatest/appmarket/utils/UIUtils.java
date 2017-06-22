package com.greatest.appmarket.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;

import com.greatest.appmarket.bean.InstallApp;
import com.greatest.appmarket.global.ApplicationMarketApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by WangHao on 2017.2.15  0015.
 */

public class UIUtils {
    public static Context getContext() {
        return ApplicationMarketApplication.getContext();
    }

    public static Handler getHandler() {
        return ApplicationMarketApplication.getHandler();
    }

    public static int getMainThreadId() {
        return ApplicationMarketApplication.getMainThreadId();
    }

    //获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    //加载布局文件
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    //判断是否运行在主线程
    public static boolean isRunOnUIThread() {
        int myTid = Process.myTid();
        return myTid == getMainThreadId();
    }

    //运行在主线程的方法
    public static void runOnUIThread(Runnable runnable) {
        if (isRunOnUIThread()) {
            //主线程直接运行
            runnable.run();
        } else {
            //子线程借助handler让其运行在主线程
            getHandler().post(runnable);
        }
    }

    public static List<InstallApp> getAppInfoList(Context context) {
        // 包管理者
        PackageManager pm = context.getPackageManager();
        // 应用集合
        List<PackageInfo> packageList = pm.getInstalledPackages(0);

        List<InstallApp> appInfoList = new ArrayList<InstallApp>();

        for (PackageInfo packageInfo : packageList) {
            InstallApp appInfo = new InstallApp();
            // 获取包名
            appInfo.setPackageName(packageInfo.packageName);
            // 应用名
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.setName(applicationInfo.loadLabel(pm).toString());
            // 应用图标
            appInfo.setIcon(applicationInfo.loadIcon(pm));
            File file = new File(applicationInfo.sourceDir);
            appInfo.setSize(file.length());
            // 是否为系统应用
            if (!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)) {
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }
}
