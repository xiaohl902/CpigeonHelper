package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarPageFragment;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/31.
 */

public class ACarServiceActivity extends ToolbarBaseActivity {
    private Fragment[] fragments;
    private GeYunTong geYunTong;

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
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        geYunTong = bundle.getParcelable("geyuntong");
        setGeYunTong(geYunTong);
        setTitle(geYunTong.getRaceName());
        setTopLeftButton(R.drawable.ic_back, this::finish);

        initFragments();
    }

    public GeYunTong getGeYunTong() {
        return geYunTong;
    }

    public void setGeYunTong(GeYunTong geYunTong) {
        this.geYunTong = geYunTong;
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
