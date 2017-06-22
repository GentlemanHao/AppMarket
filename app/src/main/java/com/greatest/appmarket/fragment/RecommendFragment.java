package com.greatest.appmarket.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.view.fly.ShakeListener;
import com.greatest.appmarket.view.fly.StellarMap;

import java.util.Random;


/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class RecommendFragment extends BaseFragment {

    private String[] data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        initData();
        stellarMap.setAdapter(new RecommendAdapter());
        //把界面划分为6*9的格子，随机推荐
        stellarMap.setRegularity(6, 9);
        int padding = UIUtils.dip2px(10);
        stellarMap.setInnerPadding(padding, padding, padding, padding);
        //设置默认页面
        stellarMap.setGroup(0, true);

        ShakeListener shakeListener = new ShakeListener(UIUtils.getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();
            }
        });

        return stellarMap;
    }

    private void initData() {
        data = new String[]{"QQ", "视频", "电子书", "酒店", "单机", "小说", "放开那三国", "斗地主", "优酷", "网游", "WIFI万能钥匙", "播放器", "捕鱼达人2", "机票", "游戏", "熊出没之熊大快跑", "美图秀秀", "浏览器", "单机游戏", "我的世界", "电影电视", "QQ空间", "旅游", "免费游戏", "2048", "刀塔传奇", "壁纸", "节奏大师", "锁屏", "装机必备", "天天动听", "备份", "网盘"};

    }

    class RecommendAdapter implements StellarMap.Adapter {

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getCount(int group) {
            int count = data.length / getGroupCount();
            if (group == getGroupCount() - 1) {
                count += data.length % getGroupCount();
            }
            return count;
        }

        @Override
        public View getView(int group, int position, View convertView) {
            position += group * getCount(group - 1);
            TextView textView = new TextView(UIUtils.getContext());
            final String keyWord = data[position];
            textView.setText(keyWord);

            Random random = new Random();
            //随机大小
            int size = 16 + random.nextInt(10);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            //随机颜色
            int red = 30 + random.nextInt(200);
            int green = 30 + random.nextInt(200);
            int blue = 30 + random.nextInt(200);
            textView.setTextColor(Color.rgb(red, green, blue));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });

            return textView;
        }

        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            /*if (isZoomIn) {
                group = group > 0 ? group-- : getGroupCount() - 1;
            } else {
                group = group < getGroupCount() - 1 ? group++ : 0;
            }
            System.out.println("group:" + group);*/
            return group == 0 ? 1 : 0;
        }
    }
}
