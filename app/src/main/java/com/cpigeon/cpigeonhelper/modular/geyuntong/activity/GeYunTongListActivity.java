package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GeYunTongListAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.MyDecoration;
import com.cpigeon.cpigeonhelper.ui.searchview.SearchEditText;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/7.
 */

public class GeYunTongListActivity extends ToolbarBaseActivity implements SearchEditText.OnSearchClickListener {
    @BindView(R.id.search_edittext)
    SearchEditText searchEdittext;
    @BindView(R.id.recycleview_geyutong_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.iv_add)
    FloatingActionButton ivAdd;
//    @BindView(R.id.iv_help)
//    ImageView ivHelp;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private GeYunTongListAdapter mAdapter;
    private boolean mIsRefreshing = false;
    @Override
    protected void swipeBack() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_geyuntong_list;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("鸽运通");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        initRefreshLayout();
        initRecyclerView();
        searchEdittext.setOnSearchClickListener(this);
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
    public void initRecyclerView() {
        mAdapter = new GeYunTongListAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            GeYunTong geYunTong = (GeYunTong) adapter.getData().get(position);
            if (geYunTong.getStateCode() == 1 && geYunTong.getMuid() != AssociationData.getUserId()) {
                Logger.e("geYunTong.getMuid()" + geYunTong.getMuid());
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("警告")
                        .setContentText("该场比赛正在监控中，你无法进入")
                        .setConfirmText("知道了")
                        .show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("geyuntong", geYunTong);
                Intent intent = new Intent(GeYunTongListActivity.this, ACarServiceActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }


        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST));
    }

    @Override
    public void loadData() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("type", AssociationData.getUserType());
        urlParams.put("key", "");
        RetrofitHelper.getApi()
                .getGeYunTongRaceList(AssociationData.getUserToken(), urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0&&listApiResponse.getData() != null && listApiResponse.getData().size() > 0) {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    } else if (listApiResponse.getErrorCode() == 0 &&listApiResponse.getData().size() == 0){
                        initEmptyView("暂时比赛信息");
                    }else {
                        initEmptyView(listApiResponse.getMsg());
                    }

                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        initEmptyView("连接超时了，网络质量可能有些差");
                    } else if (throwable instanceof ConnectException) {
                        initEmptyView("连接失败了，请您检查您的网络连接");
                    } else {
                        initEmptyView("发生了不可预期的错误：" + throwable.getMessage());
                    }
                });
    }

    private void initEmptyView(String msg) {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(msg);
    }

    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({ R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                startActivity(new Intent(GeYunTongListActivity.this, AddGeyuntongActivity.class));
                break;
        }
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchClick(View view, String keyword) {

    }

}
