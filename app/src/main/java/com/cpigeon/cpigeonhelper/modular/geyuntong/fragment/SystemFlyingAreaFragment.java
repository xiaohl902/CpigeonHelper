package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/14.
 */

public class SystemFlyingAreaFragment extends BaseFragment {

    public static SystemFlyingAreaFragment newInstance() {

        return new SystemFlyingAreaFragment();
    }
    @Override
    public int getLayoutResId() {
        return R.layout.layout_swipwithrecycler;
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
