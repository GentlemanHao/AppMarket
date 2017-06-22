package com.greatest.appmarket.bean;

import java.util.ArrayList;

/**
 * Created by WangHao on 2017.02.27  0027.
 */

public class APPInfo {
    public ArrayList<String> picture;
    public ArrayList<APPData> list;

    public class APPData {
        public String id;
        public String name;
        public String packageName;
        public String iconUrl;
        public float stars;
        public long size;
        public String downloadUrl;
        public String des;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public float getStars() {
            return stars;
        }

        public long getSize() {
            return size;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getDes() {
            return des;
        }
    }
}
