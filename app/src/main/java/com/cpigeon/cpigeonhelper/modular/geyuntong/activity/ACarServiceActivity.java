package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarServiceFragment;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/31.
 */

public class ACarServiceActivity extends ToolbarBaseActivity {

    @BindView(R.id.container)
    FrameLayout container;
    private Fragment[] fragments;

    private int currentTabIndex;

    private int index;

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
        initFragments();
    }

    private void initFragments() {
        CarServiceFragment mCarServiceFragment = CarServiceFragment.newInstance();
        fragments = new Fragment[]{
                mCarServiceFragment
        };
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mCarServiceFragment)
                .show(mCarServiceFragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    /**
     * Fragment切换
     */
    private void switchFragment() {

        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.container, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentTabIndex = index;
    }

    /**
     * 切换Fragment的下标
     */
    private void changeFragmentIndex(MenuItem item, int currentIndex) {

        index = currentIndex;
        switchFragment();
        item.setChecked(true);
    }
}
