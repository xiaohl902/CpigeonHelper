package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.usercenter.activity.BalanceReChargeActivity;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的钱包界面
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
    @BindView(R.id.tv_geyuntong_status)
    TextView mTextView;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
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
        if (RealmUtils.getInstance().existGYTInfo()&&!RealmUtils.getInstance().queryGTYInfo().get(0).isIsExpired())
        {
            mTextView.setText("续费鸽运通");
        }else {
            mTextView.setText("开通鸽运通");
        }

        RetrofitHelper.getApi()
                .getUserBalance(AssociationData.getUserToken(),AssociationData.getUserId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiResponse -> {
                    if (stringApiResponse.getErrorCode() == 0)
                    {
                        tvMoney.setText(stringApiResponse.getData());
                    }else {
                        tvMoney.setText("");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时");
                    }else if (throwable instanceof ConnectException)
                    {
                        CommonUitls.showToast(this,"连接异常");
                    }else if (throwable instanceof RuntimeException)
                    {
                        CommonUitls.showToast(this,"发生不可预期的错误:"+throwable.getMessage());
                    }
                });
    }

    public void moreDetails() {

    }


    @OnClick({R.id.ll_open_vip, R.id.ll_open_gxt, R.id.ll_xufei, R.id.ll_open_gyt, R.id.ll_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_open_vip:
                startActivity(new Intent(this,ReChargeActivity.class));
                break;
            case R.id.ll_open_gxt:
                break;
            case R.id.ll_xufei:
                break;
            case R.id.ll_open_gyt:
                break;
            case R.id.ll_recharge:
                startActivity(new Intent(this, BalanceReChargeActivity.class));
                break;
        }
    }
}
