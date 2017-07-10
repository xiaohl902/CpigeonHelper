package com.cpigeon.cpigeonhelper.modular.order.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.order.adapter.ReChargeGYTAdapter;
import com.cpigeon.cpigeonhelper.modular.order.adapter.VipLevelUpAdapter;
import com.cpigeon.cpigeonhelper.modular.order.bean.PackageInfo;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.DateUtils;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/7/10.
 */

public class ReChargeActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_userBelongXiehui)
    TextView tvUserBelongXiehui;
    @BindView(R.id.tv_user_state)
    TextView tvUserState;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_user_time)
    TextView tvUserTime;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ReChargeGYTAdapter mAdapter;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_rechargegyt;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("续费会员");
        setTopLeftButton(R.drawable.ic_back,this::finish);
        initView();
        initRecyclerView();
        loadGYTServer();
    }

    private void loadGYTServer() {
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        RetrofitHelper.getApi()
                .getGytRenewalServicePackageInfo(AssociationData.getUserToken(),urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0&&listApiResponse.getData()!=null&&listApiResponse.getData().size()>0) {
                        mAdapter.setNewData(listApiResponse.getData());
                        finishTask();
                    } else {
                        CommonUitls.showToast(this,"暂无数据");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时了");
                    }else if (throwable instanceof ConnectException){
                        CommonUitls.showToast(this,"连接失败了");
                    }else {
                        CommonUitls.showToast(this,"发生了不可预期的错误："+throwable.getMessage());
                    }
                });
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new ReChargeGYTAdapter(null);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PackageInfo packageInfo = (PackageInfo) adapter.getData().get(position);
            new SweetAlertDialog(ReChargeActivity.this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("是否从"+packageInfo.getPackageName()+"套餐?")
                    .setConfirmText("确认")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        createWxOrder(packageInfo.getId(),"renewal");
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(Dialog::dismiss)
                    .show();
        });


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createWxOrder(int id,String type) {
        Intent i = new Intent(this,PayGeyuntongActivity.class);
        i.putExtra("sid",id);
        i.putExtra("type",type);
        startActivity(i);
    }


    private void initView() {
        RealmResults<GYTService> gytServices = RealmUtils.getInstance().queryGTYInfo();
        if (TextUtils.isEmpty(gytServices.get(0).getGrade()))
        {
            tvUserState.setText("普通用户");
        }else {
            tvUserState.setText(gytServices.get(0).getGrade().toUpperCase());
        }
        tvUserTime.setText(DateUtils.compareDate(DateUtils.millis2String(System.currentTimeMillis()),gytServices.get(0).getExpireTime()));
    }


    @Override
    public void finishTask() {
        mAdapter.notifyDataSetChanged();
    }
}
