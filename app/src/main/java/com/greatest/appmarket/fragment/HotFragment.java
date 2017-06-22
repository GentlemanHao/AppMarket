package com.greatest.appmarket.fragment;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.greatest.appmarket.utils.DrawableUtils;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.view.FlowLayout;

import java.util.Random;


/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class HotFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(UIUtils.getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());

        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding, padding, padding, padding);
        flowLayout.setHorizontalSpacing(UIUtils.dip2px(8));
        flowLayout.setVerticalSpacing(UIUtils.dip2px(10));

        final String[] data = new String[]{"QQ", "视频", "电子书", "酒店", "单机", "小说", "放开那三国", "斗地主", "优酷", "网游", "WIFI万能钥匙", "播放器", "捕鱼达人2", "机票", "游戏", "熊出没之熊大快跑", "美图秀秀", "浏览器", "单机游戏", "我的世界", "电影电视", "QQ空间", "旅游", "免费游戏", "2048", "刀塔传奇", "壁纸", "节奏大师", "锁屏", "装机必备", "天天动听", "备份", "网盘"};

        for (int i = 0; i < data.length; i++) {

            final String keyWord = data[i];

            TextView textView = new TextView(UIUtils.getContext());
            textView.setText(keyWord);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            Random random = new Random();
            int red = 30 + random.nextInt(200);
            int green = 30 + random.nextInt(200);
            int blue = 30 + random.nextInt(200);

            GradientDrawable normal = DrawableUtils.getGradientDrawable(UIUtils.dip2px(6), Color.rgb(red, green, blue));
            GradientDrawable press = DrawableUtils.getGradientDrawable(UIUtils.dip2px(6), 0xffcecece);

            StateListDrawable selector = DrawableUtils.getSelector(normal, press);

            textView.setBackgroundDrawable(selector);
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });

            flowLayout.addView(textView);
        }

        scrollView.addView(flowLayout);
        return scrollView;
    }
}
