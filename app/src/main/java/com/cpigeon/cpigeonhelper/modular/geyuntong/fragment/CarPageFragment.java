package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.HomePagerAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.ui.NoScrollViewPager;
import com.flyco.tablayout.SlidingTabLayout;

import butterknife.BindView;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 首页模块主界面
 */
public class CarPageFragment extends BaseFragment {


    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.view_pager)
    NoScrollViewPager mViewPager;
    private GeYunTong geYunTong;
    public static CarPageFragment newInstance() {

        return new CarPageFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
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

        HomePagerAdapter mHomeAdapter = new HomePagerAdapter(getChildFragmentManager(),
                getApplicationContext(),geYunTong.getStateCode());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomeAdapter);
        mSlidingTab.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

}
