package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.order.adapter.PackageInfoAdapter;
import com.cpigeon.cpigeonhelper.modular.order.bean.PackageInfo;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 开通鸽运通的界面
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
                        createWxOrder(packageInfo.getId(),"open");
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createWxOrder(int id,String type) {
        Intent i = new Intent(this,PayGeyuntongActivity.class);
        i.putExtra("sid",id);
        i.putExtra("type",type);
        startActivity(i);
    }
    @Override
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::loadData);
    }
    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
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
