package com.cpigeon.cpigeonhelper.modular.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.usercenter.activity.LoginActivity;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/4.
 */

public class SettingActivity extends ToolbarBaseActivity {
    @BindView(R.id.rl_clear_cache)
    RelativeLayout rlClearCache;
    @BindView(R.id.ll_check_update)
    RelativeLayout llCheckUpdate;
    @BindView(R.id.ll_about_us)
    RelativeLayout llAboutUs;
    @BindView(R.id.ll_app_pingjia)
    RelativeLayout llAppPingjia;
    @BindView(R.id.btn_outlogin)
    Button btnOutlogin;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);//最后一个参数代表了透明度，0位全部透明
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("设置");
        setTopLeftButton(R.drawable.ic_back,this::finish);
    }

    @OnClick({R.id.rl_clear_cache, R.id.ll_check_update, R.id.ll_about_us, R.id.ll_app_pingjia, R.id.btn_outlogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_clear_cache:
                break;
            case R.id.ll_check_update:
                RetrofitHelper.getApi()
                        .checkForUpdate()
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updateBean -> {
                            if (CommonUitls.getVersionCode(SettingActivity.this) == updateBean.get(0).getVerCode())
                            {
                                CommonUitls.showToast(SettingActivity.this,"已经是最新版本啦~");
                            }else {
                                
                            }
                        }, throwable -> {

                        });
                break;
            case R.id.ll_about_us:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.ll_app_pingjia:
                break;
            case R.id.btn_outlogin:
                if (RealmUtils.getInstance().existGYTInfo())
                {
                    RealmUtils.getInstance().deleteGYTInfo();
                }
                if (RealmUtils.getInstance().existUserInfo())
                {
                    RealmUtils.getInstance().deleteUserInfo();
                }
                if (RealmUtils.getInstance().existLocation())
                {
                    RealmUtils.getInstance().deleteLocation();
                }
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
