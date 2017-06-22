package com.greatest.appmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.greatest.appmarket.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;


import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class ScanActivity extends Activity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean flag = true;
    private String photo_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.viewfinder_view);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        setClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void setClick() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_torch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light();
            }
        });

        findViewById(R.id.mo_scanner_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
                if (Build.VERSION.SDK_INT < 19) {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    // innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);  这个方法报 图片地址 空指针；使用下面的方法
                    innerIntent.setAction(Intent.ACTION_PICK);
                }

                innerIntent.setType("image/*");

                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");

                startActivityForResult(wrapperIntent, REQUEST_CODE);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_CODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);

                    if (cursor.moveToFirst()) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        if (photo_path == null) {
                            //photo_path = ImagePathUtil.getPath(getApplicationContext(),
                            //data.getData());
                        }
                    }

                    cursor.close();

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Looper.prepare();
                                Toast.makeText(ScanActivity.this, "错误", Toast.LENGTH_SHORT)
                                        .show();
                                Looper.loop();
                            } else {
                                // 数据返回
                                String recode = recode(result.toString());
                                Intent data = new Intent();
                                data.putExtra("recodeResult", recode);
                                setResult(0, data);
                                finish();
                            }
                        }
                    }).start();
                    break;

            }

        }

    }


    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        int[] pixels = new int[width * height];

        scanBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
        return null;

    }

    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    protected void light() {
        if (flag) {
            flag = false;
            // 开闪光灯
            barcodeScannerView.setTorchOn();
        } else {
            flag = true;
            // 关闪光灯
            barcodeScannerView.setTorchOff();
        }

    }
}
