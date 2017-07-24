package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.MainActivity;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.ApiConstants;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.flyarea.activity.FlyingAreaActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.order.activity.ReChargeActivity;
import com.cpigeon.cpigeonhelper.modular.order.activity.VipLevelUpActivity;
import com.cpigeon.cpigeonhelper.modular.root.activity.RootListActivity;
import com.cpigeon.cpigeonhelper.modular.root.activity.RootManagerActivity;
import com.cpigeon.cpigeonhelper.modular.setting.activity.WebViewActivity;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.DateUtils;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.cpigeon.cpigeonhelper.modular.setting.activity.WebViewActivity.EXTRA_URL;

/**
 *
 * Created by Administrator on 2017/7/6.
 *
 */

public class MyGYTActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_totaldistance)
    TextView tvTotaldistance;
    @BindView(R.id.ll_enter_geyuntong)
    LinearLayout llEnterGeyuntong;
    @BindView(R.id.ll_enter_flyarea)
    LinearLayout llEnterFlyarea;
    @BindView(R.id.ll_enter_root)
    LinearLayout llEnterRoot;
    @BindView(R.id.ll_enter_vip)
    LinearLayout llEnterVip;
    @BindView(R.id.ll_enter_help)
    LinearLayout llEnterHelp;
    @BindView(R.id.tv_top_speed)
    TextView tvTopSpeed;
    @BindView(R.id.tv_avarge_speed)
    TextView tvAvargeSpeed;
    @BindView(R.id.tv_totalrace)
    TextView tvTotalrace;
    @BindView(R.id.tv_using_time)
    TextView tvUsingTime;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_user_state)
    TextView tvState;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_mygyt;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的鸽运通");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("续费", this::xuFei);
        loadGYTStatisticalData();
        if (RealmUtils.getInstance().existGYTInfo()) {
            GYTService gytService = RealmUtils.getInstance().queryGTYInfo().get(0);
            tvState.setText(TextUtils.isEmpty(gytService.getGrade()) ? "当前用户等级：普通用户" : "当前用户等级：" + gytService.getGrade());
            tvEndTime.setText(gytService.getExpireTime());
            tvStartTime.setText(gytService.getOpenTime());
        } else {
            loadGTYServer();

        }

    }

    private void loadGYTStatisticalData() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("type", "xiehui");

        RetrofitHelper.getApi()
                .getGYTStatisticalData(AssociationData.getUserToken(),
                        urlParams)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(apiResponse -> {
                    if (apiResponse.getErrorCode() == 0 && apiResponse.getData() != null) {
                        tvTopSpeed.setText(apiResponse.getData().getMaxSpeed() + "Km/H");
                        tvAvargeSpeed.setText(CommonUitls.doubleformat(apiResponse.getData().getAvgSpeed() * 3.6,2) + "Km/H");
                        tvTotalrace.setText(apiResponse.getData().getMonitorRaceCount() + "场");
                        int hour = apiResponse.getData().getTotalSeconds() / 3600;
                        int days = hour / 24;
                        hour %= 24;
                        tvUsingTime.setText((days>0?days+"天":"")+hour+"小时");
                        tvTotaldistance.setText(CommonUitls.doubleformat(apiResponse.getData().getTotalMileage()*0.001,2)+"KM");
                    }
                }, throwable -> {

                });
    }

    private void xuFei() {
        startActivity(new Intent(this, ReChargeActivity.class));
    }

    private void loadGTYServer() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("type", AssociationData.getUserType());
        urlParams.put("atype", AssociationData.getUserAType());
        RetrofitHelper.getApi()
                .getGYTInfo(AssociationData.getUserToken(), urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gytServiceApiResponse -> {
                    if (gytServiceApiResponse.getErrorCode() == 0 && gytServiceApiResponse.getData() != null) {
                        tvState.setText(TextUtils.isEmpty(gytServiceApiResponse.getData().getGrade()) ? "当前用户等级：普通用户" : "当前用户等级：" + gytServiceApiResponse.getData().getGrade());
                        tvEndTime.setText(gytServiceApiResponse.getData().getExpireTime());
                        tvStartTime.setText(gytServiceApiResponse.getData().getOpenTime());
                        if (RealmUtils.getInstance().existGYTInfo()) {
                            RealmUtils.getInstance().deleteGYTInfo();
                            RealmUtils.getInstance().insertGYTService(gytServiceApiResponse.getData());
                        } else {
                            RealmUtils.getInstance().insertGYTService(gytServiceApiResponse.getData());
                        }

                    } else {
                        CommonUitls.showToast(this, "您暂未开通鸽运通");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时，网络不太好");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "连接异常，网络不通畅");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误" + throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.ll_enter_geyuntong, R.id.ll_enter_flyarea, R.id.ll_enter_root, R.id.ll_enter_vip, R.id.ll_enter_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_enter_geyuntong:
                startActivity(new Intent(this, GeYunTongListActivity.class));
                break;
            case R.id.ll_enter_flyarea:
                startActivity(new Intent(this, FlyingAreaActivity.class));
                break;
            case R.id.ll_enter_root:
                startActivity(new Intent(this, RootListActivity.class));
                break;
            case R.id.ll_enter_vip:
                startActivity(new Intent(this, VipLevelUpActivity.class));
                break;
            case R.id.ll_enter_help:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(EXTRA_URL, ApiConstants.BASE_URL+"APP/Help?type=help&appType=cpigeonhelper");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGTYServer();
    }
}
