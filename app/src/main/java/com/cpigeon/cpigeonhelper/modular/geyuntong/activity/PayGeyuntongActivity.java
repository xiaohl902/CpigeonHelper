package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;

import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.r0adkll.slidr.Slidr;

/**
 * Created by Administrator on 2017/5/27.
 */

public class PayGeyuntongActivity extends ToolbarBaseActivity {
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void setStatusBar() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }
}
