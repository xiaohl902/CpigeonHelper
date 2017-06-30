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
import com.cpigeon.cpigeonhelper.wxapi.WXPayEntryActivity;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.OnWxPayListener.ERR_OK;

/**
 * Created by Administrator on 2017/5/27.
 */

public class PayGeyuntongActivity extends ToolbarBaseActivity {
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
    PayReq payReq;
    private IWXAPI mWxApi = null;
    private int orderid;
    private CommonUitls.OnWxPayListener onWxPayListenerWeakReference = wxPayReturnCode -> {
        if (wxPayReturnCode == ERR_OK)
            new Handler().postDelayed(this::finish, 500);
        else
            CommonUitls.showToast(PayGeyuntongActivity.this,"支付失败");
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
        setTopLeftButton(R.drawable.ic_back,this::finish);
        Intent intent = getIntent();
        sid = intent.getIntExtra("sid",0);
        loadData();
        loadPayWay();
    }

    @Override
    public void loadData() {
        timestamp = System.currentTimeMillis() /1000;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("sid", String.valueOf(sid))
                .build();
        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid",String.valueOf(AssociationData.getUserId()));
        postParams.put("sid",String.valueOf(sid));
        RetrofitHelper
                .getApi().createServiceOrder(AssociationData.getUserToken(),requestBody,timestamp,
                CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderApiResponse -> {
                    if (orderApiResponse.getErrorCode() == 0)
                    {
                        orderid = orderApiResponse.getData().getId();
                        tvOrderNumberContent.setText(orderApiResponse.getData().getNumber());
                        tvOrderNameContent.setText(orderApiResponse.getData().getItem());
                        tvOrderTimeContent.setText(orderApiResponse.getData().getTime());
                        tvOrderPriceContent.setText(String.format("%.2f元", orderApiResponse.getData().getPrice()) );
                        if (mWxApi == null) {
                            mWxApi = WXAPIFactory.createWXAPI(mContext, WXPayEntryActivity.APP_ID, true);
                            mWxApi.registerApp(WXPayEntryActivity.APP_ID);
                        }
                        CommonUitls.getInstance().addOnWxPayListener(onWxPayListenerWeakReference);
                    }
                    else {
                        CommonUitls.showToast(this,orderApiResponse.getMsg());
                    }
                }, throwable -> {
                    CommonUitls.showToast(this,throwable.getMessage());
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
                CommonUitls.showToast(PayGeyuntongActivity.this,"请阅读之后再来");
                return;
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
                CommonUitls.showToast(PayGeyuntongActivity.this,getString(R.string.sentence_not_watch_pay_agreement_prompt));
            }else if (mWxApi.isWXAppInstalled()){
                startWxPay();
            } else {
                CommonUitls.showToast(PayGeyuntongActivity.this,"亲，您未安装微信哦");
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
        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid",String.valueOf(AssociationData.getUserId()));
        postParams.put("oid",String.valueOf(orderid));
        postParams.put("app","cpigeonhelper");


        RetrofitHelper.getApi()
                .createWxOrder(AssociationData.getUserToken(),requestBody,timestamp,CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(payReqApiResponse -> {
                    if (payReqApiResponse.getErrorCode() == 0) {
                        PayReq payReq = new PayReq();
                        payReq.appId = payReqApiResponse.getData().getAppid();
                        payReq.partnerId = payReqApiResponse.getData().getPartnerid();
                        payReq.prepayId = payReqApiResponse.getData().getPrepayid();
                        Logger.e(payReqApiResponse.getData().getPackageX());
                        payReq.packageValue = payReqApiResponse.getData().getPackageX();
                        payReq.nonceStr = payReqApiResponse.getData().getNoncestr();
                        payReq.timeStamp = payReqApiResponse.getData().getTimestamp();
                        payReq.sign = payReqApiResponse.getData().getSign();
                        entryWXPay(payReq);
                    }else {
                        CommonUitls.showToast(this,payReqApiResponse.getMsg());
                    }
                },throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时了");
                    }else if (throwable instanceof ConnectException){
                        CommonUitls.showToast(this,"连接失败");
                    }else if (throwable instanceof RuntimeException)
                    {
                        CommonUitls.showToast(this,"发生了不可预期的错误");
                    }
                });
    }

    private void entryWXPay(PayReq payReq) {
        this.payReq = payReq;
        if (mWxApi != null) {
            boolean result = mWxApi.sendReq(payReq);
            if (!result)
                CommonUitls.showToast(this,"支付失败");
            Logger.d("发起微信支付");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUitls.getInstance().removeOnWxPayListener(onWxPayListenerWeakReference);
    }
}
