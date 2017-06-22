package com.greatest.appmarket.fragment;

import java.util.HashMap;

/**
 * Created by WangHao on 2017.2.19  0019.
 */

public class FragmentFactory {

    public static HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();

    public static BaseFragment creatFragment(int position) {

        BaseFragment fragment = fragmentMap.get(position);

        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new AppFragment();
                    break;
                case 2:
                    fragment = new GameFragment();
                    break;
                case 3:
                    fragment = new SubjectFragment();
                    break;
                case 4:
                    fragment = new RecommendFragment();
                    break;
                case 5:
                    fragment = new CategoryFragment();
                    break;
                case 6:
                    fragment = new HotFragment();
                    break;
            }
            fragmentMap.put(position, fragment);
        }
        return fragment;
    }
}
