package com.cpigeon.cpigeonhelper.base;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.cpigeon.cpigeonhelper.BuildConfig;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 * Created by Administrator on 2017/5/25.
 */

public class MyApp extends Application {
    public static MyApp mInstance;
    public static MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //初始化Bugly
        initBugly();
        //初始化Steho调试工具
        initSteho();
        //初始化Realm
        initRealm();
    }

    private void initRealm() {
        Realm.init(mInstance);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(RealmUtils.DB_NAME)
                .schemaVersion(1)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void initSteho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    private void initBugly() {
//        CrashReport.initCrashReport(getApplicationContext(), "d27405999a", BuildConfig.DEBUG);
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
