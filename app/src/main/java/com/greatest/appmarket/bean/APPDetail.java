package com.greatest.appmarket.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHao on 2017.04.01  0001.
 */

public class APPDetail {

    /**
     * author : 北京千橡网景科技发展有限公司
     * date : 2014-05-20
     * des : 2005-2014 你的校园一直在这儿。中国最大的实名制SNS网络平台，大学生必备网络社交应用。-------我们好像在哪儿见过-------	早春发芽，我在人人通过姓名，学校，找到了从小到大的同学，并加入了校园新圈子	花开半夏，在新鲜事里和好友分享彼此的生活点滴，我们渺小如星辰，却真实存在着	花花世界，这里的人貌似不疯不成活，蛇精病短视频、激萌语音照片，芝麻烂谷飚日志	一叶知秋，喜欢上了每天看人人话题、看世界，公共主页、我知道这个世界有多大我们就得担负多大。	漫天雪花：不知什么时候，习惯上了回顾过去，三千前，五年前，我们无知无畏，无所不能，每个样子，都好像在哪儿见过。
     * downloadNum : 1000万+
     * downloadUrl : app/com.renren.mobile.android/com.renren.mobile.android.apk
     * iconUrl : app/com.renren.mobile.android/icon.jpg
     * id : 1580615
     * name : 人人
     * packageName : com.renren.mobile.android
     * safe : [{"safeDes":"已通过安智市场官方认证，是正版软件","safeDesColor":0,"safeDesUrl":"app/com.renren.mobile.android/safeDesUrl0.jpg","safeUrl":"app/com.renren.mobile.android/safeIcon0.jpg"}]
     * screen : ["app/com.renren.mobile.android/screen0.jpg","app/com.renren.mobile.android/screen1.jpg","app/com.renren.mobile.android/screen2.jpg","app/com.renren.mobile.android/screen3.jpg","app/com.renren.mobile.android/screen4.jpg"]
     * size : 21803987
     * stars : 2
     * version : 7.5.3
     */

    private String author;
    private String date;
    private String des;
    private String downloadNum;
    private String downloadUrl;
    private String iconUrl;
    private String id;
    private String name;
    private String packageName;
    private long size;
    private float stars;
    private String version;
    private ArrayList<SafeBean> safe;
    private ArrayList<String> screen;

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getDes() {
        return des;
    }

    public String getDownloadNum() {
        return downloadNum;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getSize() {
        return size;
    }

    public float getStars() {
        return stars;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<SafeBean> getSafe() {
        return safe;
    }

    public ArrayList<String> getScreen() {
        return screen;
    }

    public static class SafeBean {
        /**
         * safeDes : 已通过安智市场官方认证，是正版软件
         * safeDesColor : 0
         * safeDesUrl : app/com.renren.mobile.android/safeDesUrl0.jpg
         * safeUrl : app/com.renren.mobile.android/safeIcon0.jpg
         */

        private String safeDes;
        private int safeDesColor;
        private String safeDesUrl;
        private String safeUrl;

        public String getSafeDes() {
            return safeDes;
        }

        public int getSafeDesColor() {
            return safeDesColor;
        }

        public String getSafeDesUrl() {
            return safeDesUrl;
        }

        public String getSafeUrl() {
            return safeUrl;
        }
    }
}
