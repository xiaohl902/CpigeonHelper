package com.cpigeon.cpigeonhelper.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/5/25.
 */

public abstract class BaseActivity extends RxAppCompatActivity{
    private Unbinder mUnbinder;
    public Context mContext;
    public WeakReference<AppCompatActivity> mWeakReference;
    public int mColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        swipeBack();
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        mUnbinder = ButterKnife.bind(this);
        mContext = MyApp.getInstance();
        //初始化设置StatusBar
        setStatusBar();
        //初始化ToolBar
        initToolBar();
        //初始化控件
        initViews(savedInstanceState);

    }

    protected abstract void swipeBack();


    @LayoutRes
    protected abstract int getLayoutId();

    public void loadData() {}

    protected abstract void setStatusBar();


    protected abstract void initToolBar();


    protected abstract void initViews(Bundle savedInstanceState);


    public void showProgressBar() {}


    public void hideProgressBar() {}


    public void initRecyclerView() {}


    public void initRefreshLayout() {}

    public void finishTask() {}

    private void doBeforeSetcontentView() {
        mWeakReference = new WeakReference<AppCompatActivity>(this);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(mWeakReference);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //获取颜色
        mColor = getResources().getColor(R.color.colorPrimary);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        AppManager.getAppManager().removeActivity(mWeakReference);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
    }

    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 判定是否需要隐藏
    protected boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom);
        }
        return false;
    }

    // 隐藏软键盘
    protected void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
