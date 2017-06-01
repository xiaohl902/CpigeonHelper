package com.cpigeon.cpigeonhelper.modular.root.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.ApiConstants;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.root.adapter.RootListAdapter;
import com.cpigeon.cpigeonhelper.modular.root.adapter.SearchListAdapter;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootList;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootManagerList;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootUserBean;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserInfoByTelBean;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.ui.SnackbarUtil;
import com.cpigeon.cpigeonhelper.ui.searchview.SearchEditText;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/5/27.
 */

public class SearchUserActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.search_edittext)
    SearchEditText searchEdittext;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    private SearchListAdapter mAdapter;
    private long timestamp;
    private Map<String, Object> postParams;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_searchuser;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("添加授权");
        this.setTopLeftButton(R.drawable.ic_back, () -> SearchUserActivity.this.finish());
        this.initRecyclerView();

    }



    @OnClick(R.id.tv_search)
    public void onViewClicked() {
        clearData();
        timestamp = System.currentTimeMillis() / 1000;
        postParams = new HashMap<>();
        postParams.put("p", searchEdittext.getText().toString().trim());
        if (!TextUtils.isEmpty(searchEdittext.getText().toString().trim())) {
            RetrofitHelper.getApi()
                    .getUserInfoByTel(AssociationData.getUserToken(),
                            searchEdittext.getText().toString().trim(),
                            timestamp, CommonUitls.getApiSign(timestamp,postParams))
                    .throttleFirst(10, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listApiResponse -> {
                        if (listApiResponse.isStatus()&&listApiResponse.getData()!=null)
                        {
                            mAdapter.setNewData(listApiResponse.getData());
                            finishTask();
                        }else {
                            initEmptyView("暂无数据");
                        }
                    }, throwable -> initEmptyView("出错了"+throwable.getMessage()));
        }
    }

    private void clearData() {
        if (postParams!=null)
        {
            postParams.clear();
        }

    }

    @Override
    public void finishTask() {
        hideEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new SearchListAdapter(null);
        mAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            UserInfoByTelBean userInfoByTelBean = (UserInfoByTelBean) baseQuickAdapter.getData().get(i);
            Logger.e("当前用户的uid"+userInfoByTelBean.getUid());
            setUserPermission(userInfoByTelBean.getUid());
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUserPermission(int auuid) {
        timestamp = System.currentTimeMillis() /1000;
        postParams.clear();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("auuid", String.valueOf(auuid))
                .build();
        postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("auuid", String.valueOf(auuid));
        RetrofitHelper.getApi()
                .setAuthUserPermissions(AssociationData.getUserToken(),
                        requestBody,timestamp,CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rootManagerListApiResponse -> {

                        Logger.e("授权成功了");

                },throwable -> {
                    Logger.e("授权出了问题"+throwable.getMessage());
                });
    }

    public void initEmptyView(String tips) {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


}