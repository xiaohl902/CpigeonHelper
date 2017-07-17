package com.cpigeon.cpigeonhelper.modular.flyarea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.flyarea.adapter.SimpleFlyingAreaAdapter;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by Administrator on 2017/7/14.
 *
 */

public class SimpleFlyingAreaActivity  extends ToolbarBaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private SimpleFlyingAreaAdapter mAdapter;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.layout_swipwithrecycler;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("选择司放地");
        setTopLeftButton(R.drawable.ic_back,this::finish);
        setTopRightButton("添加",R.drawable.ic_add,this::add);
        initRefreshLayout();
        initRecyclerView();
    }

    public void add()
    {
        startActivity(new Intent(this,FlyingAreaActivity.class));
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new SimpleFlyingAreaAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlyingArea flyingarea = (FlyingArea)adapter.getItem(position);
            EventBus.getDefault().post(flyingarea);
            this.finish();
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        setRecycleNoScroll();
    }

    @Override
    public void loadData() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("type", "user");
        urlParams.put("pi", "-1");
        urlParams.put("ps", "10");
        RetrofitHelper.getApi()
                .getFlyingAreas(AssociationData.getUserId(), urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getData() != null && listApiResponse.getData().size()>0)
                    {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    }else {
                        initEmptyView(listApiResponse.getMsg());
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        initEmptyView("连接超时了？");
                    }else if (throwable instanceof ConnectException){
                        initEmptyView("连接失败了？");
                    }else {
                        initEmptyView("发生了不可预期的错误："+throwable.getMessage());
                    }
                });
    }

    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            clearData();
            loadData();
        });
    }

    private void clearData() {
        mIsRefreshing = true;

    }

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

    private void setRecycleNoScroll() {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    public void initEmptyView(String tips) {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
    }
}
