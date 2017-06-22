package com.greatest.appmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.greatest.appmarket.R;
import com.greatest.appmarket.fragment.BaseFragment;
import com.greatest.appmarket.fragment.FragmentFactory;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.view.PagerTab;

public class MainActivity extends BaseActivity {

    private PagerTab pagerTab;
    private ViewPager viewPager;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerTab = (PagerTab) findViewById(R.id.pagertab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(6);

        myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        pagerTab.setViewPager(viewPager);

        pagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = FragmentFactory.creatFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initScan();
        initUser();

    }

    private void initUser() {
        ImageView iv_user = (ImageView) findViewById(R.id.iv_user);
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        ImageView iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initScan() {
        ImageView iv_scan = (ImageView) findViewById(R.id.iv_scan);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.setPrompt("");
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String re = result.getContents();
                Toast.makeText(UIUtils.getContext(), "扫描结果：" + re, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    class MyAdapter extends FragmentPagerAdapter {

        private String[] tab_names;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            tab_names = UIUtils.getStringArray(R.array.tab_names);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_names[position];
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment = FragmentFactory.creatFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return tab_names.length;
        }

    }
}