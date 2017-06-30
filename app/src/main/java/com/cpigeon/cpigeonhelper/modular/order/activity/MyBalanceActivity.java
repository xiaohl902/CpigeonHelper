package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

/**
 * Created by Administrator on 2017/6/30.
 */

public class MyBalanceActivity extends ToolbarBaseActivity {
    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_balance;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("钱包");
        setTopLeftButton(R.drawable.ic_back,this::finish);
        setTopRightButton("明细",this::moreDetails);
    }

    public void moreDetails(){

    }
}
