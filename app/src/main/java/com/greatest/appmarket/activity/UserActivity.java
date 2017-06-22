package com.greatest.appmarket.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greatest.appmarket.R;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.SpUtil;
import com.greatest.appmarket.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

public class UserActivity extends AppCompatActivity {

    private TextView tv_username;
    private ImageView iv_usericon;
    private ImageView iv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SpUtil.getBoolean(UIUtils.getContext(), "ISLOGIN", false)) {
            tv_username.setText(SpUtil.getString(UIUtils.getContext(), "USERNAME", "您还没登录"));
            BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
            bitmapUtils.display(iv_usericon, com.greatest.appmarket.utils.URL.IMAGE_URL + SpUtil.getString(UIUtils.getContext(), "USERICON", ""));
        }
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.this.finish();
            }
        });

        RelativeLayout rl_download = (RelativeLayout) findViewById(R.id.rl_download);
        rl_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, DownloadInfoActivity.class));
            }
        });

        RelativeLayout rl_install = (RelativeLayout) findViewById(R.id.rl_install);
        rl_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, AppInstallActivity.class));
            }
        });

        RelativeLayout rl_loginexit = (RelativeLayout) findViewById(R.id.rl_loginexit);
        rl_loginexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                builder.setMessage("确认退出登录吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SpUtil.putBoolean(UserActivity.this, "ISLOGIN", false);
                        tv_username.setText("您还没登录");
                        iv_usericon.setImageResource(R.drawable.user_icon_off);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                builder.create().show();
            }
        });

        iv_login = (ImageView) findViewById(R.id.iv_login);
        iv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(UserActivity.this, LoginActivity.class), 0);
                startActivity(new Intent(UserActivity.this, LoginActivity.class));
            }
        });

        tv_username = (TextView) findViewById(R.id.tv_username);
        iv_usericon = (ImageView) findViewById(R.id.iv_usericon);
        //iv_login.setVisibility(View.GONE);

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 0) {
                tv_username.setText(data.getStringExtra("username"));
                BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
                bitmapUtils.display(iv_usericon, com.greatest.appmarket.utils.URL.IMAGE_URL + data.getStringExtra("usericon"));

                //iv_login.setVisibility(View.GONE);
            }
        }
    }*/

}
