package com.greatest.appmarket.bean;

import java.util.ArrayList;

/**
 * Created by WangHao on 2017.02.27  0027.
 */

public class SubjectInfo {
    public ArrayList<SubjectData> list;

    public class SubjectData {
        public String des;
        public String url;

        public String getDes() {
            return des;
        }

        public String getUrl() {
            return url;
        }
    }
}
