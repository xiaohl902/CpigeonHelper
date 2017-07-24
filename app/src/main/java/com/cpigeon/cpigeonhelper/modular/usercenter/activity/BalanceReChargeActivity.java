package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.cpigeon.cpigeonhelper.utils.CashierInputFilter;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.cpigeon.cpigeonhelper.wxapi.WXPayEntryActivity;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.OnWxPayListener.ERR_OK;

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
    private PayReq payReq;

    private CommonUitls.OnWxPayListener onWxPayListenerWeakReference = wxPayReturnCode -> {
        if (wxPayReturnCode == ERR_OK) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("支付成功")
                    .setConfirmText("好的")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    })
                    .show();
        } else
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("支付失败")
                    .setConfirmText("好的")
            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();
    };

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
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(mContext, null);
            mWxApi.registerApp(WXPayEntryActivity.APP_ID);
        }
        CommonUitls.getInstance().addOnWxPayListener(onWxPayListenerWeakReference);
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
                if (TextUtils.isEmpty(etMoneyIncomeNumber.getText().toString().trim()))
                {
                    CommonUitls.showToast(this,"充值金额必须大于0.01");
                    return;
                }
                if (!cbOrderProtocolIncome.isChecked())
                {
                    CommonUitls.showToast(this,"请阅读中鸽网支付协议");
                }else {
                    long timestamp = System.currentTimeMillis() / 1000;
                    RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                            .addFormDataPart("m", etMoneyIncomeNumber.getText().toString().trim())
                            .build();
                    Map<String, Object> postParams = new HashMap<>();
                    postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                    postParams.put("m", etMoneyIncomeNumber.getText().toString().trim());
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
                                                    if (mWxApi.isWXAppInstalled()) {
                                                        if (payReq != null) {
                                                            entryWXPay(payReq);
                                                            return;
                                                        }
                                                        mWxApi.sendReq(payRequestApiResponse.getData().getWxPayReq());
                                                    }
                                                }
                                            }, throwable -> {

                                            });


                                }
                            }, throwable -> {

                            });
                }
                break;
        }
    }

    private void entryWXPay(PayReq payReq) {
        this.payReq = payReq;
        if (mWxApi != null) {
            boolean result = mWxApi.sendReq(payReq);
            if (!result) {
                CommonUitls.showToast(this, "支付失败");
            } else {
                Logger.d("发起微信支付");
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWxApi = null;
        payReq = null;
        CommonUitls.getInstance().removeOnWxPayListener(onWxPayListenerWeakReference);
    }
}
