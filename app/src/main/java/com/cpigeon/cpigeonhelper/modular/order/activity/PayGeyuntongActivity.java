package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.order.bean.PayRequest;
import com.cpigeon.cpigeonhelper.ui.PayFragment;
import com.cpigeon.cpigeonhelper.ui.PayPwdView;
import com.cpigeon.cpigeonhelper.modular.usercenter.activity.PayPwdActivity;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.cpigeon.cpigeonhelper.wxapi.WXPayEntryActivity;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.OnWxPayListener.ERR_OK;

/**
 * Created by Administrator on 2017/5/27.
 */

public class PayGeyuntongActivity extends ToolbarBaseActivity implements PayPwdView.InputCallBack {
    @BindView(R.id.tv_order_number_title)
    TextView tvOrderNumberTitle;
    @BindView(R.id.tv_order_number_content)
    TextView tvOrderNumberContent;
    @BindView(R.id.tv_order_name_title)
    TextView tvOrderNameTitle;
    @BindView(R.id.tv_order_name_content)
    TextView tvOrderNameContent;
    @BindView(R.id.tv_order_time_title)
    TextView tvOrderTimeTitle;
    @BindView(R.id.tv_order_time_content)
    TextView tvOrderTimeContent;
    @BindView(R.id.tv_order_price_title)
    TextView tvOrderPriceTitle;
    @BindView(R.id.tv_order_price_content)
    TextView tvOrderPriceContent;
    @BindView(R.id.tv_order_explain)
    TextView tvOrderExplain;
    @BindView(R.id.cb_order_protocol)
    CheckBox cbOrderProtocol;
    @BindView(R.id.tv_order_protocol)
    TextView tvOrderProtocol;
    @BindView(R.id.layout_order_pay_way)
    LinearLayout layoutOrderPayWay;
    private int sid;
    private long timestamp;
    private PayReq payReq;
    private IWXAPI mWxApi;
    private int orderid;
    private PayFragment fragment;
    private String price;
    private String type;
    private CommonUitls.OnWxPayListener onWxPayListenerWeakReference = wxPayReturnCode -> {
        if (wxPayReturnCode == ERR_OK)
        {
            CommonUitls.showToast(PayGeyuntongActivity.this, "支付成功了");
        }else
            CommonUitls.showToast(PayGeyuntongActivity.this, "支付失败");
    };

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_paygeyuntongservice;
    }

    @Override
    protected void setStatusBar() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("订单支付");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        Intent intent = getIntent();
        sid = intent.getIntExtra("sid", 0);
        type = intent.getStringExtra("type");
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(mContext, null);
            mWxApi.registerApp(WXPayEntryActivity.APP_ID);
        }
        loadData();
        loadPayWay();
    }

    @Override
    public void loadData() {
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("sid", String.valueOf(sid))
                .addFormDataPart("type", type)
                .build();
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("sid", String.valueOf(sid));
        postParams.put("type", type);
        RetrofitHelper
                .getApi().createServiceOrder(AssociationData.getUserToken(), requestBody, timestamp,
                CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderApiResponse -> {
                    if (orderApiResponse.getErrorCode() == 0) {
                        orderid = orderApiResponse.getData().getId();
                        tvOrderNumberContent.setText(orderApiResponse.getData().getNumber());
                        tvOrderNameContent.setText(orderApiResponse.getData().getItem());
                        tvOrderTimeContent.setText(orderApiResponse.getData().getTime());
                        tvOrderPriceContent.setText(String.format("%.2f元", orderApiResponse.getData().getPrice()));
                        price = String.format("%.2f元", orderApiResponse.getData().getPrice());
                        CommonUitls.getInstance().addOnWxPayListener(onWxPayListenerWeakReference);
                    } else {
                        CommonUitls.showToast(this, orderApiResponse.getMsg());
                    }
                }, throwable -> {
                    CommonUitls.showToast(this, throwable.getMessage());
                });
    }

    private void loadPayWay() {
        layoutOrderPayWay.removeAllViews();
        View splitLine;
        //分割线布局
        ViewGroup.LayoutParams splitLineLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.split_line_width));
        //创建支付方式
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_item_pay_way, null);
        ((TextView) v.findViewById(R.id.tv_pay_way_name)).setText("余额支付");
        ((ImageView) v.findViewById(R.id.iv_pay_icon)).setImageResource(R.drawable.svg_ic_pay_balance);
        layoutOrderPayWay.addView(v);
        v.setOnClickListener(v1 -> {
            if (!cbOrderProtocol.isChecked()) {
                CommonUitls.showToast(PayGeyuntongActivity.this, "请阅读之后再来");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(PayFragment.EXTRA_CONTENT, "支付金额：¥ " + price);
                fragment = new PayFragment();
                fragment.setArguments(bundle);
                fragment.setPaySuccessCallBack(this);
                fragment.show(getSupportFragmentManager(), "Pay");
            }
        });


        //分割线
        splitLine = new View(mContext);
        splitLine.setLayoutParams(splitLineLayoutParams);
        splitLine.setBackgroundColor(getResources().getColor(R.color.colorLayoutSplitLineGray));
        layoutOrderPayWay.addView(splitLine);

        v = LayoutInflater.from(mContext).inflate(R.layout.layout_item_pay_way, null);
        ((TextView) v.findViewById(R.id.tv_pay_way_name)).setText("微信支付");
        ((ImageView) v.findViewById(R.id.iv_pay_icon)).setImageResource(R.drawable.svg_ic_pay_weixin);
        layoutOrderPayWay.addView(v);
        v.setOnClickListener(v12 -> {
            if (!cbOrderProtocol.isChecked()) {
                CommonUitls.showToast(PayGeyuntongActivity.this, getString(R.string.sentence_not_watch_pay_agreement_prompt));
            } else if (mWxApi.isWXAppInstalled()) {
                startWxPay();
            } else {
                CommonUitls.showToast(PayGeyuntongActivity.this, "亲，您未安装微信哦");
            }
        });
    }

    private void startWxPay() {
        if (payReq != null) {
            entryWXPay(payReq);
            return;
        }
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("oid", String.valueOf(orderid))
                .addFormDataPart("app", "cpigeonhelper")
                .build();
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("oid", String.valueOf(orderid));
        postParams.put("app", "cpigeonhelper");


        RetrofitHelper.getApi()
                .createWxOrder(AssociationData.getUserToken(), requestBody, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(payReqApiResponse -> {
                    if (payReqApiResponse.getErrorCode() == 0) {
                        entryWXPay(payReqApiResponse.getData().getWxPayReq());
                    } else {
                        CommonUitls.showToast(this, payReqApiResponse.getMsg());
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时了");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "连接失败");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误");
                    }
                });
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



    @Override
    public void onInputFinish(String result) {
        Logger.e(result);
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("oid", String.valueOf(orderid))
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("p", EncryptionTool.encryptAES(result))
                .build();

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("oid", String.valueOf(orderid));
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("p", EncryptionTool.encryptAES(result));

        RetrofitHelper.getApi()
                .orderPayByBalance(AssociationData.getUserToken(), mRequestBody, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectApiResponse -> {
                    fragment.dismiss();
                    if (objectApiResponse.getErrorCode() == 0) {
                        CommonUitls.showToast(this, "支付成功");
                        if (RealmUtils.getInstance().existGYTInfo()) {
                            RealmUtils.getInstance().deleteGYTInfo();
                        }

                        finish();
                    } else if (objectApiResponse.getErrorCode() == 20010) {
                        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("温馨提示")
                                .setContentText("您暂未设置支付密码，是否跳转设置？")
                                .setConfirmText("好的")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    startActivity(new Intent(PayGeyuntongActivity.this, PayPwdActivity.class));
                                })
                                .setCancelText("取消")
                                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                                .show();
                    } else {
                        CommonUitls.showToast(this, objectApiResponse.getMsg());
                    }
                }, throwable -> {
                    fragment.dismiss();
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "连接异常，请检查连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误：" + throwable.getMessage());
                    }

                });
    }
}
