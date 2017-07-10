package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/30.
 */

public class MyBalanceActivity extends ToolbarBaseActivity {
    @BindView(R.id.ll_open_vip)
    LinearLayout llOpenVip;
    @BindView(R.id.ll_open_gxt)
    LinearLayout llOpenGxt;
    @BindView(R.id.ll_xufei)
    LinearLayout llXufei;
    @BindView(R.id.ll_open_gyt)
    LinearLayout llOpenGyt;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.ll_recharge)
    LinearLayout llRecharge;

    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_balance;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("钱包");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("明细", this::moreDetails);
    }

    public void moreDetails() {

    }
    @OnClick({R.id.ll_open_vip, R.id.ll_open_gxt, R.id.ll_xufei, R.id.ll_open_gyt, R.id.ll_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_open_vip:
                break;
            case R.id.ll_open_gxt:
                break;
            case R.id.ll_xufei:
                break;
            case R.id.ll_open_gyt:
                break;
            case R.id.ll_recharge:
                startActivity(new Intent(

                ));
                break;
        }
    }
}
