package com.greatest.appmarket.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by WangHao on 2017.03.05  0005.
 */

public class CacheUtils {
    public static void setCache(String url, String json) {
        File cacheDir = UIUtils.getContext().getCacheDir();
        //生成缓存文件
        File cacheFile = new File(cacheDir, url);
        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile);
            //缓存失效时间
            long deadTime = System.currentTimeMillis() + 30 * 60 * 1000;
            writer.write(deadTime + "\n");

            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCache(String url) {
        File cacheDir = UIUtils.getContext().getCacheDir();
        File cacheFile = new File(cacheDir, url);
        BufferedReader reader = null;
        if (cacheFile.exists()) {
            //判断缓存是否有效
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                long deadTime = Long.parseLong(reader.readLine());
                if (System.currentTimeMillis() < deadTime) {
                    //缓存有效
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    return buffer.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
