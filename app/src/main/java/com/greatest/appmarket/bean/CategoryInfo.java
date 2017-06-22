package com.greatest.appmarket.bean;

import java.util.ArrayList;

/**
 * Created by WangHao on 2017.03.17  0017.
 */

public class CategoryInfo {

    private ArrayList<CategoryData> infos;

    public ArrayList<CategoryData> getInfos() {
        return infos;
    }

    public static class CategoryData {

        private String title;
        private String name1;
        private String name2;
        private String name3;
        private String url1;
        private String url2;
        private String url3;

        public String getTitle() {
            return title;
        }

        public String getName1() {
            return name1;
        }

        public String getName2() {
            return name2;
        }

        public String getName3() {
            return name3;
        }

        public String getUrl1() {
            return url1;
        }

        public String getUrl2() {
            return url2;
        }

        public String getUrl3() {
            return url3;
        }
    }
}
