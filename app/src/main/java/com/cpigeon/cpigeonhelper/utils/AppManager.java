package com.cpigeon.cpigeonhelper.utils;

import android.app.Activity;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Created by Administrator on 2017/5/25.
 */

public class AppManager {

    private Stack<WeakReference<AppCompatActivity>> mWeakReferences;

    private volatile static AppManager mInstance;

    private AppManager() {

    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (mInstance == null) {
            synchronized (AppManager.class) {
                if (mInstance == null) {
                    mInstance = new AppManager();
                }
            }

        }
        return mInstance;
    }

    /***
     * 栈中Activity的数
     *
     * @return Activity的数
     */
    public int stackSize() {
        return mWeakReferences.size();
    }

    /***
     * 获得Activity栈
     *
     * @return Activity栈
     */
    public Stack<WeakReference<AppCompatActivity>> getStack() {
        return mWeakReferences;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(WeakReference<AppCompatActivity> activity) {
        if (mWeakReferences == null) {
            mWeakReferences = new Stack<>();
        }
        mWeakReferences.add(activity);
    }

    public void removeActivity(WeakReference<AppCompatActivity> activity) {
        if (mWeakReferences != null) {
            mWeakReferences.remove(activity);
        }
    }

    /***
     * 获取栈顶Activity（堆栈中最后一个压入的）
     *
     * @return Activity
     */
    public Activity getTopActivity() {
        AppCompatActivity activity = mWeakReferences.lastElement().get();
        return activity;
    }

    /***
     * 通过class 获取栈顶Activity
     *
     * @param cls
     * @return Activity
     */
    public AppCompatActivity getActivityByClass(Class<?> cls) {
        AppCompatActivity return_activity = null;
        for (WeakReference<AppCompatActivity> activity : mWeakReferences) {
            if (activity.get().getClass().equals(cls)) {
                return_activity = activity.get();
                break;
            }
        }
        return return_activity;
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void killTopActivity() {
        try {
            WeakReference<AppCompatActivity> activity = mWeakReferences.lastElement();
            killActivity(activity);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /***
     * 结束指定的Activity
     *
     * @param activity
     */
    public void killActivity(WeakReference<AppCompatActivity> activity) {
        try {
            ListIterator<WeakReference<AppCompatActivity>> iterator = mWeakReferences.listIterator();
            while (iterator.hasNext()) {
                WeakReference<AppCompatActivity> stackActivity = iterator.next();
                if (stackActivity.get() == null) {
                    iterator.remove();
                    continue;
                }
                if (stackActivity.get().getClass().getName().equals(activity.get().getClass().getName())) {
                    iterator.remove();
                    stackActivity.get().finish();
                    break;
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 结束除了当前Activity以外的所有Activity
     * @param cls
     */
    public void killAllToLoginActivity(Class<?> cls) {
        try {

            ListIterator<WeakReference<AppCompatActivity>> listIterator = mWeakReferences.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null && cls != activity.getClass()) {
                    activity.finish();
                }
                listIterator.remove();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /***
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void killActivity(Class<?> cls) {
        try {

            ListIterator<WeakReference<AppCompatActivity>> listIterator = mWeakReferences.listIterator();
            while (listIterator.hasNext()) {
                WeakReference<AppCompatActivity> stackActivity = listIterator.next();
                if (stackActivity == null) {
                    listIterator.remove();
                    continue;
                }
                if (stackActivity.getClass() == cls) {
                    listIterator.remove();

                    stackActivity.get().finish();

                    break;
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 结束所有Activity
     */
    public void killAllActivity() {
        try {
            ListIterator<WeakReference<AppCompatActivity>> listIterator = mWeakReferences.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    activity.finish();
                }
                listIterator.remove();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit() {
        try {
            killAllActivity();
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }
}
