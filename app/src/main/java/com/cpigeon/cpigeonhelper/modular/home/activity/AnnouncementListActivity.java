package com.cpigeon.cpigeonhelper.modular.home.activity;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

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
                        Logger.e("6666");
                    } else {
                        Logger.e("12312312312313");
                    }
                },throwable -> {
                    Logger.e("错误消息:"+throwable.getMessage());
                });
    }
}
