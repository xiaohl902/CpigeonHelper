package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.utils.DateUtils;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/7/6.
 */

public class VipLevelUpActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_userBelongXiehui)
    TextView tvUserBelongXiehui;
    @BindView(R.id.tv_user_state)
    TextView tvUserState;
    @BindView(R.id.tv_user_time)
    TextView tvUserTime;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_viplevelup;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("会员升级");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        initView();
        loadGYTServer();

    }

    private void loadGYTServer() {

    }

    private void initView() {
        RealmResults<GYTService> gytServices = RealmUtils.getInstance().queryGTYInfo();
        if (TextUtils.isEmpty(gytServices.get(0).getGrade()))
        {
            tvUserState.setText("普通用户");
        }else {
            tvUserState.setText(gytServices.get(0).getGrade().toUpperCase());
        }


        tvUserTime.setText(DateUtils.compareDate(gytServices.get(0).getExpireTime(),DateUtils.millis2String(System.currentTimeMillis())));
    }



}
