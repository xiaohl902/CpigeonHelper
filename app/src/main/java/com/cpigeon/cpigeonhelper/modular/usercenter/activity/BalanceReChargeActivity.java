package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/7.
 */

public class BalanceReChargeActivity extends ToolbarBaseActivity {
    @BindView(R.id.et_money_income_number)
    EditText etMoneyIncomeNumber;
    @BindView(R.id.tv_pay_way_tips)
    TextView tvPayWayTips;
    @BindView(R.id.iv_wxpay_ok)
    AppCompatImageView ivWxpayOk;
    @BindView(R.id.rl_wxpay)
    RelativeLayout rlWxpay;
    @BindView(R.id.cb_order_protocol_income)
    CheckBox cbOrderProtocolIncome;
    @BindView(R.id.tv_order_protocol_income)
    TextView tvOrderProtocolIncome;
    @BindView(R.id.btn_ok_income)
    Button btnOkIncome;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_balance_recharge;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

}
