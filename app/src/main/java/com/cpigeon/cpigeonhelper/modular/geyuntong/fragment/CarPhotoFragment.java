package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.UploadImgActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.TimeLinePhotoAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.RaceImageOrVideo;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by Administrator on 2017/6/1.
 *
 */

public class CarPhotoFragment extends BaseFragment {

    @BindView(R.id.btn_add_photo)
    FloatingActionButton btnAddPhoto;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TimeLinePhotoAdapter mAdapter;
    private boolean mIsRefreshing = false;
    private GeYunTong geYunTong;
    private List<LocalMedia> list = new ArrayList<>();
    public static CarPhotoFragment newInstance() {

        return new CarPhotoFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_photo;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
    }

    @Override
    public void finishCreateView(Bundle state) {

        if (geYunTong.getStateCode()==2)
        {
            btnAddPhoto.setVisibility(View.GONE);
        }else {
            btnAddPhoto.setVisibility(View.VISIBLE);
        }
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

    @OnClick(R.id.btn_add_photo)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(),UploadImgActivity.class);
        intent.putExtra("rid",geYunTong.getId());
        startActivity(intent);
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

            mIsRefreshing = true;

            loadData();
        });
    }
    @Override
    public void loadData() {
        if (list.size() > 0)
        {
            list.clear();
        }
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("rid",geYunTong.getId());
        urlParams.put("ft","image");

        RetrofitHelper.getApi()
                .getGYTRaceImageOrVideo(
                        AssociationData.getUserToken(),
                        urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0&&listApiResponse.getData()!=null&&listApiResponse.getData().size()>0) {

                        for (RaceImageOrVideo raceImage :listApiResponse.getData())
                        {
                            LocalMedia localMedia = new LocalMedia();
                            localMedia.setPath(raceImage.getUrl());
                            list.add(localMedia);
                        }
                        mAdapter.setNewData(listApiResponse.getData());
                        Logger.e("图片的size："+list.size());
                        finishTask();
                    } else {
                        initEmptyView("暂无数据，快去添加照片吧");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        initEmptyView("连接超时，网络状态不太好哦");
                    }else if (throwable instanceof ConnectException)
                    {
                        initEmptyView("连接异常，请检查网络连接哦");
                    }else if (throwable instanceof RuntimeException)
                    {
                        initEmptyView("发生了不可预期的错误");
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
        mAdapter = new TimeLinePhotoAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (list.size() > 0) {
                PictureSelector.create(CarPhotoFragment.this).externalPicturePreview(position, list);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


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
