package com.greatest.appmarket.bean;

import android.os.Environment;

import java.io.File;

/**
 * Created by WangHao on 2017.04.08  0008.
 */

public class DownloadInfo {
    private String id;
    private String name;
    private String iconUrl;
    private String downloadUrl;
    private String packageName;
    private long size;
    private long currentPos;
    private int currentState;

    public DownloadInfo(String id, String name, String downloadUrl, String packageName, long size, String iconUrl) {
        this.id = id;
        this.name = name;
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        this.size = size;
        this.iconUrl = iconUrl;
    }

    //获取下载进度
    public float getProgress() {
        if (size == 0) {
            return 0;
        }
        return currentPos / (float) size;
    }

    //获取下载路径
    public String getDownloadPath() {
        StringBuffer sb = new StringBuffer();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        sb.append(path);
        sb.append(File.separator);
        sb.append("MyAppDownload");

        if (createDir(sb.toString())) {
            return sb.toString() + File.separator + packageName + ".apk";
        }
        return null;
    }

    //创建路径
    public boolean createDir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(long currentPos) {
        this.currentPos = currentPos;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getSize() {
        return size;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
