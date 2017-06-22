package com.greatest.appmarket.fragment;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.adapter.MyBaseAdapter;
import com.greatest.appmarket.bean.SubjectInfo;
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

public class SubjectFragment extends BaseFragment {
    private FrameLayout frameLayout;
    private ListView lv_subject;

    private SubjectInfo subjectInfo;
    private SubjectInfo newSubjectInfo;

    private String[] urls = {"subjectlist0", "subjectlist1", "subjectlist2", "subjectlist3"};
    private boolean ISFIRSTLOAD = true;
    private int currentUrl = 0;
    private HttpUtils httpUtils;
    private SubjectAdapter subjectAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showRightPage();
            switch (msg.what) {
                case 0:
                    subjectAdapter = new SubjectAdapter(subjectInfo.list);
                    lv_subject.setAdapter(subjectAdapter);
                    ISFIRSTLOAD = false;
                    break;
                case 1:
                    if (newSubjectInfo.list.size() != 0) {
                        subjectInfo.list.addAll(newSubjectInfo.list);
                        subjectAdapter.notifyDataSetChanged();
                    }
                    ISFIRSTLOAD = false;
                    break;
                case 2:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UIUtils.inflate(R.layout.fragment_subject);
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

        lv_subject = (ListView) view.findViewById(R.id.lv_subject);
        //设置默认状态选择器为全透明
        lv_subject.setSelector(new ColorDrawable());
        lv_subject.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                        subjectInfo = gson.fromJson(cache, SubjectInfo.class);
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
                            subjectInfo = gson.fromJson(cache, SubjectInfo.class);
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
                                    if (newSubjectInfo != null) {
                                        newSubjectInfo.list.clear();
                                    }
                                    cache = responseInfo.result;
                                    newSubjectInfo = gson.fromJson(cache, SubjectInfo.class);
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
                            if (newSubjectInfo != null) {
                                newSubjectInfo.list.clear();
                            }
                            newSubjectInfo = gson.fromJson(cache, SubjectInfo.class);
                            handler.sendEmptyMessage(1);
                            currentUrl++;
                        }
                    }
                }
            }).start();
        }
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo.SubjectData> {

        private BitmapUtils bitmapUtils;

        public SubjectAdapter(ArrayList<SubjectInfo.SubjectData> data) {
            super(data);
            bitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                SubjectFragment.ViewHolder holder;
                if (convertView == null) {
                    holder = new SubjectFragment.ViewHolder();
                    convertView = View.inflate(UIUtils.getContext(), R.layout.item_subject, null);
                    holder.sb_icon = (ImageView) convertView.findViewById(R.id.sb_icon);
                    holder.sb_title = (TextView) convertView.findViewById(R.id.sb_title);
                    convertView.setTag(holder);
                } else {
                    holder = (SubjectFragment.ViewHolder) convertView.getTag();
                }
                bitmapUtils.display(holder.sb_icon, URL.Base_URL + getItem(position).getUrl());
                holder.sb_title.setText(getItem(position).getDes());
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
        private ImageView sb_icon;
        private TextView sb_title;
    }
}