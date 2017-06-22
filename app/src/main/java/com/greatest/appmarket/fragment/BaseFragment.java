package com.greatest.appmarket.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.greatest.appmarket.R;
import com.greatest.appmarket.utils.UIUtils;


/**
 * Created by WangHao on 2017.03.05  0005.
 */

public class BaseFragment extends Fragment {
    private View page_loading;
    private View page_empty;
    public View page_error;
    public final int STATE_LOADING = 0;
    public final int STATE_EMPTY = 1;
    public final int STATE_ERROR = 2;
    public final int STATE_SUCCESS = 3;
    public static int currentState = 0;
    private ImageView imageView;
    private RotateAnimation rotateAnimation;

    public View lodingPage() {
        FrameLayout frameLayout = new FrameLayout(UIUtils.getContext());
        page_loading = UIUtils.inflate(R.layout.page_loading);

        imageView = (ImageView) page_loading.findViewById(R.id.iv_loading);
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(-1);

        page_empty = UIUtils.inflate(R.layout.page_empty);
        page_error = UIUtils.inflate(R.layout.page_error);

        frameLayout.addView(page_loading);
        frameLayout.addView(page_empty);
        frameLayout.addView(page_error);

        showRightPage();
        return frameLayout;
    }

    public void showRightPage() {
        if (!(currentState == STATE_LOADING)) {
            imageView.clearAnimation();
        } else {
            imageView.startAnimation(rotateAnimation);
        }
        page_loading.setVisibility(currentState == STATE_LOADING ? View.VISIBLE : View.GONE);
        page_empty.setVisibility(currentState == STATE_EMPTY ? View.VISIBLE : View.GONE);
        page_error.setVisibility(currentState == STATE_ERROR ? View.VISIBLE : View.GONE);
    }
}
