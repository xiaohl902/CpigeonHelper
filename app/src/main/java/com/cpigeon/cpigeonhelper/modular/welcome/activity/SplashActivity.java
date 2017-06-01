package com.cpigeon.cpigeonhelper.modular.welcome.activity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.BuildConfig;
import com.cpigeon.cpigeonhelper.MainActivity;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.usercenter.activity.LoginActivity;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

import static com.cpigeon.cpigeonhelper.common.db.AssociationData.DEV_ID;

/**
 * Created by Administrator on 2017/5/25.
 */

public class SplashActivity extends BaseActivity {
    private long timestamp;
    private boolean isOwnDev = true;

    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    isPermission();
                });

    }

    private void checkDevice() {
        timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", AssociationData.getUserId());
        postParams.put("appid", BuildConfig.APPLICATION_ID);

        RetrofitHelper.getApi()
                .getDeviceInfo(AssociationData.getUserToken(),
                        postParams, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceBeanApiResponse -> {
                    Logger.e("当前系统的Dev_Id" + DEV_ID);
                    if (deviceBeanApiResponse.isStatus()) {
                        Logger.e("您的设备没有在其他地方登录过");
                        isOwnDev = true;
                    } else {
                        Logger.e("您的设备有在其他地方登录过");
                        isOwnDev = false;
                    }
                    this.finishTask();
                }, throwable -> {
                    Logger.e("发生了异常" + throwable.getMessage());
                    this.finishTask();
                });

    }


    public void finishTask() {

        if (AssociationData.checkIsLogin() && isOwnDev) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        SplashActivity.this.finish();
    }


    public void isPermission() {
        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissonItem(Manifest.permission.READ_PHONE_STATE, "设备信息", R.drawable.permission_ic_phone));
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "地理位置", R.drawable.permission_ic_location));
        HiPermission.create(SplashActivity.this)
                .title("~报告圣上~")
                .permissions(permissonItems)
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))//permission icon color
                .msg("我们需要储存空间权限，设备信息权限和位置权限，才能一统天下，如果拒绝，则失去江山")

                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        AppManager.getAppManager().AppExit();
                    }

                    @Override
                    public void onFinish() {
                        checkDevice();
                    }

                    @Override
                    public void onDeny(String s, int i) {
                        Logger.e("onDeny" + s + "第" + i + "个");
                    }

                    @Override
                    public void onGuarantee(String s, int i) {
                        Logger.e("onGuarantee" + s + "第" + i + "个");
                    }
                });
    }

    /**
     * 加载闪图时候避免用户点击返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
