package com.greatest.appmarket.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.DownloadRecord;
import com.greatest.appmarket.db.DownloadDao;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.SpUtil;
import com.greatest.appmarket.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;


import java.util.List;

public class DownloadInfoActivity extends AppCompatActivity {

    private List<DownloadRecord> downloadRecords;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DownloadRecordAdapter downloadRecordAdapter = new DownloadRecordAdapter();
                    listView.setAdapter(downloadRecordAdapter);
                    break;
            }
        }
    };
    private ListView listView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SpUtil.getBoolean(UIUtils.getContext(), "ISLOGIN", false)) {
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                DownloadDao downloadDao = DownloadDao.getInstance(getApplication());
                downloadRecords = downloadDao.findAll();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_download);

        textView = (TextView) findViewById(R.id.tv_login_now);
        button = (Button) findViewById(R.id.bt_login_now);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DownloadInfoActivity.this, LoginActivity.class));
            }
        });

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadInfoActivity.this.finish();
            }
        });
    }

    class DownloadRecordAdapter extends BaseAdapter {

        private BitmapUtils bitmapUtils;

        public DownloadRecordAdapter() {
            bitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public int getCount() {
            return downloadRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return downloadRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.item_download, null);
                holder.download_icon = (ImageView) convertView.findViewById(R.id.download_icon);
                holder.download_name = (TextView) convertView.findViewById(R.id.download_name);
                holder.download_size = (TextView) convertView.findViewById(R.id.download_size);
                holder.download_time = (TextView) convertView.findViewById(R.id.download_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            bitmapUtils.display(holder.download_icon, downloadRecords.get(position).iconurl);
            holder.download_name.setText(downloadRecords.get(position).name);
            holder.download_size.setText(downloadRecords.get(position).size);
            holder.download_time.setText(downloadRecords.get(position).time);
            return convertView;
        }
    }

    class ViewHolder {
        private ImageView download_icon;
        private TextView download_name;
        private TextView download_size;
        private TextView download_time;
    }
}
