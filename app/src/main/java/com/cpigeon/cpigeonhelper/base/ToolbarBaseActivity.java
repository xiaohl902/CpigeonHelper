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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 * Created by Administrator on 2017/5/26.
 *
 */

public abstract class ToolbarBaseActivity extends RxAppCompatActivity {


    private Toolbar toolbar;
    private FrameLayout viewContent;
    private TextView tvTitle;
    private Unbinder mUnbinder;
    public Context mContext;
    public WeakReference<AppCompatActivity> mWeakReference;
    public int mColor;
    private OnClickListener onClickListenerTopLeft,onClickListenerTopRight;
    private int menuResId;//右边图标
    private String menuStr;//名称
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        swipeBack();
        setContentView(R.layout.activity_base_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewContent = (FrameLayout) findViewById(R.id.viewContent);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        //设置mContext
        mContext = MyApp.getInstance();
        //初始化设置StatusBar
        setStatusBar();
        //初始化ToolBar
        setSupportActionBar(toolbar);
        //设置不显示自带的title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //
        LayoutInflater.from(ToolbarBaseActivity.this).inflate(getContentView(), viewContent);

        mUnbinder = ButterKnife.bind(this);

        //
        initViews(savedInstanceState);
    }

    protected void setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
    }

    protected void setTopLeftButton(){
        setTopLeftButton(R.drawable.ic_back, null);
    }

    protected void setTopLeftButton(int iconResId, OnClickListener onClickListener){
        toolbar.setNavigationIcon(iconResId);
        this.onClickListenerTopLeft = onClickListener;
    }

    protected void setTopRightButton(String menuStr, OnClickListener onClickListener){
        this.onClickListenerTopRight = onClickListener;
        this.menuStr = menuStr;
    }

    protected void setTopRightButton(String menuStr, int menuResId, OnClickListener onClickListener){
        this.menuResId = menuResId;
        this.menuStr = menuStr;
        this.onClickListenerTopRight = onClickListener;
    }

    protected abstract void swipeBack();

    protected abstract int getContentView();

    protected abstract void setStatusBar();

    protected abstract void initViews(Bundle savedInstanceState);

    public void loadData() {
    }


    public void showProgressBar() {
    }


    public void hideProgressBar() {
    }


    public void initRecyclerView() {
    }


    public void initRefreshLayout() {
    }

    public void finishTask() {
    }

    private void doBeforeSetcontentView() {
        mWeakReference = new WeakReference<AppCompatActivity>(this);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(mWeakReference);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        AppManager.getAppManager().removeActivity(mWeakReference);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onClickListenerTopLeft.onClick();
        }
        else if (item.getItemId() == R.id.menu_function){
            onClickListenerTopRight.onClick();
        }

        return true; // true 告诉系统我们自己处理了点击事件
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuResId != 0 || !TextUtils.isEmpty(menuStr)){
            getMenuInflater().inflate(R.menu.menu_activity_base_toolbar, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menuResId != 0) {
            menu.findItem(R.id.menu_function).setIcon(menuResId);
        }
        if (!TextUtils.isEmpty(menuStr)){
            menu.findItem(R.id.menu_function).setTitle(menuStr);
        }
        return super.onPrepareOptionsMenu(menu);
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

    public interface OnClickListener{
        void onClick();
    }

}
