package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GeYunTongListAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.MyDecoration;
import com.cpigeon.cpigeonhelper.ui.searchview.SearchEditText;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/7.
 */

public class GeYunTongListActivity extends ToolbarBaseActivity {
    @BindView(R.id.search_edittext)
    SearchEditText searchEdittext;
    @BindView(R.id.recycleview_geyutong_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_help)
    ImageView ivHelp;
    private GeYunTongListAdapter mAdapter;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_geyuntong_list;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("鸽运通");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("编辑", this::edit);
        initRecyclerView();
        loadData();
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new GeYunTongListAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            GeYunTong geYunTong = (GeYunTong) adapter.getData().get(position);
            Intent intent = new Intent(GeYunTongListActivity.this,ACarServiceActivity.class);
            intent.putExtra("longitude",geYunTong.getLongitude());
            intent.putExtra("latitude",geYunTong.getLatitude());
            startActivity(intent);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDecoration(this,MyDecoration.VERTICAL_LIST));
    }

    @Override
    public void loadData() {
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("uid",AssociationData.getUserId());
        urlParams.put("type","xiehui");
        urlParams.put("key","");
        RetrofitHelper.getApi()
                .getGeYunTongRaceList(AssociationData.getUserToken(),urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getData()!=null && listApiResponse.getData().size()>0)
                    {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    }else {
                        showEmptyView();
                    }

                }, throwable -> showErrorView(throwable.getMessage()));
    }

    private void showErrorView(String msg) {

        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(msg+"，点我刷新");
    }

    @Override
    public void finishTask() {
        hideEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    private void showEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("没有比赛列表，快去添加吧");
    }

    private void edit() {

    }

    @OnClick({R.id.empty_view, R.id.iv_add, R.id.iv_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.empty_view:
                loadData();
                break;
            case R.id.iv_add:
                startActivity(new Intent(GeYunTongListActivity.this,AddGeyuntongActivity.class));
                break;
            case R.id.iv_help:
                break;
        }
    }

    public void hideEmptyView() {

        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
