package com.cpigeon.cpigeonhelper.modular.setting.activity;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;

/**
 * Created by Administrator on 2017/6/20.
 */

public class AboutActivity extends ToolbarBaseActivity{
    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    protected void setStatusBar() {
        setTitle("关于");
        setTopLeftButton(R.drawable.ic_back,this::finish);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }
}
