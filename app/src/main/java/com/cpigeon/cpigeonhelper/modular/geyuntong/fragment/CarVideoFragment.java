package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;

/**
 * Created by Administrator on 2017/6/1.
 */

public class CarVideoFragment extends BaseFragment {

    public static CarVideoFragment newInstance() {

        return new CarVideoFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_video;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {

        if (!isPrepared || !isVisible) {
            return;
        }

        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

}
