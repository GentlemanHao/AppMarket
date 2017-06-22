package com.greatest.appmarket.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greatest.appmarket.R;
import com.greatest.appmarket.utils.UIUtils;


/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class GameFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return UIUtils.inflate(R.layout.page_empty);
    }
}
