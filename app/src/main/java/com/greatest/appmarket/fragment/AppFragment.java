package com.greatest.appmarket.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.adapter.MyBaseAdapter;
import com.greatest.appmarket.bean.APPBean;
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

public class AppFragment extends BaseFragment {

    private FrameLayout frameLayout;
    private ListView lv_app;
    private APPBean appBean;
    private APPBean newAppBean;
    private APPAdapter appAdapter;

    private String[] urls = {"applist1", "applist2", "applist3"};
    private boolean ISFIRSTLOAD = true;
    private int currentUrl = 0;
    private HttpUtils httpUtils;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showRightPage();
            switch (msg.what) {
                case 0:
                    appAdapter = new APPAdapter(appBean.list);
                    lv_app.setAdapter(appAdapter);
                    ISFIRSTLOAD = false;
                    break;
                case 1:
                    if (newAppBean.list.size() != 0) {
                        appBean.list.addAll(newAppBean.list);
                        appAdapter.notifyDataSetChanged();
                    }
                    ISFIRSTLOAD = false;
                    break;
                case 2:
                    break;
            }
        }
    };

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(UIUtils.getContext(), R.layout.fragment_app, null);
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
                initData();
                currentState = STATE_LOADING;
                showRightPage();
            }
        });

        lv_app = (ListView) view.findViewById(R.id.lv_app);
        //设置默认状态选择器为全透明
        lv_app.setSelector(new ColorDrawable());
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
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
    }

    private void initData() {
        if (currentUrl <= urls.length - 1) {
            new Thread(new Runnable() {

                private String cache;
                Gson gson = new Gson();

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (httpUtils == null)
                        httpUtils = new HttpUtils();

                    if (ISFIRSTLOAD) {
                        cache = currentUrl > urls.length - 1 ? null : CacheUtils.getCache(urls[currentUrl]);
                        if (cache == null) {
                            httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + urls[currentUrl], new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    if (responseInfo.result != null) {
                                        cache = responseInfo.result;
                                        appBean = gson.fromJson(cache, APPBean.class);
                                        currentState = STATE_SUCCESS;
                                        handler.sendEmptyMessage(0);

                                        CacheUtils.setCache(urls[currentUrl], cache);

                                        currentUrl++;
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
                            appBean = gson.fromJson(cache, APPBean.class);
                            currentState = STATE_SUCCESS;
                            handler.sendEmptyMessage(0);
                            currentUrl++;
                        }

                    } else {
                        cache = currentUrl > urls.length - 1 ? null : CacheUtils.getCache(urls[currentUrl]);
                        if (cache == null) {
                            httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + urls[currentUrl], new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    if (newAppBean != null) {
                                        newAppBean.list.clear();
                                    }
                                    cache = responseInfo.result;
                                    newAppBean = gson.fromJson(cache, APPBean.class);
                                    handler.sendEmptyMessage(1);

                                    CacheUtils.setCache(urls[currentUrl], cache);

                                    currentUrl++;
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {

                                }
                            });
                        } else {
                            System.out.println("使用缓存数据...");
                            if (newAppBean != null) {
                                newAppBean.list.clear();
                            }
                            newAppBean = gson.fromJson(cache, APPBean.class);
                            handler.sendEmptyMessage(1);
                            currentUrl++;
                        }
                    }
                }
            }).start();
        }
    }

    class APPAdapter extends MyBaseAdapter<APPBean.APPData> {

        private BitmapUtils bitmapUtils;

        public APPAdapter(ArrayList<APPBean.APPData> data) {
            super(data);
            bitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                AppFragment.ViewHolder holder;
                if (convertView == null) {
                    holder = new AppFragment.ViewHolder();
                    convertView = View.inflate(UIUtils.getContext(), R.layout.item_home, null);
                    holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
                    holder.app_download = (Button) convertView.findViewById(R.id.app_download);
                    holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
                    holder.app_des = (TextView) convertView.findViewById(R.id.app_des);
                    holder.app_size = (TextView) convertView.findViewById(R.id.app_size);
                    holder.app_star = (RatingBar) convertView.findViewById(R.id.app_star);
                    convertView.setTag(holder);
                } else {
                    holder = (AppFragment.ViewHolder) convertView.getTag();
                }
                bitmapUtils.display(holder.app_icon, URL.Base_URL + getItem(position).getIconUrl());
                holder.app_name.setText(getItem(position).getName());
                holder.app_des.setText(getItem(position).getDes());
                holder.app_size.setText(Formatter.formatFileSize(UIUtils.getContext(), getItem(position).getSize()));
                holder.app_star.setRating(getItem(position).getStars());
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

    class ViewHolder {
        private ImageView app_icon;
        private Button app_download;
        private TextView app_name;
        private TextView app_size;
        private TextView app_des;
        private RatingBar app_star;
    }
}
