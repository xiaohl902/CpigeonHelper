package com.cpigeon.cpigeonhelper.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.cpigeon.cpigeonhelper.BuildConfig;
import com.cpigeon.cpigeonhelper.MainActivity;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.InitializeService;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.autolayout.config.AutoLayoutConifg;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 *
 * Created by Administrator on 2017/5/25.
 *
 */

public class MyApp extends Application {

    public static MyApp mInstance;

    public static MyApp getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initRealm();
        InitializeService.start(this);
    }

    private void initRealm() {
        Realm.init(this.getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(RealmUtils.DB_NAME)
                .schemaVersion(1)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)
        {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                createConfigurationContext(newConfig);
            } else {
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
        }
        return res;
    }
}
