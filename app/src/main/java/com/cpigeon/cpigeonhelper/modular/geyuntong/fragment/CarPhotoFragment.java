package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.CarPhotoAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.CarVideoAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.MyDecoration;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/1.
 */

public class CarPhotoFragment extends BaseFragment {

    @BindView(R.id.btn_add_photo)
    Button btnAddPhoto;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private CarPhotoAdapter mAdapter;
    private boolean mIsRefreshing = false;
    private GeYunTong geYunTong;
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
        startActivity(new Intent(getActivity(),UploadImgActivity.class));
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
                    if (listApiResponse.isStatus()&&listApiResponse.getData()!=null&&listApiResponse.getData().size()>0) {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    } else {
                        initEmptyView("暂无数据");
                    }
                }, throwable -> {
                    initEmptyView("加载失败，请检查网络设置。");
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
        mAdapter = new CarPhotoAdapter(null);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDecoration(getActivity(),MyDecoration.VERTICAL_LIST));

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
