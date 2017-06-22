package com.greatest.appmarket.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.APPDetail;
import com.greatest.appmarket.bean.DownloadInfo;
import com.greatest.appmarket.manager.DownloadManager;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.greatest.appmarket.view.ObservableScrollView;
import com.greatest.appmarket.view.ProgressHorizontal;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class AppDetailActivity extends AppCompatActivity implements DownloadManager.DownloadObserver, View.OnClickListener {

    private String packageName;
    private APPDetail appDetail;
    private ImageView app_icon;
    private TextView app_name;
    private RatingBar app_star;
    private TextView app_score;
    private TextView app_size;
    private TextView app_downloadNum;
    private LinearLayout ll_safe;
    private TextView app_author;
    private TextView app_des;
    private TextView app_author2;
    private TextView app_date;
    private TextView app_version;

    private LinearLayout ll_pic;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showData();
                    break;
            }
        }
    };
    private TextView app_title;
    private DownloadManager downloadManager;
    private int currentState;
    private float currentPos;
    private FrameLayout fl_progress;
    private Button bt_download;
    private ProgressHorizontal progress;
    private DownloadInfo downloadInfo;

    private void showData() {
        //应用基本信息
        BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
        bitmapUtils.display(app_icon, URL.Base_URL + appDetail.getIconUrl());
        app_name.setText(appDetail.getName());
        app_star.setRating(appDetail.getStars());
        app_score.setText(appDetail.getStars() + "");
        app_size.setText(Formatter.formatFileSize(UIUtils.getContext(), appDetail.getSize()));
        app_downloadNum.setText(appDetail.getDownloadNum() + "安装");
        app_author.setText(appDetail.getAuthor());

        //标题
        app_title.setText(appDetail.getName());

        //应用安全图标
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (UIUtils.dip2px(50), UIUtils.dip2px(22));
        for (int i = 0; i < appDetail.getSafe().size(); i++) {
            ImageView imageView = new ImageView(this);
            if (i > 0) {
                layoutParams.leftMargin = UIUtils.dip2px(10);
            }
            imageView.setLayoutParams(layoutParams);
            bitmapUtils.display(imageView, URL.Base_URL + appDetail.getSafe().get(i).getSafeUrl());
            ll_safe.addView(imageView);
        }

        //应用截图
        LinearLayout.LayoutParams paramsNormal = new LinearLayout.LayoutParams
                (UIUtils.dip2px(120), UIUtils.dip2px(200));
        LinearLayout.LayoutParams paramsAfter = new LinearLayout.LayoutParams
                (UIUtils.dip2px(120), UIUtils.dip2px(200));
        paramsNormal.leftMargin = UIUtils.dip2px(20);
        for (int i = 0; i < appDetail.getScreen().size(); i++) {
            ImageView imageView = new ImageView(this);
            if (i == appDetail.getScreen().size() - 1) {
                paramsAfter.leftMargin = UIUtils.dip2px(20);
                paramsAfter.rightMargin = UIUtils.dip2px(20);
                imageView.setLayoutParams(paramsAfter);
            } else {
                imageView.setLayoutParams(paramsNormal);
            }
            bitmapUtils.display(imageView, URL.Base_URL + appDetail.getScreen().get(i));
            ll_pic.addView(imageView);
            final int currentItem = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppDetailActivity.this, ImageDetailActivity.class);
                    intent.putStringArrayListExtra("imageList", appDetail.getScreen());
                    intent.putExtra("currentItem", currentItem);
                    startActivity(intent);
                }
            });
        }

        //应用介绍
        app_des.setText(appDetail.getDes());
        app_des.setEllipsize(TextUtils.TruncateAt.END);
        app_des.setMaxLines(3);
        app_des.setOnClickListener(new View.OnClickListener() {
            boolean showAll = false;

            @Override
            public void onClick(View v) {
                if (!showAll) {
                    app_des.setEllipsize(null);
                    app_des.setSingleLine(false);
                    showAll = true;
                } else {
                    app_des.setEllipsize(TextUtils.TruncateAt.END);
                    app_des.setMaxLines(3);
                    showAll = false;
                }
            }
        });

        //应用信息
        app_author2.setText(appDetail.getAuthor());
        app_date.setText(appDetail.getDate());
        app_version.setText(appDetail.getVersion());

        //显示进度条状态
        showProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        //初始化一键分享
        ShareSDK.initSDK(AppDetailActivity.this);

        app_icon = (ImageView) findViewById(R.id.ad_iv_icon);
        app_name = (TextView) findViewById(R.id.ad_tv_name);
        app_star = (RatingBar) findViewById(R.id.ad_rb_star);
        app_score = (TextView) findViewById(R.id.ad_tv_score);
        app_size = (TextView) findViewById(R.id.ad_tv_size);
        app_downloadNum = (TextView) findViewById(R.id.ad_tv_downloadNum);

        app_author = (TextView) findViewById(R.id.ad_tv_author);
        app_des = (TextView) findViewById(R.id.ad_tv_des);

        app_author2 = (TextView) findViewById(R.id.ad_tv_author2);
        app_date = (TextView) findViewById(R.id.ad_tv_date);
        app_version = (TextView) findViewById(R.id.ad_tv_version);

        app_title = (TextView) findViewById(R.id.ad_tv_title);

        ll_safe = (LinearLayout) findViewById(R.id.ll_safe);

        ll_pic = (LinearLayout) findViewById(R.id.ll_pic);

        //初始化下载进度条
        initProgress();

        ImageView iv_back = (ImageView) findViewById(R.id.ad_iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailActivity.this.finish();
            }
        });
        ImageView iv_share = (ImageView) findViewById(R.id.ad_iv_share);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSDK.initSDK(AppDetailActivity.this);
                OnekeyShare oks = new OnekeyShare();
//关闭sso授权
                oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
                oks.setTitle("Android应用市场");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
                oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段
                oks.setText("我是分享文本");
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://sharesdk.cn");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
                oks.show(AppDetailActivity.this);
            }
        });

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
        scrollView.setOnScollChangedListener(new ObservableScrollView.OnScollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y >= 180) {
                    app_title.setVisibility(View.VISIBLE);
                } else {
                    app_title.setVisibility(View.GONE);
                }
            }
        });

        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");
        initData();
    }

    private void initProgress() {

        bt_download = (Button) findViewById(R.id.ad_bt_download);
        bt_download.setOnClickListener(this);

        fl_progress = (FrameLayout) findViewById(R.id.fl_progress);
        fl_progress.setOnClickListener(this);
        progress = new ProgressHorizontal(AppDetailActivity.this);
        progress.setProgressBackgroundResource(R.drawable.bt_download_bggg);
        progress.setProgressResource(R.drawable.bt_download_bg);
        progress.setProgressTextColor(Color.WHITE);
        progress.setProgressTextSize(UIUtils.dip2px(16));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        fl_progress.addView(progress, params);

        downloadManager = DownloadManager.getInstance();
        downloadManager.registerObserver(this);
    }


    private void showProgress() {
        if (appDetail != null) {
            downloadInfo = downloadManager.getDownloadInfo(appDetail.getId());
            if (downloadInfo != null) {
                //之前下载过
                currentState = downloadInfo.getCurrentState();
                currentPos = downloadInfo.getProgress();
            } else {
                //没有下载过
                currentState = DownloadManager.DOWNLOAD_UNDO;
                currentPos = 0;
            }
        }

        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                switch (currentState) {
                    case DownloadManager.DOWNLOAD_UNDO:
                        fl_progress.setVisibility(View.GONE);
                        bt_download.setVisibility(View.VISIBLE);
                        bt_download.setText("下 载");
                        break;
                    case DownloadManager.DOWNLOAD_WAITING:
                        fl_progress.setVisibility(View.GONE);
                        bt_download.setVisibility(View.VISIBLE);
                        bt_download.setText("等 待 下 载");
                        break;
                    case DownloadManager.DOWNLOAD_DOWNLOADING:
                        fl_progress.setVisibility(View.VISIBLE);
                        bt_download.setVisibility(View.GONE);
                        progress.setCenterText("");
                        progress.setProgress(currentPos);

                        break;
                    case DownloadManager.DOWNLOAD_PAUSE:
                        fl_progress.setVisibility(View.VISIBLE);
                        bt_download.setVisibility(View.GONE);
                        progress.setCenterText("继 续");
                        break;
                    case DownloadManager.DOWNLOAD_ERROR:
                        fl_progress.setVisibility(View.GONE);
                        bt_download.setVisibility(View.VISIBLE);
                        bt_download.setText("下 载 失 败");
                        break;
                    case DownloadManager.DOWNLOAD_SUCCESS:
                        fl_progress.setVisibility(View.GONE);
                        bt_download.setVisibility(View.VISIBLE);
                        bt_download.setText("安 装");
                        break;
                }
            }
        });
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + packageName + "/" + packageName, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Gson gson = new Gson();
                        appDetail = gson.fromJson(responseInfo.result, APPDetail.class);
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        }).start();
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo info) {
        //if (info.getId().equals(downloadInfo.getId()))
        showProgress();
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo info) {
        //if (info.getId().equals(downloadInfo.getId()))
        showProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_bt_download:
            case R.id.fl_progress:
                if (currentState == DownloadManager.DOWNLOAD_UNDO
                        || currentState == DownloadManager.DOWNLOAD_PAUSE
                        || currentState == DownloadManager.DOWNLOAD_ERROR) {
                    //开始下载
                    downloadInfo = downloadManager.getDownloadInfo(appDetail.getId(), appDetail.getName(),
                            appDetail.getDownloadUrl(), appDetail.getPackageName(), appDetail.getSize(), appDetail.getIconUrl());
                    downloadManager.download(downloadInfo);
                } else if (currentState == DownloadManager.DOWNLOAD_DOWNLOADING
                        || currentState == DownloadManager.DOWNLOAD_WAITING) {
                    //暂停下载
                    downloadManager.pause(downloadInfo);
                } else if (currentState == DownloadManager.DOWNLOAD_SUCCESS) {
                    downloadManager.install(downloadInfo);
                }
                break;
        }
    }
}
