package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.FlyingAreaHomeFragment;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/14.
 */

public class FlyingAreaActivity extends ToolbarBaseActivity {
    private Fragment[] fragments;


    private FlyingAreaHomeFragment homeFragment;

    @BindView(R.id.container)
    FrameLayout container;

    @Override
    protected void swipeBack() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_car_pager;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("常用司放地");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("添加", R.drawable.ic_add, () -> startActivity(new Intent(FlyingAreaActivity.this,AddFlyingAreaActivity.class)));
        initFragments();
    }

    private void initFragments() {
        homeFragment = FlyingAreaHomeFragment.newInstance();
        fragments = new Fragment[] {
                homeFragment,

        };

        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, homeFragment)
                .show(homeFragment).commit();
    }

    /**
     * 解决App重启后导致Fragment重叠的问题
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

}
