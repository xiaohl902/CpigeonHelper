package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.order.adapter.OrderListAdapter;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.CustomLoadMoreView;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 * Created by Administrator on 2017/6/20.
 *
 */

public class OrderListActivity extends ToolbarBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false;
    private OrderListAdapter mAdapter;
    private static final int ps = 6;
    private int pi = 1;
    boolean canLoadMore = true,isMoreDateLoading = false;
    private int mCurrentCounter = 0;
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
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的订单");
        setTopLeftButton(R.drawable.ic_back,this::finish);
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
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

    @Override
    public void initRecyclerView() {
        mAdapter = new OrderListAdapter(null);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setEnableLoadMore(true);
        setRecycleNoScroll();
    }

    @Override
    public void loadData() {
        long timestamp = System.currentTimeMillis() / 1000;

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("ps", String.valueOf(ps))
                .addFormDataPart("pi", String.valueOf(pi))
                .build();

        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("ps", String.valueOf(ps));
        postParams.put("pi",String.valueOf(pi));

        RetrofitHelper.getApi()
                .getOrderList(AssociationData.getUserToken(),requestBody,
                        timestamp, CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0 && listApiResponse.getData().size() > 0)
                    {
                        mAdapter.addData(listApiResponse.getData());
                        canLoadMore = listApiResponse.getData()!=null && listApiResponse.getData().size() == ps;
                        mCurrentCounter = mAdapter.getData().size();
                        finishTask();
                    }else {
                        initErrorView(listApiResponse.getMsg());
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        initErrorView("连接超时了，都啥年代了还塞网络？");
                    }else if (throwable instanceof ConnectException){
                        initErrorView("连接失败了，都啥年代了无网络？");
                    }else {
                        initErrorView("发生了不可预期的错误："+throwable.getMessage());
                    }
                });

    }

    @Override
    public void finishTask() {
        if (canLoadMore)
        {
            pi++;
            mAdapter.loadMoreComplete();
        }else {
            mAdapter.loadMoreEnd(false);
        }
        isMoreDateLoading = mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
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

    public void initErrorView(String tips) {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
    }


    private void clearData() {
        mIsRefreshing = true;
    }

    @Override
    public void onLoadMoreRequested() {
       if (canLoadMore)
       {
           mSwipeRefreshLayout.setEnabled(false);
           isMoreDateLoading = true;
           loadData();
       }else {
           mAdapter.setEnableLoadMore(false);

       }
    }


}
