package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.FlyingAreaPagerAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.HomePagerAdapter;
import com.cpigeon.cpigeonhelper.ui.NoScrollViewPager;
import com.flyco.tablayout.SlidingTabLayout;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/14.
 */

public class FlyingAreaHomeFragment extends BaseFragment {

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.view_pager)
    NoScrollViewPager mViewPager;

    public static FlyingAreaHomeFragment newInstance() {

        return new FlyingAreaHomeFragment();
    }


    @Override
    public int getLayoutResId() {

        return R.layout.fragment_car_pager;
    }


    @Override
    public void finishCreateView(Bundle state) {
        initViewPager();
    }


    private void initViewPager() {

        FlyingAreaPagerAdapter mHomeAdapter = new FlyingAreaPagerAdapter(getChildFragmentManager(),
                getApplicationContext());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomeAdapter);
        mSlidingTab.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }
}
