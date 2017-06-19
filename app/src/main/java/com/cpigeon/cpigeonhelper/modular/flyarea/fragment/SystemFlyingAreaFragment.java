package com.cpigeon.cpigeonhelper.modular.flyarea.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.flyarea.adapter.SystemFlyingAreaAdapter;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/14.
 */

public class SystemFlyingAreaFragment extends BaseFragment {

    public static SystemFlyingAreaFragment newInstance() {

        return new SystemFlyingAreaFragment();
    }

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private long timestamp;
    private SystemFlyingAreaAdapter mAdapter;

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
        mAdapter = new SystemFlyingAreaAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlyingArea flyingArea = (FlyingArea) adapter.getData().get(position);
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("是否添加到我的司放地?")
                    .setConfirmText("确认")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                                .addFormDataPart("type", "xiehui")
                                .addFormDataPart("alias", flyingArea.getAlias())
                                .addFormDataPart("area", flyingArea.getArea())
                                .addFormDataPart("lo", String.valueOf(flyingArea.getLongitude()))
                                .addFormDataPart("la", String.valueOf(flyingArea.getLatitude()))
                                .build();

                        Map<String, Object> postParams = new HashMap<>();
                        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                        postParams.put("type", "xiehui");
                        postParams.put("alias", flyingArea.getAlias());
                        postParams.put("area", flyingArea.getArea());
                        postParams.put("lo", String.valueOf(flyingArea.getLongitude()));
                        postParams.put("la", String.valueOf(flyingArea.getLatitude()));

                        timestamp = System.currentTimeMillis() / 1000;

                        RetrofitHelper.getApi()
                                .createFlyingArea(AssociationData.getUserToken(), mRequestBody, timestamp,
                                        CommonUitls.getApiSign(timestamp, postParams))
                                .compose(bindToLifecycle())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(flyingAreaApiResponse -> {
                                    if (flyingAreaApiResponse.getErrorCode() == 0) {
                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("成功")
                                                .setContentText("添加司放地成功")
                                                .setConfirmText("好的")
                                                .show();
                                    } else {
                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("错误")
                                                .setContentText(flyingAreaApiResponse.getMsg())
                                                .setConfirmText("好的")
                                                .show();
                                    }
                                }, throwable -> new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("错误")
                                        .setContentText(throwable.getMessage())
                                        .setConfirmText("好的")
                                        .show());
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                    .show();
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        setRecycleNoScroll();
    }

    @Override
    protected void loadData() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("type", "refer");
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
                        initEmptyView();
                    }


                }, throwable -> {
                    initErrorView();
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

    public void initErrorView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }

    public void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("暂无数据，快去添加吧");
    }
}
