package com.cpigeon.cpigeonhelper.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by Administrator on 2017/7/11.
 */

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static final String APP_ID = "wxfa9493e635225d92";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Logger.e("baseReq = [" + baseReq.toString() + "]");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Logger.e("BaseResp{" +
                "errCode=" + baseResp.errCode +
                ", errStr='" + baseResp.errStr + '\'' +
                ", transaction='" + baseResp.transaction + '\'' +
                ", openId='" + baseResp.openId + '\'' +
                '}');
        CommonUitls.getInstance().onWxPay(this, baseResp.errCode);
        this.finish();
    }
}
