package com.greatest.appmarket.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.InstallApp;
import com.greatest.appmarket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class AppInstallActivity extends AppCompatActivity {

    private List<InstallApp> installApps = new ArrayList<>();
    private ListView lv_install;
    private InstallAppAdapter installAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_install);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        installApps = UIUtils.getAppInfoList(UIUtils.getContext());
        installAppAdapter.notifyDataSetChanged();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInstallActivity.this.finish();
            }
        });

        installApps = UIUtils.getAppInfoList(UIUtils.getContext());
        lv_install = (ListView) findViewById(R.id.lv_install);
        installAppAdapter = new InstallAppAdapter();
        lv_install.setAdapter(installAppAdapter);
    }

    class InstallAppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return installApps.size();
        }

        @Override
        public Object getItem(int position) {
            return installApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(UIUtils.getContext(), R.layout.item_install, null);
                holder.app_icon = (ImageView) convertView.findViewById(R.id.install_icon);
                holder.app_name = (TextView) convertView.findViewById(R.id.install_name);
                holder.app_size = (TextView) convertView.findViewById(R.id.install_size);
                holder.app_uninstall = (Button) convertView.findViewById(R.id.install_un);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.app_icon.setImageDrawable(installApps.get(position).getIcon());
            holder.app_name.setText(installApps.get(position).getName());
            holder.app_size.setText(Formatter.formatFileSize(UIUtils.getContext(), installApps.get(position).getSize()));
            holder.app_uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + installApps.get(position).getPackageName()));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        private ImageView app_icon;
        private TextView app_name;
        private TextView app_size;
        private Button app_uninstall;
    }
}
