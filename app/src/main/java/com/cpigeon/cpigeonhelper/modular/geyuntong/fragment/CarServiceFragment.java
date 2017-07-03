package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.LocationInfoReports;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.MyLocation;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.ui.Util;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmAsyncTask;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CarServiceFragment extends BaseFragment implements AMap.OnMyLocationChangeListener, TraceListener {
    @BindView(R.id.mapView)
    TextureMapView mMapView;
    @BindView(R.id.tvMileage)
    TextView tvMileage;
    @BindView(R.id.tv_using_time)
    TextView tvUsingTime;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvAirdistance)
    TextView tvAirdistance;
    @BindView(R.id.tv_sifang_location)
    TextView tvSifangLocation;
    @BindView(R.id.tvWeather)
    TextView tvWeather;
    @BindView(R.id.tv_now_weather)
    TextView tvNowWeather;
    @BindView(R.id.btn_stop)
    ToggleButton mToggleButton;
    ///////////////////////////////////////////////////////////////////////////
    // 高德地图相关
    ///////////////////////////////////////////////////////////////////////////
    private GeYunTong geYunTong;
    private PolylineOptions mPolyoptions;
    private Polyline mpolyline;
    private long mStartTime;
    private long mEndTime;
    private TraceOverlay mTraceoverlay;
    private List<TraceLocation> mTracelocationlist = new ArrayList<>();
    private List<TraceOverlay> mOverlayList = new ArrayList<>();
    private List<AMapLocation> recordList = new ArrayList<>();
    private PathRecord record;
    private int mDistance = 0;
    private int i = 1;
    private AMap aMap;
    private MyLocation mMyLocation = new MyLocation();
    private int ift;
    public static CarServiceFragment newInstance() {

        return new CarServiceFragment();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_service;
    }

    @Override
    public void finishCreateView(Bundle state) {
        mMapView.onCreate(state);
        switch (geYunTong.getStateCode()) {
            case 0:
                mToggleButton.setChecked(false);//表示还未开启监控
                break;
            case 1:
                mToggleButton.setChecked(true);//表示已经开始监控了
                initMap();
                initpolyline();
                break;
            case 2:
                Logger.e("比赛已经结束了的");
                break;
        }
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.GRAY);
        PolylineOptions tracePolytion = new PolylineOptions();
        tracePolytion.width(40);
        tracePolytion.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
        if (mToggleButton.isChecked()) {
            aMap.clear(true);
            if (record != null) {
                record = null;
            }
            record = new PathRecord();
            mStartTime = System.currentTimeMillis();
            record.setmDate(getcueDate(mStartTime));
        } else {
            mMapView.onDestroy();
        }
        mTraceoverlay = new TraceOverlay(aMap);
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔,单位为毫秒。
        myLocationStyle.strokeColor(R.color.colorAccent);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(R.color.colorPrimary);//设置定位蓝点精度圆圈的边框颜色的方法。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
    }

    /**
     * 给服务器发送一条消息
     */
    public void sendMsg(String msg) {
        String s = EncryptionTool.encryptAES(AssociationData.getUserToken(), KEY_SERVER_PWD);
        IoBuffer buffer = IoBuffer.allocate(100000);
        buffer.put(
                ("[len=" + s.length() + "]" + s)
                        .getBytes());
        SessionManager.getInstance().writeToServer(buffer);

    }

    @OnClick(R.id.btn_stop)
    public void onViewClicked() {
        if (mToggleButton.isChecked()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("确认立即开启鸽车监控？")
                    .setConfirmText("确认")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        mStartTime = System.currentTimeMillis() / 1000;
                        startRaceMonitor();
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        mToggleButton.setChecked(false);
                    })
                    .show();
        } else {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("警告")
                    .setContentText("是否退出鸽车监控，退出之后无法再开启")
                    .setConfirmText("确认")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        mEndTime = System.currentTimeMillis() / 1000;
                        stopRaceMonitor();
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        mToggleButton.setChecked(true);
                    })
                    .show();
        }

    }

    /**
     * 开始鸽车监控
     */
    private void startRaceMonitor() {
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("type", AssociationData.getUserType())
                .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                .build();

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("type", AssociationData.getUserType());
        postParams.put("rid", String.valueOf(geYunTong.getId()));

        RetrofitHelper
                .getApi()
                .startRaceMonitor(AssociationData.getUserToken()
                        , mRequestBody, mStartTime, CommonUitls.getApiSign(mStartTime, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectApiResponse -> {
                    if (objectApiResponse.getErrorCode() == 0) {
                        initMap();
                        initpolyline();
                    } else {
                        mToggleButton.setChecked(false);
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误提示")
                                .setContentText(objectApiResponse.getMsg())
                                .setConfirmText("好的")
                                .show();
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(getActivity(), "请求超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(getActivity(), "无法连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(getActivity(), "运行时异常");
                    }
                });
    }

    /**
     * 停止鸽车监控
     */
    private void stopRaceMonitor() {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("温馨提示")
                .setContentText("是否将当前时间设为司放时间？")
                .setConfirmText("确认")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    ift = 1;
                    stopRace();
                })
                .setCancelText("取消")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    ift = 0;
                    stopRace();
                })
                .show();


    }


    public void stopRace() {
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("type", AssociationData.getUserType())
                .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                .addFormDataPart("ift", String.valueOf(ift))
                .build();

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("type", AssociationData.getUserType());
        postParams.put("rid", String.valueOf(geYunTong.getId()));
        postParams.put("ift", String.valueOf(ift));

        RetrofitHelper.getApi()
                .stopRaceMonitor(AssociationData.getUserToken(),
                        mRequestBody, mEndTime, CommonUitls.getApiSign(mEndTime, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectApiResponse -> {
                    if (objectApiResponse.getErrorCode() == 0) {

                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("温馨提示")
                                .setContentText(objectApiResponse.getMsg())
                                .setConfirmText("确认")
                                .show();
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(getActivity(), "请求超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(getActivity(), "无法连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(getActivity(), "运行时异常");
                    }
                });
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {

            LatLng mylocation = new LatLng(location.getLatitude(),
                    location.getLongitude());

            mMyLocation.setId(i);
            mMyLocation.setRaceid(geYunTong.getId());
            mMyLocation.setLatitude(location.getLatitude());
            mMyLocation.setLongitude(location.getLongitude());
            RealmUtils.getInstance().insertLocation(mMyLocation);
            i++;
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            Log.e("szx", location.getLatitude() + "," + location.getLongitude());
            record.addpoint(location);
            mPolyoptions.add(mylocation);
            mTracelocationlist.add(Util.parseTraceLocation(location));
            redrawline();
            int tracesize = 30;
            if (mTracelocationlist.size() > tracesize - 1) {
                trace();
            }

        }

    }

    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = aMap.addPolyline(mPolyoptions);
            }
        }
    }

    private void trace() {
        List<TraceLocation> locationList = new ArrayList<>(mTracelocationlist);
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, this);
        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size() - 1);
        mTracelocationlist.clear();
        mTracelocationlist.add(lastlocation);
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    @Override
    public void onRequestFailed(int i, String s) {
        mOverlayList.add(mTraceoverlay);
        mTraceoverlay = new TraceOverlay(aMap);
    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {

    }

    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int i2) {
        if (lineID == 1) {
            if (linepoints != null && linepoints.size() > 0) {
                mTraceoverlay.add(linepoints);
                mDistance += distance;
                mTraceoverlay.setDistance(mTraceoverlay.getDistance() + distance);
            }
        } else if (lineID == 2) {
            if (linepoints != null && linepoints.size() > 0) {
                aMap.addPolyline(new PolylineOptions()
                        .color(Color.RED)
                        .width(40).addAll(linepoints));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

}
