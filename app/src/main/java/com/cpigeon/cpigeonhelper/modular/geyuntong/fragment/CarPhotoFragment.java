package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.base.BaseFragment;

/**
 * Created by Administrator on 2017/6/1.
 */

public class CarPhotoFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void finishCreateView(Bundle state) {

    }
    public static CarPhotoFragment newInstance() {

        return new CarPhotoFragment();
    }
}
