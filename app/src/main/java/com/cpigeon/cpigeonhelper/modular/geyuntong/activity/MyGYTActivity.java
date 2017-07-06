package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.modular.flyarea.activity.FlyingAreaActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.order.activity.VipLevelUpActivity;
import com.cpigeon.cpigeonhelper.modular.root.activity.RootListActivity;
import com.cpigeon.cpigeonhelper.modular.root.activity.RootManagerActivity;
import com.cpigeon.cpigeonhelper.utils.DateUtils;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class MyGYTActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_totaldistance)
    TextView tvTotaldistance;
    @BindView(R.id.ll_enter_geyuntong)
    LinearLayout llEnterGeyuntong;
    @BindView(R.id.ll_enter_flyarea)
    LinearLayout llEnterFlyarea;
    @BindView(R.id.ll_enter_root)
    LinearLayout llEnterRoot;
    @BindView(R.id.ll_enter_vip)
    LinearLayout llEnterVip;
    @BindView(R.id.ll_enter_help)
    LinearLayout llEnterHelp;
    @BindView(R.id.tv_top_speed)
    TextView tvTopSpeed;
    @BindView(R.id.tv_avarge_speed)
    TextView tvAvargeSpeed;
    @BindView(R.id.tv_totalrace)
    TextView tvTotalrace;
    @BindView(R.id.tv_using_time)
    TextView tvUsingTime;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_user_state)
    TextView tvState;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_mygyt;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的鸽运通");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("续费", this::xuFei);
        GYTService gytService = RealmUtils.getInstance().queryGTYInfo().get(0);
        if (gytService!=null)
        {
            tvState.setText(TextUtils.isEmpty(gytService.getGrade())?"当前用户等级：普通用户":"当前用户等级："+gytService.getGrade());
            tvEndTime.setText(gytService.getExpireTime());
            tvStartTime.setText(gytService.getOpenTime());
            tvUsingTime.setText(DateUtils.compareDate(gytService.getExpireTime(),gytService.getOpenTime()));
        }else {
            tvState.setText("当前用户等级：普通用户");
            tvEndTime.setText("无");
            tvStartTime.setText("无");
            tvUsingTime.setText("无");
        }

    }

    private void xuFei(){

    }

    @OnClick({R.id.ll_enter_geyuntong, R.id.ll_enter_flyarea, R.id.ll_enter_root, R.id.ll_enter_vip, R.id.ll_enter_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_enter_geyuntong:
                startActivity(new Intent(this,GeYunTongListActivity.class));
                break;
            case R.id.ll_enter_flyarea:
                startActivity(new Intent(this,FlyingAreaActivity.class));
                break;
            case R.id.ll_enter_root:
                startActivity(new Intent(this,RootListActivity.class));
                break;
            case R.id.ll_enter_vip:
                startActivity(new Intent(this,VipLevelUpActivity.class));
                break;
            case R.id.ll_enter_help:
                break;
        }
    }
}
