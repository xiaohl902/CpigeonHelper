package com.cpigeon.cpigeonhelper.modular.flyarea.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/14.
 */

public class AddFlyingAreaActivity extends ToolbarBaseActivity {
    @BindView(R.id.et_flyingarea_place)
    EditText etFlyingareaPlace;
    @BindView(R.id.et_flyingarea_la)
    EditText etFlyingareaLa;
    @BindView(R.id.et_flyingarea_lo)
    EditText etFlyingareaLo;
    @BindView(R.id.et_flyingarea_alias)
    EditText etFlyingareaAlias;
    private long timestamp;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_flyingarea_add;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("添加司放地");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("完成", this::save);
    }

    public void save() {
        if ("".equals(etFlyingareaLa.getText().toString().trim()) ||
                "".equals(etFlyingareaLo.getText().toString().trim()) ||
                "".equals(etFlyingareaPlace.getText().toString().trim()) ||
                "".equals(etFlyingareaAlias.getText().toString().trim())) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误")
                    .setContentText("添加失败")
                    .setConfirmText("好的")
                    .show();

        } else if (!CommonUitls.isAjLocation(Double.parseDouble(etFlyingareaLa.getText().toString())) ||
                !CommonUitls.isAjLocation(Double.parseDouble(etFlyingareaLo.getText().toString()))) {

            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误")
                    .setContentText("坐标不正确")
                    .setConfirmText("好的")
                    .show();
        } else {

            RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("type", AssociationData.getUserType())
                    .addFormDataPart("alias", etFlyingareaAlias.getText().toString().trim())
                    .addFormDataPart("area", etFlyingareaPlace.getText().toString().trim())
                    .addFormDataPart("lo", etFlyingareaLo.getText().toString().trim())
                    .addFormDataPart("la", etFlyingareaLa.getText().toString().trim())
                    .build();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("type", AssociationData.getUserType());
            postParams.put("alias", etFlyingareaAlias.getText().toString().trim());
            postParams.put("area", etFlyingareaPlace.getText().toString().trim());
            postParams.put("lo", etFlyingareaLo.getText().toString().trim());
            postParams.put("la", etFlyingareaLa.getText().toString().trim());

            timestamp = System.currentTimeMillis() / 1000;

            RetrofitHelper.getApi()
                    .createFlyingArea(AssociationData.getUserToken(), mRequestBody, timestamp,
                            CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(flyingAreaApiResponse -> {
                        if (flyingAreaApiResponse.getErrorCode() == 0) {
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("成功")
                                    .setContentText("添加司放地成功")
                                    .setConfirmText("好的")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        AddFlyingAreaActivity.this.finish();
                                    })
                                    .show();

                        } else {
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("错误")
                                    .setContentText(flyingAreaApiResponse.getMsg())
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
}
