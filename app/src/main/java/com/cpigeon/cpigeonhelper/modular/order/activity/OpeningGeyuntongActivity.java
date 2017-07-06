package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.order.adapter.PackageInfoAdapter;
import com.cpigeon.cpigeonhelper.modular.order.bean.PackageInfo;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/26.
 */

public class OpeningGeyuntongActivity extends ToolbarBaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private PackageInfoAdapter mAdapter;
    private boolean mIsRefreshing = false;
    @Override
    protected void swipeBack() {
    }

    @Override
    protected int getContentView() {
        return R.layout.layout_swipwithrecycler;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);//最后一个参数代表了透明度，0位全部透明
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("开通套餐");
        this.setTopLeftButton(R.drawable.ic_back, OpeningGeyuntongActivity.this::finish);
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getApi()
                .getServicePackageInfo("gytopen")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0&&listApiResponse.getData()!=null&&listApiResponse.getData().size()>0) {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    } else {
                        initEmptyView("暂无数据");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        initEmptyView("连接超时了");
                    }else if (throwable instanceof ConnectException){
                        initEmptyView("连接失败了");
                    }else {
                        initEmptyView("发生了不可预期的错误："+throwable.getMessage());
                    }
                });
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new PackageInfoAdapter(null);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PackageInfo packageInfo = (PackageInfo) adapter.getData().get(position);
            new SweetAlertDialog(OpeningGeyuntongActivity.this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("是否开通"+packageInfo.getPackageName()+"套餐?")
                    .setConfirmText("确认")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        createWxOrder(packageInfo.getPackageName(),packageInfo.getId());
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(Dialog::dismiss)
                    .show();
        });


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createWxOrder(String packageName, int id) {
        Intent i = new Intent(this,PayGeyuntongActivity.class);
        i.putExtra("sid",id);
        startActivity(i);
    }


    @Override
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mIsRefreshing = true;
            loadData();
        });
    }
    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void initEmptyView(String tips) {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
    }

}
