package com.cpigeon.cpigeonhelper.modular.root.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.root.adapter.RootListAdapter;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootList;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.MyDecoration;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RootListActivity extends ToolbarBaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RootListAdapter mAdapter;
    private boolean mIsRefreshing = false;
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
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("账户授权");
        this.setTopLeftButton(R.drawable.ic_back, this::finish);
        this.setTopRightButton("完成",
                R.drawable.ic_addroot, () -> startActivity(new Intent(RootListActivity.this, SearchUserActivity.class)));
        initRefreshLayout();
        initRecyclerView();
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
    public void loadData() {
        RetrofitHelper.getApi()
                .getAuthUsers(AssociationData.getUserToken(), String.valueOf(AssociationData.getUserId()))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0&&listApiResponse.getData()!=null&&listApiResponse.getData().size()>0) {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    } else {
                        initEmptyView("暂无授权用户");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        initEmptyView("连接超时");
                    }else if (throwable instanceof ConnectException)
                    {
                        initEmptyView("无法连接到服务器");
                    }else if (throwable instanceof RuntimeException){
                        initEmptyView("发生了不可预知的错误"+throwable.getMessage());
                    }
                });
    }

    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new RootListAdapter(null);
        mAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            RootList rootList = (RootList) baseQuickAdapter.getData().get(i);
            Intent intent = new Intent(RootListActivity.this, RootManagerActivity.class);
            intent.putExtra("auuid", rootList.getAuthUserInfo().getUid());
            intent.putExtra("imgurl", rootList.getAuthUserInfo().getHeadimgUrl());
            intent.putExtra("name", TextUtils.isEmpty(rootList.getAuthUserInfo().getName())?
                    rootList.getAuthUserInfo().getNickname():rootList.getAuthUserInfo().getName());

            startActivity(intent);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDecoration(this,MyDecoration.VERTICAL_LIST));

    }

    public void initEmptyView(String tips) {

        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
