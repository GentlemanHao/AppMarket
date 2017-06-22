package com.greatest.appmarket.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.UserInfo;
import com.greatest.appmarket.utils.SpUtil;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView iv_cancel = (ImageView) findViewById(R.id.iv_login_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        et_username.setText(SpUtil.getString(UIUtils.getContext(), "LOGINUSERNAME", ""));
        et_password.setText(SpUtil.getString(UIUtils.getContext(), "LOGINPASSWORD", ""));

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {

            private UserInfo userInfo;

            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new HttpUtils().send(HttpRequest.HttpMethod.GET, URL.APP_URL + "userinfo", new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                if (responseInfo.result != null) {
                                    userInfo = new Gson().fromJson(responseInfo.result, UserInfo.class);
                                    if (username.equals(userInfo.getUserid()) && password.equals(userInfo.getPassword())) {
                                        /*Intent intent = new Intent();
                                        intent.putExtra("username", userInfo.getUsername());
                                        intent.putExtra("usericon", userInfo.getUsericon());
                                        setResult(1, intent);*/

                                        SpUtil.putString(UIUtils.getContext(), "LOGINUSERNAME", username);
                                        SpUtil.putString(UIUtils.getContext(), "LOGINPASSWORD", password);
                                        SpUtil.putBoolean(UIUtils.getContext(), "ISLOGIN", true);
                                        SpUtil.putString(UIUtils.getContext(), "USERNAME", userInfo.getUsername());
                                        SpUtil.putString(UIUtils.getContext(), "USERICON", userInfo.getUsericon());

                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                        et_password.setText("");
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {

                            }
                        });
                    }
                }).start();
            }
        });
    }
}
