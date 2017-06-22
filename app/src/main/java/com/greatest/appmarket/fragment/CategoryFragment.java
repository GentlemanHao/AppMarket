package com.greatest.appmarket.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.CategoryInfo;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class CategoryFragment extends BaseFragment {
    private FrameLayout frameLayout;
    private CategoryInfo categoryInfo;
    private ListView lv_game;
    private BitmapUtils bitmapUtils;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showRightPage();
            switch (msg.what) {
                case 0:
                    lv_game.setAdapter(new CategoryAdapter());
                    break;
                case 1:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(UIUtils.getContext(), R.layout.fragment_category, null);
        initView(view);
        initData();

        return view;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + "Category", new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        System.out.println(responseInfo.result);
                        Gson gson = new Gson();
                        categoryInfo = gson.fromJson(responseInfo.result, CategoryInfo.class);
                        currentState = STATE_SUCCESS;
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        currentState = STATE_ERROR;
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }).start();
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

        lv_game = (ListView) view.findViewById(R.id.lv_game);
    }

    class CategoryAdapter extends BaseAdapter {

        private int TYPE_MORE = 0;
        private int TYPE_NORMAL = 1;

        public CategoryAdapter() {
            bitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public int getCount() {
            return categoryInfo.getInfos().size();
        }

        @Override
        public Object getItem(int position) {
            return categoryInfo.getInfos().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0 || position == 5) ? TYPE_MORE : TYPE_NORMAL;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_MORE) {
                TextView textView = new TextView(UIUtils.getContext());
                textView.setText(categoryInfo.getInfos().get(position).getTitle());
                textView.setPadding(20, 20, 20, 20);
                textView.setTextColor(Color.parseColor("#555555"));
                return textView;
            } else {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(UIUtils.getContext(), R.layout.category_item, null);
                    holder.imageView1 = (ImageView) convertView.findViewById(R.id.category_one);
                    holder.imageView2 = (ImageView) convertView.findViewById(R.id.category_two);
                    holder.imageView3 = (ImageView) convertView.findViewById(R.id.category_three);
                    holder.textView1 = (TextView) convertView.findViewById(R.id.category_one_name);
                    holder.textView2 = (TextView) convertView.findViewById(R.id.category_two_name);
                    holder.textView3 = (TextView) convertView.findViewById(R.id.category_three_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                bitmapUtils.display(holder.imageView1, URL.Base_URL + categoryInfo.getInfos().get(position).getUrl1());
                bitmapUtils.display(holder.imageView2, URL.Base_URL + categoryInfo.getInfos().get(position).getUrl2());
                bitmapUtils.display(holder.imageView3, URL.Base_URL + categoryInfo.getInfos().get(position).getUrl3());

                final String name1 = categoryInfo.getInfos().get(position).getName1();
                final String name2 = categoryInfo.getInfos().get(position).getName2();
                final String name3 = categoryInfo.getInfos().get(position).getName3();

                holder.textView1.setText(name1);
                holder.textView2.setText(name2);
                holder.textView3.setText(name3);

                holder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UIUtils.getContext(), name1, Toast.LENGTH_SHORT).show();
                    }
                });
                holder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UIUtils.getContext(), name2, Toast.LENGTH_SHORT).show();
                    }
                });
                holder.imageView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UIUtils.getContext(), name3, Toast.LENGTH_SHORT).show();
                    }
                });

                return convertView;
            }
        }
    }

    class ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
