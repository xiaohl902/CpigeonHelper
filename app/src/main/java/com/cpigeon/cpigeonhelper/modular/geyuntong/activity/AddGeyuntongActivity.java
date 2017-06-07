package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/7.
 */

public class AddGeyuntongActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_geyuntong_name)
    TextView tvGeyuntongName;
    @BindView(R.id.tv_geyuntong_place)
    TextView tvGeyuntongPlace;
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;
    @BindView(R.id.ll_geyuntong_name)
    LinearLayout llGeyuntongName;
    @BindView(R.id.ll_geyuntong_place)
    LinearLayout llGeyuntongPlace;
    @BindView(R.id.ll_geyuntong_latlng)
    LinearLayout llGeyuntongLatlng;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this,mColor,0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("添加比赛");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("完成", this::add);
    }

    @OnClick({R.id.ll_geyuntong_name, R.id.ll_geyuntong_place, R.id.ll_geyuntong_latlng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_geyuntong_name:
                break;
            case R.id.ll_geyuntong_place:
                break;
            case R.id.ll_geyuntong_latlng:
                break;
        }
    }
    private void add(){
        Logger.e("保存了");
    }
}
