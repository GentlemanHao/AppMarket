package com.greatest.appmarket.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.activity.AppDetailActivity;
import com.greatest.appmarket.adapter.MyBaseAdapter;
import com.greatest.appmarket.bean.APPInfo;
import com.greatest.appmarket.bean.DownloadInfo;
import com.greatest.appmarket.manager.DownloadManager;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.CacheUtils;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class HomeFragment extends BaseFragment implements DownloadManager.DownloadObserver, View.OnClickListener {

    private ListView lv_home;

    private FrameLayout frameLayout;
    private APPInfo appInfo;
    private APPInfo newAppInfo;

    private String[] urls = {"homelist0", "homelist1", "homelist2", "homelist3"};
    private boolean ISFIRSTLOAD = true;
    private int currentUrl = 0;
    private int pointPosition;
    private HomeAdapter homeAdapter;
    private HttpUtils httpUtils;
    private ArrayList<String> imageList;
    private RelativeLayout relativeLayout;
    private ViewPager viewPager;
    private DownloadManager downloadManager;
    private ViewHolder holder;
    private LinearLayout linearLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showRightPage();
            switch (msg.what) {
                case 0:
                    homeAdapter = new HomeAdapter(appInfo.list);
                    lv_home.setAdapter(homeAdapter);

                    startViewPager();

                    ISFIRSTLOAD = false;
                    currentUrl++;
                    break;
                case 1:
                    if (newAppInfo.list.size() != 0) {
                        appInfo.list.addAll(newAppInfo.list);
                        homeAdapter.notifyDataSetChanged();
                    }

                    ISFIRSTLOAD = false;
                    currentUrl++;
                    break;
                case 2:
                    break;
            }
        }
    };

    private void startViewPager() {
        if (imageList == null) {
            imageList = appInfo.picture;
        }
        viewPager.setAdapter(new TitleAdapter());
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        lv_home.addHeaderView(relativeLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < imageList.size(); i++) {
            ImageView point = new ImageView(UIUtils.getContext());
            if (i == 0) {
                point.setImageResource(R.drawable.indicator_selected);
            } else {
                point.setImageResource(R.drawable.indicator_normal);
                params.leftMargin = UIUtils.dip2px(2);
            }
            point.setLayoutParams(params);
            linearLayout.addView(point);
        }

        TitleTask titleTask = new TitleTask();
        titleTask.start();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position % imageList.size();
                ImageView point = (ImageView) linearLayout.getChildAt(position);
                point.setImageResource(R.drawable.indicator_selected);

                ImageView prePoint = (ImageView) linearLayout.getChildAt(pointPosition);
                prePoint.setImageResource(R.drawable.indicator_normal);

                pointPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(UIUtils.getContext(), R.layout.fragment_home, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        frameLayout = (FrameLayout) view.findViewById(R.id.fl_page);
        frameLayout.addView(lodingPage());

        Button button = (Button) page_error.findViewById(R.id.btn_retry);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentState = STATE_LOADING;
                showRightPage();
                initData();
            }
        });

        lv_home = (ListView) view.findViewById(R.id.lv_home);
        //设置默认状态选择器为全透明
        lv_home.setSelector(new ColorDrawable());
        lv_home.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && view.getLastVisiblePosition() == view.getCount() - 1) {
                    initData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        lv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), AppDetailActivity.class);
                intent.putExtra("packageName", appInfo.list.get(position - 1).getPackageName());
                startActivity(intent);
            }
        });

        initViewPager();
    }

    private void initViewPager() {
        relativeLayout = new RelativeLayout(UIUtils.getContext());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(160));
        relativeLayout.setLayoutParams(params);

        viewPager = new ViewPager(UIUtils.getContext());
        RelativeLayout.LayoutParams vpParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.addView(viewPager, vpParams);

        linearLayout = new LinearLayout(UIUtils.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int padding = UIUtils.dip2px(10);
        linearLayout.setPadding(padding, padding, padding, padding);

        llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeLayout.addView(linearLayout, llParams);
    }

    private void initData() {
        if (currentUrl <= urls.length - 1) {
            new Thread(new Runnable() {

                private String cache;
                Gson gson = new Gson();

                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (httpUtils == null)
                        httpUtils = new HttpUtils();
                    if (ISFIRSTLOAD) {
                        if (currentUrl < 4) {
                            cache = CacheUtils.getCache(urls[currentUrl]);
                        }
                        if (cache == null) {
                            httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + urls[currentUrl], new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    if (responseInfo.result != null) {
                                        cache = responseInfo.result;
                                        appInfo = gson.fromJson(cache, APPInfo.class);

                                        currentState = STATE_SUCCESS;
                                        handler.sendEmptyMessage(0);

                                        CacheUtils.setCache(urls[currentUrl], cache);

                                        //currentUrl++;
                                    } else {
                                        currentState = STATE_EMPTY;
                                        handler.sendEmptyMessage(2);
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    currentState = STATE_ERROR;
                                    handler.sendEmptyMessage(2);
                                }
                            });
                        } else {
                            System.out.println("使用缓存数据...");
                            appInfo = gson.fromJson(cache, APPInfo.class);
                            currentState = STATE_SUCCESS;
                            handler.sendEmptyMessage(0);
                            //currentUrl++;
                        }
                    } else {
                        if (currentUrl < 4) {
                            cache = CacheUtils.getCache(urls[currentUrl]);
                        }
                        if (cache == null && currentUrl < 4) {
                            httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + urls[currentUrl], new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    if (newAppInfo != null) {
                                        newAppInfo.list.clear();
                                    }
                                    cache = responseInfo.result;
                                    newAppInfo = gson.fromJson(cache, APPInfo.class);
                                    handler.sendEmptyMessage(1);

                                    CacheUtils.setCache(urls[currentUrl], cache);

                                    //currentUrl++;
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {

                                }
                            });
                        } else {
                            System.out.println("使用缓存数据...");
                            if (newAppInfo != null) {
                                newAppInfo.list.clear();
                            }
                            newAppInfo = gson.fromJson(cache, APPInfo.class);
                            handler.sendEmptyMessage(1);
                            //currentUrl++;
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo info) {
        //if (downloadInfo.getId().equals(info.getId()))
        updateProgress(info);
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo info) {
        //if (downloadInfo.getId().equals(info.getId()))
        updateProgress(info);
    }

    class HomeAdapter extends MyBaseAdapter<APPInfo.APPData> {

        private BitmapUtils bitmapUtils;
        private final FrameLayout.LayoutParams params;

        public HomeAdapter(ArrayList<APPInfo.APPData> data) {
            super(data);
            bitmapUtils = BitmapHelper.getBitmapUtils();

            params = new FrameLayout.LayoutParams(UIUtils.dip2px(30), UIUtils.dip2px(30));

            downloadManager = DownloadManager.getInstance();
            downloadManager.registerObserver(HomeFragment.this);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(UIUtils.getContext(), R.layout.item_home, null);
                    holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
                    holder.app_download = (Button) convertView.findViewById(R.id.app_download);
                    holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
                    holder.app_des = (TextView) convertView.findViewById(R.id.app_des);
                    holder.app_size = (TextView) convertView.findViewById(R.id.app_size);
                    holder.app_star = (RatingBar) convertView.findViewById(R.id.app_star);
                    holder.donutProgress = (DonutProgress) convertView.findViewById(R.id.donut_progress);
                    holder.donutProgress.setFinishedStrokeWidth(UIUtils.dip2px(5));
                    holder.donutProgress.setUnfinishedStrokeWidth(UIUtils.dip2px(5));
                    holder.donutProgress.setTextSize(UIUtils.dip2px(8));
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                bitmapUtils.display(holder.app_icon, URL.Base_URL + getItem(position).getIconUrl());
                holder.app_name.setText(getItem(position).getName());
                holder.app_des.setText(getItem(position).getDes());
                holder.app_size.setText(Formatter.formatFileSize(UIUtils.getContext(), getItem(position).getSize()));
                holder.app_star.setRating(getItem(position).getStars());

                holder.app_download.setTag(position);
                holder.donutProgress.setTag(position);

                holder.app_download.setOnClickListener(HomeFragment.this);
                holder.donutProgress.setOnClickListener(HomeFragment.this);

                //初始化下载进度条
                showProgress(position);
            } else {
                if (currentUrl > urls.length - 1) {
                    convertView = View.inflate(UIUtils.getContext(), R.layout.item_empty, null);

                } else {
                    convertView = View.inflate(UIUtils.getContext(), R.layout.item_more, null);
                }
            }
            return convertView;
        }
    }

    private void showProgress(int position) {
        DownloadInfo downloadInfo = downloadManager.getDownloadInfo(appInfo.list.get(position).getId());
        updateProgress(downloadInfo);
    }

    private void updateProgress(final DownloadInfo info) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                int downloadState = info != null ? info.getCurrentState() : DownloadManager.DOWNLOAD_UNDO;
                switch (downloadState) {
                    case DownloadManager.DOWNLOAD_UNDO:
                        holder.donutProgress.setVisibility(View.GONE);
                        holder.app_download.setVisibility(View.VISIBLE);
                        holder.app_download.setText("下载");
                        break;
                    case DownloadManager.DOWNLOAD_WAITING:
                        holder.donutProgress.setVisibility(View.GONE);
                        holder.app_download.setVisibility(View.VISIBLE);
                        holder.app_download.setText("排队");
                        break;
                    case DownloadManager.DOWNLOAD_DOWNLOADING:
                        holder.donutProgress.setVisibility(View.VISIBLE);
                        holder.app_download.setVisibility(View.GONE);
                        holder.donutProgress.setMax(100);
                        holder.donutProgress.setProgress(((int) (info.getProgress() * 10000)) / 100);
                        break;
                    case DownloadManager.DOWNLOAD_PAUSE:
                        holder.donutProgress.setVisibility(View.GONE);
                        holder.app_download.setVisibility(View.VISIBLE);
                        holder.app_download.setText("继续");
                        break;
                    case DownloadManager.DOWNLOAD_ERROR:
                        holder.donutProgress.setVisibility(View.GONE);
                        holder.app_download.setVisibility(View.VISIBLE);
                        holder.app_download.setText("重试");
                        break;
                    case DownloadManager.DOWNLOAD_SUCCESS:
                        holder.donutProgress.setVisibility(View.GONE);
                        holder.app_download.setVisibility(View.VISIBLE);
                        holder.app_download.setText("安装");
                        break;
                }
                homeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_download:
            case R.id.donut_progress:
                int position = (int) v.getTag();
                DownloadInfo downloadInfo = downloadManager.getDownloadInfo(appInfo.list.get(position).getId());
                int downloadState = downloadInfo != null ? downloadInfo.getCurrentState() : DownloadManager.DOWNLOAD_UNDO;

                if (downloadState == DownloadManager.DOWNLOAD_UNDO
                        || downloadState == DownloadManager.DOWNLOAD_PAUSE
                        || downloadState == DownloadManager.DOWNLOAD_ERROR) {
                    //开始下载
                    downloadInfo = downloadManager.getDownloadInfo(appInfo.list.get(position).getId(),
                            appInfo.list.get(position).getName(), appInfo.list.get(position).getDownloadUrl(),
                            appInfo.list.get(position).getPackageName(), appInfo.list.get(position).getSize(), appInfo.list.get(position).getIconUrl());
                    downloadManager.download(downloadInfo);
                } else if (downloadState == DownloadManager.DOWNLOAD_DOWNLOADING
                        || downloadState == DownloadManager.DOWNLOAD_WAITING) {
                    //暂停下载
                    downloadManager.pause(downloadInfo);
                } else if (downloadState == DownloadManager.DOWNLOAD_SUCCESS) {
                    downloadManager.install(downloadInfo);
                }
                break;
        }
    }

    class ViewHolder {
        private ImageView app_icon;
        private Button app_download;
        private TextView app_name;
        private TextView app_size;
        private TextView app_des;
        private RatingBar app_star;
        private DonutProgress donutProgress;
    }

    class TitleAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % imageList.size();
            String url = imageList.get(position);
            ImageView imageView = new ImageView(UIUtils.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
            bitmapUtils.display(imageView, URL.Base_URL + url);
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class TitleTask implements Runnable {

        public void start() {
            UIUtils.getHandler().removeCallbacksAndMessages(null);
            UIUtils.getHandler().postDelayed(this, 4000);
        }

        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            UIUtils.getHandler().postDelayed(this, 4000);
        }
    }

    public static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
