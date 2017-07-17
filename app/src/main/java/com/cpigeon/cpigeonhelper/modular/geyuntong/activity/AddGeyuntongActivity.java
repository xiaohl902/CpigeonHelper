package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.flyarea.activity.SimpleFlyingAreaActivity;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 * Created by Administrator on 2017/6/7.
 *
 */

public class AddGeyuntongActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_geyuntong_name)
    EditText tvGeyuntongName;
    @BindView(R.id.tv_geyuntong_place)
    TextView tvGeyuntongPlace;
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;

    @BindView(R.id.btn_delete_race)
    Button btnDeleteRace;

    @BindView(R.id.ll_chose_flyingarea)
    LinearLayout llChoseFlyingarea;
    private long timestamp;
    private int faid = 0;
    private GeYunTong geYunTong;
    private SweetAlertDialog mSweetAlertDialog = null;

    public void setGeYunTong(GeYunTong geYunTong) {
        this.geYunTong = geYunTong;
    }

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
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            geYunTong = bundle.getParcelable("geyuntong");
            setGeYunTong(geYunTong);
            setTitle("修改比赛");
            setTopLeftButton(R.drawable.ic_back, this::finish);
            setTopRightButton("完成", this::edit);
            tvGeyuntongName.setText(geYunTong.getRaceName());
            tvGeyuntongPlace.setText(geYunTong.getFlyingArea());
            tvLongitude.setText(String.valueOf(geYunTong.getLongitude()));
            tvLatitude.setText (String.valueOf(geYunTong.getLatitude()));
            btnDeleteRace.setVisibility(View.VISIBLE);
        } else {
            setTitle("添加比赛");
            setTopLeftButton(R.drawable.ic_back, this::finish);
            setTopRightButton("完成", this::add);
            btnDeleteRace.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.btn_delete_race})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_delete_race:
                deleteRace();
                break;
        }
    }

    /**
     * 删除比赛
     */
    private void deleteRace() {
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                .addFormDataPart("type", AssociationData.getUserType())
                .build();

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("rid", String.valueOf(geYunTong.getId()));
        postParams.put("type", AssociationData.getUserType());

        RetrofitHelper.getApi().deleteGeYunTongRace(AssociationData.getUserToken()
                , mRequestBody
                , timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(geYunTongApiResponse -> {
                    if (geYunTongApiResponse.getErrorCode() == 0) {
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("删除成功")
                                .setConfirmText("知道了")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismiss();
                                    AppManager.getAppManager().killActivity(ACarServiceActivity.class);
                                    AddGeyuntongActivity.this.finish();
                                })
                                .show();
                    } else {
                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(geYunTongApiResponse.getMsg())
                                .setConfirmText("知道了")
                                .show();
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "删除失败了，请检查网络");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "删除失败了，请检查连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "删除失败了");
                    }
                });

    }

    /**
     * 添加比赛
     */
    private void add() {
        if (TextUtils.isEmpty(tvGeyuntongName.getText().toString().trim())) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("输入错误")
                    .setConfirmText("知道了")
                    .show();
        } else {
            if (mSweetAlertDialog == null) {
                mSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                mSweetAlertDialog.setTitleText("正在拼命加载..");
                mSweetAlertDialog.show();
            }
            timestamp = System.currentTimeMillis() / 1000;
            RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("type", AssociationData.getUserType())
                    .addFormDataPart("rname", tvGeyuntongName.getText().toString())
                    .addFormDataPart("farea", tvGeyuntongPlace.getText().toString())
                    .addFormDataPart("lo", tvLatitude.getText().toString())
                    .addFormDataPart("la", tvLongitude.getText().toString())
                    .addFormDataPart("faid", String.valueOf(faid))
                    .build();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("type", AssociationData.getUserType());
            postParams.put("rname", tvGeyuntongName.getText().toString());
            postParams.put("farea", tvGeyuntongPlace.getText().toString());
            postParams.put("lo", tvLatitude.getText().toString());
            postParams.put("la", tvLongitude.getText().toString());
            postParams.put("faid", String.valueOf(faid));

            RetrofitHelper.getApi().createGeYunTongRace(AssociationData.getUserToken()
                    , mRequestBody
                    , timestamp, CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(geYunTongApiResponse -> {
                        mSweetAlertDialog.dismissWithAnimation();
                        if (geYunTongApiResponse.getErrorCode() == 0) {
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("添加成功")
                                    .setConfirmText("知道了")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        AddGeyuntongActivity.this.finish();
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(geYunTongApiResponse.getMsg())
                                    .setConfirmText("知道了")
                                    .show();
                        }
                    }, throwable -> {
                        mSweetAlertDialog.dismissWithAnimation();
                        if (throwable instanceof SocketTimeoutException) {
                            CommonUitls.showToast(this, "添加失败，请检查网络");
                        } else if (throwable instanceof ConnectException) {
                            CommonUitls.showToast(this, "添加失败，请检查连接");
                        } else if (throwable instanceof RuntimeException) {
                            CommonUitls.showToast(this, "添加失败");
                        }
                    });

        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FlyingArea flyingArea){
        tvLatitude.setText(String.valueOf(flyingArea.getLatitude()));
        tvLongitude.setText(String.valueOf(flyingArea.getLongitude()));
        tvGeyuntongPlace.setText(String.valueOf(flyingArea.getArea()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 修改比赛
     */
    private void edit() {
        if (TextUtils.isEmpty(tvGeyuntongName.getText().toString().trim())) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("输入错误")
                    .setConfirmText("知道了")
                    .show();
        } else {
            timestamp = System.currentTimeMillis() / 1000;
            RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                    .addFormDataPart("type", AssociationData.getUserType())
                    .addFormDataPart("rname", tvGeyuntongName.getText().toString())
                    .addFormDataPart("farea", tvGeyuntongPlace.getText().toString())
                    .addFormDataPart("lo", tvLatitude.getText().toString())
                    .addFormDataPart("la", tvLongitude.getText().toString())
                    .addFormDataPart("faid", String.valueOf(faid))
                    .build();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("rid", String.valueOf(geYunTong.getId()));
            postParams.put("type", AssociationData.getUserType());
            postParams.put("rname", tvGeyuntongName.getText().toString());
            postParams.put("farea", tvGeyuntongPlace.getText().toString());
            postParams.put("lo", tvLatitude.getText().toString());
            postParams.put("la", tvLongitude.getText().toString());
            postParams.put("faid", String.valueOf(faid));

            RetrofitHelper.getApi().updateGeYunTongRace(AssociationData.getUserToken()
                    , mRequestBody
                    , timestamp, CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(geYunTongApiResponse -> {
                        if (geYunTongApiResponse.isStatus()) {
                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("温馨提示")
                                    .setContentText("修改信息成功")
                                    .setConfirmText("确认")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        AppManager.getAppManager().killActivity(ACarServiceActivity.class);
                                        finish();
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(geYunTongApiResponse.getMsg())
                                    .setConfirmText("知道了")
                                    .show();
                        }
                    }, throwable -> {
                        if (throwable instanceof SocketTimeoutException) {
                            CommonUitls.showToast(this, "修改失败，请检查网络");
                        } else if (throwable instanceof ConnectException) {
                            CommonUitls.showToast(this, "修改失败，请检查连接");
                        } else if (throwable instanceof RuntimeException) {
                            CommonUitls.showToast(this, "修改失败");
                        }
                    });

        }
    }


    @OnClick(R.id.ll_chose_flyingarea)
    public void onViewClicked() {
        startActivity(new Intent(this, SimpleFlyingAreaActivity.class));
    }
}
