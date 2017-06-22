package com.greatest.appmarket.manager;

import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.widget.Toast;

import com.greatest.appmarket.bean.DownloadInfo;
import com.greatest.appmarket.db.DownloadDao;
import com.greatest.appmarket.utils.SpUtil;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WangHao on 2017.04.08  0008.
 */

public class DownloadManager {

    public static final int DOWNLOAD_UNDO = 1;//未下载
    public static final int DOWNLOAD_WAITING = 2;//等待下载
    public static final int DOWNLOAD_DOWNLOADING = 3;//下载中
    public static final int DOWNLOAD_PAUSE = 4;//下载暂停
    public static final int DOWNLOAD_ERROR = 5;//下载失败
    public static final int DOWNLOAD_SUCCESS = 6;//下载成功

    private static DownloadManager downloadManager = new DownloadManager();

    // 4 观察者集合
    private ArrayList<DownloadObserver> observers = new ArrayList<>();

    //下载对象集合
    private ConcurrentHashMap<String, DownloadInfo> downloadInfoMap = new ConcurrentHashMap<>();
    //下载任务集合
    private ConcurrentHashMap<String, DownloadTask> downloadTaskMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, HttpHandler<File>> downloadHandlerMap = new ConcurrentHashMap<>();

    public DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return downloadManager;
    }

    // 1 观察者接口
    public interface DownloadObserver {
        //下载状态改变
        public void onDownloadStateChanged(DownloadInfo info);

        //下载进度发生改变
        public void onDownloadProgressChanged(DownloadInfo info);
    }

    // 2 注册观察者
    public void registerObserver(DownloadObserver observer) {
        if (observer != null && !observers.contains(observer))
            observers.add(observer);
    }

    // 3 注销观察者
    public void unRegisterObserver(DownloadObserver observer) {
        if (observer != null && observers.contains(observer))
            observers.remove(observer);
    }

    // 5 通知下载状态发生改变
    public void notifyDownloadStateChanged(DownloadInfo info) {
        for (DownloadObserver observer : observers) {
            observer.onDownloadStateChanged(info);
        }
    }

    // 6 通知下载进度发生改变
    public void notifyDownloadProgressChanged(DownloadInfo info) {
        for (DownloadObserver observer : observers) {
            observer.onDownloadProgressChanged(info);
        }
    }

    public DownloadInfo getDownloadInfo(String id, String name, String downloadUrl, String packageName, long size, String iconUrl) {
        //断点续传的判断
        DownloadInfo downloadInfo = downloadInfoMap.get(id);
        if (downloadInfo == null) {
            downloadInfo = new DownloadInfo(id, name, downloadUrl, packageName, size, iconUrl);
        }
        return downloadInfo;
    }

    //下载
    public synchronized void download(DownloadInfo downloadInfo) {

        //状态先改为等待
        downloadInfo.setCurrentState(DOWNLOAD_WAITING);
        notifyDownloadStateChanged(downloadInfo);
        downloadInfoMap.put(downloadInfo.getId(), downloadInfo);

        //执行下载任务
        DownloadTask downloadTask = new DownloadTask(downloadInfo);
        ThreadManager.getThreadPool().execute(downloadTask);
        downloadTaskMap.put(downloadInfo.getId(), downloadTask);
    }

    class DownloadTask implements Runnable {

        private DownloadInfo downloadInfo;
        HttpHandler<File> handler;

        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            HttpUtils httpUtils = new HttpUtils();
            handler = httpUtils.download(URL.Base_URL + downloadInfo.getDownloadUrl(), downloadInfo.getDownloadPath(), true, true, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();
                    Toast.makeText(UIUtils.getContext(), "开始下载:" + downloadInfo.getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled() {
                    super.onCancelled();
                    Toast.makeText(UIUtils.getContext(), "取消下载", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(UIUtils.getContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    downloadInfo.setCurrentState(DOWNLOAD_SUCCESS);
                    notifyDownloadStateChanged(downloadInfo);

                    //获取用户的登录状态
                    if (SpUtil.getBoolean(UIUtils.getContext(),"ISLOGIN", false)){
                        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
                        Date curDate = new Date(System.currentTimeMillis());

                        DownloadDao downloadDao = DownloadDao.getInstance(UIUtils.getContext());
                        downloadDao.insert(downloadInfo.getName(),
                                downloadInfo.getPackageName(), formatter.format(curDate),
                                URL.Base_URL + downloadInfo.getIconUrl(),
                                Formatter.formatFileSize(UIUtils.getContext(), downloadInfo.getSize()));
                    }

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    downloadInfo.setCurrentState(DOWNLOAD_DOWNLOADING);
                    notifyDownloadStateChanged(downloadInfo);

                    downloadInfo.setCurrentPos(current);
                    notifyDownloadProgressChanged(downloadInfo);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(UIUtils.getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    downloadInfo.setCurrentState(DOWNLOAD_ERROR);
                    notifyDownloadStateChanged(downloadInfo);
                }
            });
            downloadHandlerMap.put(downloadInfo.getId(), handler);
        }
    }

    //暂停
    public synchronized void pause(DownloadInfo info) {
        if (downloadInfoMap.containsKey(info.getId())) {
            if (info.getCurrentState() == DOWNLOAD_DOWNLOADING || info.getCurrentState() == DOWNLOAD_WAITING) {
                info.setCurrentState(DOWNLOAD_PAUSE);
                notifyDownloadStateChanged(info);

                DownloadTask downloadTask = downloadTaskMap.get(info.getId());
                if (downloadTask != null) {
                    //移除线程队列
                    //ThreadManager.getThreadPool().cancel(downloadTask);
                    downloadHandlerMap.get(info.getId()).cancel();
                }
            }
        }
    }

    //安装
    public synchronized void install(DownloadInfo info) {
        if (info != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + info.getDownloadPath()), "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    public DownloadInfo getDownloadInfo(String id) {
        return downloadInfoMap.get(id);
    }
}
