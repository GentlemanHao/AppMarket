package com.greatest.appmarket.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.greatest.appmarket.R;
import com.greatest.appmarket.fragment.HomeFragment;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.URL;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

public class ImageDetailActivity extends AppCompatActivity {

    private ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent intent = getIntent();
        imageList = intent.getStringArrayListExtra("imageList");
        int currentItem = intent.getIntExtra("currentItem", 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.id_viewPager);
        if (imageList != null) {
            viewPager.setAdapter(new ImageAdapter());
        }
        viewPager.setPageTransformer(true, new HomeFragment.DepthPageTransformer());
        viewPager.setCurrentItem(currentItem);
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(ImageDetailActivity.this);
            BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
            bitmapUtils.display(imageView, URL.Base_URL + imageList.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDetailActivity.this.finish();
                }
            });

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
