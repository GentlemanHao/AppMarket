package com.greatest.appmarket.activity;

import android.content.Intent;
import android.media.effect.Effect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greatest.appmarket.R;
import com.greatest.appmarket.bean.APPInfo;
import com.greatest.appmarket.utils.BitmapHelper;
import com.greatest.appmarket.utils.UIUtils;
import com.greatest.appmarket.utils.URL;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText et_search;
    private StringBuffer voiceBuffer;
    private String[] urls = {"homelist1", "homelist2", "homelist3"};
    private List<APPInfo.APPData> appList = new ArrayList<>();
    private List<APPInfo.APPData> searchList = new ArrayList<>();
    private ListView lv_search;
    private TextView tv_nosearch;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58f5d64a");

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils httpUtils = new HttpUtils();
                final Gson gson = new Gson();
                for (int i = 0; i < urls.length; i++) {
                    httpUtils.send(HttpRequest.HttpMethod.GET, URL.APP_URL + urls[i], new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            APPInfo appInfo = gson.fromJson(responseInfo.result, APPInfo.class);
                            appList.addAll(appInfo.list);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(SearchActivity.this, "搜索出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        initView();
    }

    private void initView() {

        et_search = (EditText) findViewById(R.id.et_search);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });

        ImageView iv_voice = (ImageView) findViewById(R.id.iv_voice);
        iv_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startVoice();
            }
        });

        TextView tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });

        tv_nosearch = (TextView) findViewById(R.id.tv_nosearch);
        lv_search = (ListView) findViewById(R.id.lv_search);
        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, AppDetailActivity.class);
                intent.putExtra("packageName", searchList.get(position).getPackageName());
                startActivity(intent);
            }
        });
    }

    private void startSearch() {
        searchList.clear();
        String keyWord = et_search.getText().toString().trim();
        if (!TextUtils.isEmpty(keyWord)) {
            for (int i = 0; i < appList.size(); i++) {
                if (appList.get(i).getName().contains(keyWord)) {
                    searchList.add(appList.get(i));
                }
            }
            searchAdapter = new SearchAdapter();
            lv_search.setAdapter(searchAdapter);
            if (searchList.size() > 0) {
                searchAdapter.notifyDataSetChanged();
                lv_search.setVisibility(View.VISIBLE);
                tv_nosearch.setVisibility(View.GONE);
            } else {
                searchAdapter.notifyDataSetChanged();
                lv_search.setVisibility(View.GONE);
                tv_nosearch.setVisibility(View.VISIBLE);
            }
        }
    }

    class SearchAdapter extends BaseAdapter {

        BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();

        @Override
        public int getCount() {
            return searchList.size();
        }

        @Override
        public Object getItem(int position) {
            return searchList.get(position);
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
                convertView = View.inflate(UIUtils.getContext(), R.layout.item_home, null);
                holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
                holder.app_des = (TextView) convertView.findViewById(R.id.app_des);
                holder.app_size = (TextView) convertView.findViewById(R.id.app_size);
                holder.app_star = (RatingBar) convertView.findViewById(R.id.app_star);
                holder.app_download = (Button) convertView.findViewById(R.id.app_download);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            bitmapUtils.display(holder.app_icon, URL.Base_URL + searchList.get(position).getIconUrl());
            holder.app_name.setText(searchList.get(position).getName());
            holder.app_des.setText(searchList.get(position).getDes());
            holder.app_size.setText(Formatter.formatFileSize(UIUtils.getContext(), searchList.get(position).getSize()));
            holder.app_star.setRating(searchList.get(position).getStars());
            holder.app_download.setVisibility(View.GONE);

            return convertView;
        }
    }

    class ViewHolder {
        private ImageView app_icon;
        private TextView app_name;
        private TextView app_size;
        private TextView app_des;
        private Button app_download;
        private RatingBar app_star;
    }

    public void startVoice() {
        //1.创建SpeechRecognizer对象，第二个参数：本地识别时传InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(SearchActivity.this, null);
//2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
//3.开始听写

        mIat.startListening(mRecoListener);

    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录13.1)；
//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//关于解析Json的代码可参见Demo中JsonParser类；
//isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("wwwwwwwwwwwwww", "result:" + results.getResultString());
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
//打印错误码描述
            Log.d("wwwwwwwwwwwwww", "error:" + error.getPlainDescription(true));
        }

        //开始录音
        public void onBeginOfSpeech() {
        }

        //volume音量值0~30，data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
        }

        //结束录音
        public void onEndOfSpeech() {
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

}