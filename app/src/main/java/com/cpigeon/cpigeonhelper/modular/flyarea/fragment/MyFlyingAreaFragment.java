package com.cpigeon.cpigeonhelper.modular.flyarea.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.flyarea.activity.FlyingAreaEditActivity;
import com.cpigeon.cpigeonhelper.modular.flyarea.adapter.FlyingAreaAdapter;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyFlyingAreaFragment extends BaseFragment {

    public static MyFlyingAreaFragment newInstance() {

        return new MyFlyingAreaFragment();
    }

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;

    private FlyingAreaAdapter mAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.layout_swipwithrecycler;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {

        if (!isPrepared || !isVisible) {
            return;
        }

        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    @OnClick(R.id.empty_layout)
    public void onViewClicked() {

    }


    @Override
    protected void initRecyclerView() {
        mAdapter = new FlyingAreaAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlyingArea flyingArea = (FlyingArea) adapter.getData().get(position);
            Intent i = new Intent(getActivity(), FlyingAreaEditActivity.class);
            i.putExtra("faid",flyingArea.getFaid());
            i.putExtra("place",flyingArea.getArea());
            i.putExtra("lo",flyingArea.getLongitude());
            i.putExtra("la",flyingArea.getLatitude());
            i.putExtra("alias",flyingArea.getAlias());
            startActivity(i);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        setRecycleNoScroll();
    }

    @Override
    protected void loadData() {
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
                        initEmptyView("啊偶，连接超时了，都啥年代了还塞网络？");
                    }else if (throwable instanceof ConnectException){
                        initEmptyView("啊偶，连接失败了，都啥年代了无网络？");
                    }else {
                        initEmptyView("啊偶，发生了不可预期的错误："+throwable.getMessage());
                    }
                });
    }

    @Override
    protected void initRefreshLayout() {
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

    @Override
    protected void finishTask() {
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
