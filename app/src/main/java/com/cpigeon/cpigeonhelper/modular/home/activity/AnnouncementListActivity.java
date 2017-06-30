package com.cpigeon.cpigeonhelper.modular.home.activity;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/25.
 */

public class AnnouncementListActivity extends BaseActivity {
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, mColor);
    }

    @Override
    protected void initToolBar() {
        initRefreshLayout();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initRefreshLayout() {
        loadData();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getApi()
                .getAnnouncementList("协会用户")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.isStatus()) {

                    } else {

                    }
                },throwable -> {
                    if(throwable instanceof SocketTimeoutException){
                        CommonUitls.showToast(this,"连接超时了，都啥年代了还塞网络？");
                    }else if(throwable instanceof ConnectException){
                        CommonUitls.showToast(this,"连接失败了，都啥年代了无网络？");
                    }else if(throwable instanceof RuntimeException){
                        CommonUitls.showToast(this,"发生了不可预期的错误："+throwable.getMessage());
                    }
                });
    }
}
