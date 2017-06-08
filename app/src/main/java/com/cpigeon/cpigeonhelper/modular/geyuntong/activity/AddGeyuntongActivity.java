package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.xiehui.fragment.EditDialogFragment;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

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
 * Created by Administrator on 2017/6/7.
 *
 */

public class AddGeyuntongActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_geyuntong_name)
    TextView tvGeyuntongName;
    @BindView(R.id.tv_geyuntong_place)
    TextView tvGeyuntongPlace;
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;
    @BindView(R.id.ll_geyuntong_name)
    LinearLayout llGeyuntongName;
    @BindView(R.id.ll_geyuntong_place)
    LinearLayout llGeyuntongPlace;
    @BindView(R.id.ll_geyuntong_latlng)
    LinearLayout llGeyuntongLatlng;
    private long timestamp;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this,mColor,0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("添加比赛");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("完成", this::add);
    }

    @OnClick({R.id.ll_geyuntong_name, R.id.ll_geyuntong_place, R.id.ll_geyuntong_latlng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_geyuntong_name:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_GYP_NAME,"比赛名称").show(getSupportFragmentManager(),"提示");
                break;
            case R.id.ll_geyuntong_place:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_GYP_PLACE,"司放地点").show(getSupportFragmentManager(),"提示");
                break;
            case R.id.ll_geyuntong_latlng:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_GYP_LATLNG,"司放地坐标").show(getSupportFragmentManager(),"提示");
                break;
        }
    }
    private void add(){
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("type","xiehui")
                .addFormDataPart("rname",tvGeyuntongName.getText().toString())
                .build();

        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid",String.valueOf(AssociationData.getUserId()));
        postParams.put("type","xiehui");
        postParams.put("rname",tvGeyuntongName.getText().toString());


        RetrofitHelper.getApi().createGeYunTongRace(AssociationData.getUserToken()
                , mRequestBody
                , timestamp, CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(geYunTongApiResponse -> {
                        if (geYunTongApiResponse.isStatus())
                        {
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("添加成功")
                                    .setConfirmText("知道了")
                                    .show();
                        }else {
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("添加失败了")
                                    .setConfirmText("知道了")
                                    .show();
                        }
                }, throwable -> {
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("添加失败了")
                            .setConfirmText("知道了")
                            .show();
                });

    }
}
