package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarPageFragment;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/31.
 */

public class ACarServiceActivity extends ToolbarBaseActivity {
    private Fragment[] fragments;


    private CarPageFragment carPageFragment;

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
        setTitle("成都三道堰虐菜局比赛");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        //初始化Fragment
        initFragments();
    }

    private void initFragments() {
        carPageFragment = CarPageFragment.newInstance();
        fragments = new Fragment[] {
                carPageFragment,

        };

        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, carPageFragment)
                .show(carPageFragment).commit();
    }

    /**
     * 解决App重启后导致Fragment重叠的问题
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }


}
