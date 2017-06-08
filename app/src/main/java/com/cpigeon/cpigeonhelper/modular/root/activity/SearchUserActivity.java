package com.cpigeon.cpigeonhelper.modular.root.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 * Created by Administrator on 2017/5/27.
 *
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
    private String s;
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
        this.setTopLeftButton(R.drawable.ic_back, this::finish);
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
            s = TextUtils.isEmpty(userInfoByTelBean.getNickname()) ? userInfoByTelBean.getNickname() : userInfoByTelBean.getNickname();
            Logger.e("当前用户的昵称"+s);
            if (userInfoByTelBean.getAuthUid() == AssociationData.getUserId())
            {
                Toast.makeText(mContext, "该账户已经是授权账户", Toast.LENGTH_SHORT).show();
            }else if (userInfoByTelBean.getAuthUid()!=0 && userInfoByTelBean.getAuthUid()!= AssociationData.getUserId()){
                Toast.makeText(mContext, "当前账户已被其他协会或公棚授权", Toast.LENGTH_SHORT).show();
            }else {
                new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("添加用户")
                        .setContentText("是否添加"+s+"为授权用户?")
                        .setConfirmText("我确定")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                            setUserPermission(userInfoByTelBean.getUid());
                        })
                        .setCancelText("点错了")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation).show();
            }
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
                    new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("添加成功")
                            .setContentText("成功添加"+s+"为授权用户")
                            .setConfirmText("确定")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            }

                            ).show();
                },throwable -> {
                    new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("添加错误")
                            .setContentText(throwable.getMessage())
                            .setConfirmText("确定")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            }).show();
                });
    }

    public void initEmptyView(String tips) {
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
