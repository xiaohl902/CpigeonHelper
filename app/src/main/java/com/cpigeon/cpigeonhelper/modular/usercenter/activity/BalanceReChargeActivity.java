package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.order.bean.PayRequest;
import com.cpigeon.cpigeonhelper.utils.CashierInputFilter;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.cpigeon.cpigeonhelper.wxapi.WXPayEntryActivity;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    private int currPayway = -1;

    private IWXAPI mWxApi = null;

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
        setTitle("余额充值");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        chosePayWayMoney(1);
        etMoneyIncomeNumber.setFilters(new InputFilter[]{new CashierInputFilter()});
        etMoneyIncomeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chosePayWayMoney(currPayway);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mWxApi = WXAPIFactory.createWXAPI(mContext, WXPayEntryActivity.APP_ID, true);
        mWxApi.registerApp(WXPayEntryActivity.APP_ID);
    }

    /**
     * 添加手续费
     *
     * @param fee  金额
     * @param rate 费率
     * @return
     */
    public double getTotalFee(double fee, double rate) {
        double f = fee * rate;//手续费
        return fee + (f <= 0.01 ? 0.01f : f);
    }

    private void chosePayWayMoney(int type) {
        if (currPayway != type)
            currPayway = type;
        showTip();
    }

    private void showTip() {
        double fee;
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        try {
            fee = Double.valueOf(etMoneyIncomeNumber.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            fee = 0;
        }
        if (fee == 0) {
            tvPayWayTips.setVisibility(View.INVISIBLE);
        } else if (currPayway == 1) {
            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("需要收取1%%的手续费，实际支付%.2f元", getTotalFee(fee, 0.01)));
            builder.setSpan(redSpan, 15,
                    15 + String.format("%.2f", getTotalFee(fee, 0.01)).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvPayWayTips.setText(builder);
            tvPayWayTips.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.rl_wxpay, R.id.tv_order_protocol_income, R.id.btn_ok_income})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_order_protocol_income:

                break;
            case R.id.btn_ok_income:
                long timestamp = System.currentTimeMillis() / 1000;
                RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                        .addFormDataPart("m", etMoneyIncomeNumber.getText().toString().trim())
                        .build();
                Map<String, Object> postParams = new HashMap<>();
                postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                postParams.put("m", etMoneyIncomeNumber.getText().toString().trim());


//                if (mWxApi != null) {
//                    PayReq payReq=new PayReq();
//                    payReq.appId = "wxc9d120321bd1180a";
//                    payReq.partnerId = "1434404202";
//                    payReq.prepayId = "wx2017071215154874a1255eb50189903129";
//                    payReq.packageValue ="Sign=WXPay";
//                    payReq.nonceStr = "d5cfead94f5350c12c322b5b664544c1";
//                    payReq.timeStamp = "1499843734";
//                    payReq.sign = "E25CC211B1F2F87A2302E57FA4D614A9";
//                    boolean result = mWxApi.sendReq(payReq);
//                    if (!result) {
//                        CommonUitls.showToast(this, "发起支付失败");
//                    }
//                }


                RetrofitHelper.getApi()
                        .createRechargeOrder(AssociationData.getUserToken(), mRequestBody, timestamp,
                                CommonUitls.getApiSign(timestamp, postParams))
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderApiResponse -> {
                            if (orderApiResponse.getErrorCode() == 0) {
                                //创建充值订单完成，请求服务器进行微信预支付订单的获取
                                RequestBody mBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                                        .addFormDataPart("did", String.valueOf(orderApiResponse.getData().getId()))
                                        .addFormDataPart("t", "android")
                                        .addFormDataPart("app", "cpigeonhelper")
                                        .build();

                                Map<String, Object> pstParams = new HashMap<>();
                                pstParams.put("uid", String.valueOf(AssociationData.getUserId()));
                                pstParams.put("did", String.valueOf(orderApiResponse.getData().getId()));
                                pstParams.put("t", "android");
                                pstParams.put("app", "cpigeonhelper");

                                //请求服务器进行微信预支付订单的获取
                                RetrofitHelper.getApi()
                                        .getWXPrePayOrderForRecharge(AssociationData.getUserToken(), mBody,
                                                timestamp, CommonUitls.getApiSign(timestamp, pstParams))
                                        .compose(bindToLifecycle())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(payRequestApiResponse -> {
                                            if (payRequestApiResponse.getErrorCode() == 0) {
                                                if (mWxApi != null) {
                                                    boolean result = mWxApi.sendReq(payRequestApiResponse.getData().getWxPayReq());
                                                    if (!result) {
                                                        CommonUitls.showToast(this, "支付失败");
                                                    }
                                                }
                                            }
                                        }, throwable -> {

                                        });


                            }
                        }, throwable -> {

                        });
                break;
        }
    }

}
