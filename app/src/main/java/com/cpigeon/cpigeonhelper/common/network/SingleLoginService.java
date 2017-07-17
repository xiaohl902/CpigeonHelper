package com.cpigeon.cpigeonhelper.common.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.DeviceBean;
import com.orhanobut.logger.Logger;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/17.
 */

public class SingleLoginService extends IntentService {
    private String token;
    private static final String ACTION_CHECK_SINGLE_LOGIN = "com.cpigeon.cpigeonhelper.common.network.SingleLoginService";


    public SingleLoginService() {
        super("SingleLoginService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        token = AssociationData.getUserToken();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_SINGLE_LOGIN.equals(action)) {
                checkLogin();
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SingleLoginService.class);
        intent.setAction(ACTION_CHECK_SINGLE_LOGIN);
        context.startService(intent);
    }

    private void checkLogin() {
        RetrofitHelper.getApi()
                .singleLoginCheck(token)
                .subscribe(deviceBeanApiResponse -> {
                    if (deviceBeanApiResponse.getErrorCode()!=20000)
                    {
                        Thread.sleep(3000);
                        checkLogin();
                    }else {
                        Logger.e("您已下线");
                    }
                });
    }
}
