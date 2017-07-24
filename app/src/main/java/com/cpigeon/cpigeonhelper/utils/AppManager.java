package com.cpigeon.cpigeonhelper.utils;

import android.app.Activity;
import android.content.Context;
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

    /**
     * Activity栈
     */
    private Stack<WeakReference<AppCompatActivity>> mActivityStack;

    private enum MyEnumSingleton{
        INSTANCE;
        private AppManager appManager;
        MyEnumSingleton(){
            appManager = new AppManager();
        }
        public AppManager getAppManager(){
            return appManager;
        }

    }

    public static AppManager getAppManager(){
        return MyEnumSingleton.INSTANCE.getAppManager();
    }

    /***
     * 栈中Activity的数
     *
     * @return Activity的数
     */
    public int stackSize() {
        return mActivityStack.size();
    }

    /***
     * 获得Activity栈
     *
     * @return Activity栈
     */
    public Stack<WeakReference<AppCompatActivity>> getStack() {
        return mActivityStack;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(WeakReference<AppCompatActivity> activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    public void removeActivity(WeakReference<AppCompatActivity> activity){
        if(mActivityStack!=null){
            mActivityStack.remove(activity);
        }
    }

    /***
     * 获取栈顶Activity（堆栈中最后一个压入的）
     *
     * @return Activity
     */
    public Activity getTopActivity() {
        AppCompatActivity activity = mActivityStack.lastElement().get();
        return activity;
    }

    /***
     * 通过class 获取栈顶Activity
     *
     * @param cls
     * @return Activity
     */
    public Activity getActivityByClass(Class<?> cls) {
        AppCompatActivity return_activity = null;
        for (WeakReference<AppCompatActivity> activity : mActivityStack) {
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
            WeakReference<AppCompatActivity> activity = mActivityStack.lastElement();
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
            Iterator<WeakReference<AppCompatActivity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<AppCompatActivity> stackActivity = iterator.next();
                if(stackActivity.get()==null){
                    iterator.remove();
                    continue;
                }
                if (stackActivity.get().getClass().getName().equals(activity.get().getClass().getName())) {
                    iterator.remove();
                    stackActivity.get().finish();
                    break;
                }
            }
//            if (activity != null) {
//                mActivityStack.remove(activity);
//                activity.finish();
//                activity = null;
//            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /***
     * 方法说明：去除所有的activity 到登录界面
     *
     * @author 王文勤
     * 2015年8月11日下午12:05:41
     */
    public void killAllToLoginActivity(Class<?> cls) {
        try {

            ListIterator<WeakReference<AppCompatActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null && cls != activity.getClass()) {
                    listIterator.remove();
                    activity.finish();
                }
            }

//            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
//                if (null != mActivityStack.get(i) && cls != mActivityStack.get(i).getClass()) {
//                    killActivity(mActivityStack.get(i));
//                    i -= 1;
//                }
//                size = mActivityStack.size();
//            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }


    /***
     * 方法说明：去除所有的activity 到登录界面
     *
     * @author 王文勤
     * 2015年8月11日下午12:05:41
     */
    public void killAllToLoginActivity() {
        try {
            ListIterator<WeakReference<AppCompatActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    listIterator.remove();
                    activity.finish();
                }
            }

//            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
//                if (null != mActivityStack.get(i)) {
//                    killActivity(mActivityStack.get(i));
//                    i -= 1;
//                }
//                size = mActivityStack.size();
//            }
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

            ListIterator<WeakReference<AppCompatActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity == null) {
                    listIterator.remove();
                    continue;
                }
//                if (activity.getClass().getName().equals(cls.getName())) {
                if (activity.getClass() == cls) {
                    listIterator.remove();
                    if (activity != null) {
                        activity.finish();
                    }
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
            ListIterator<WeakReference<AppCompatActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity != null) {
                    activity.finish();
                }
                listIterator.remove();
            }
//			for (int i = 0, size = mActivityStack.size(); i < size; i++) {
//				if (null != mActivityStack.get(i)) {
//					mActivityStack.get(i).finish();
//				}
//			}
//            mActivityStack.clear();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            killAllActivity();
//            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.restartPackage(context.getPackageName());
//            System.exit(0);
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
        }
    }
}
