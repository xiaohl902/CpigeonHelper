package com.cpigeon.cpigeonhelper.modular.flyarea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.EditText;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
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
 * 司放地编辑界面
 * Created by Administrator on 2017/6/15.
 */

public class FlyingAreaEditActivity extends ToolbarBaseActivity {
    @BindView(R.id.et_flyingarea_place)
    EditText etFlyingareaPlace;
    @BindView(R.id.et_flyingarea_la)
    EditText etFlyingareaLa;
    @BindView(R.id.et_flyingarea_lo)
    EditText etFlyingareaLo;
    @BindView(R.id.et_flyingarea_alias)
    EditText etFlyingareaAlias;
    @BindView(R.id.btn_delete_flyingarea)
    Button btnDeleteFlyingarea;
    private int faid;
    private String place;
    private Double la;
    private Double lo;
    private String alias;
    private long timestamp;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_flyingarea_edit;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        setTitle("编辑");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("完成", this::save);

        Intent intent = getIntent();
        faid = intent.getIntExtra("faid",0);
        la = intent.getDoubleExtra("la",0);
        lo = intent.getDoubleExtra("lo",0);
        place = intent.getStringExtra("place");
        alias = intent.getStringExtra("alias");

        Logger.e(la+lo+place+alias);

        etFlyingareaPlace.setText(place);
        etFlyingareaLa.setText(String.format("%.6f",la));
        etFlyingareaLo.setText(String.format("%.6f",lo));
        etFlyingareaAlias.setText(alias);
    }

    public void save() {
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("type", AssociationData.getUserType())
                .addFormDataPart("faid", String.valueOf(faid))
                .addFormDataPart("alias", etFlyingareaAlias.getText().toString().trim())
                .addFormDataPart("area", etFlyingareaPlace.getText().toString().trim())
                .addFormDataPart("lo", etFlyingareaLo.getText().toString().trim())
                .addFormDataPart("la", etFlyingareaLa.getText().toString().trim())
                .build();
        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("type", AssociationData.getUserType());
        postParams.put("faid", String.valueOf(faid));
        postParams.put("alias", etFlyingareaAlias.getText().toString().trim());
        postParams.put("area", etFlyingareaPlace.getText().toString().trim());
        postParams.put("lo", etFlyingareaLo.getText().toString().trim());
        postParams.put("la", etFlyingareaLa.getText().toString().trim());


        RetrofitHelper.getApi()
                .modifyflyingarea(AssociationData.getUserToken(),
                        mRequestBody,timestamp, CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flyingAreaApiResponse -> {
                    if (flyingAreaApiResponse.getErrorCode() == 0){
                        new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("成功")
                                .setContentText("修改成功")
                                .setConfirmText("好的")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                })
                                .show();
                    }else {
                        new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误提示")
                                .setContentText(flyingAreaApiResponse.getMsg())
                                .setConfirmText("好的")
                                .show();
                    }
                }, throwable -> new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("错误提示")
                        .setContentText(throwable.getMessage())
                        .setConfirmText("好的")
                        .show());
    }


    @OnClick(R.id.btn_delete_flyingarea)
    public void onViewClicked() {

        timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("faid", String.valueOf(faid))
                .build();

        Map<String,Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("faid", String.valueOf(faid));

        RetrofitHelper.getApi()
                .deleteFlyingArea(AssociationData.getUserToken(),
                        mRequestBody,timestamp,CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectApiResponse -> {
                    if (objectApiResponse.getErrorCode() == 0){
                        new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("温馨提示")
                                .setContentText("删除成功")
                                .setConfirmText("好的")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                })
                                .show();
                    }else {
                        new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("温馨提示")
                                .setContentText(objectApiResponse.getMsg())
                                .setConfirmText("好的")
                                .show();
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时，网络不太稳定");
                    }else if (throwable instanceof ConnectException)
                    {
                        CommonUitls.showToast(this,"连接失败，请您检查一下网络");
                    }else if (throwable instanceof RuntimeException){
                        CommonUitls.showToast(this,"连接出错，请联系管理员"+throwable.getMessage());
                    }
                });
    }
}
