package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.ui.Util;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.SensorEventHelper;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CarServiceFragment extends BaseFragment implements LocationSource, AMapLocationListener, TraceListener {
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
    private PolylineOptions mPolyoptions, tracePolytion;
    private Polyline mpolyline;

    private Queue<AMapLocation> aMapLocationQueue;
    private AMapLocation aMapLocation;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private Marker mLocMarker;
    private long mStartTime;//开始时间
    private long mEndTime;//结束时间
    private AMap mAMap;
    private OnLocationChangedListener mListener;

    private Circle mCircle;
    private SensorEventHelper mSensorHelper;
    private PathRecord mPathRecord;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private TraceOverlay mTraceoverlay;
    private List<TraceLocation> mTracelocationlist = new ArrayList<TraceLocation>();
    private List<TraceOverlay> mOverlayList = new ArrayList<TraceOverlay>();
    private LatLng mLocation;
    private int mDistance = 0;
    private int tracesize = 30;
    private GeYunTong geYunTong;
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
        init();
        initpolyline();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
    }

    private void init() {
        Logger.e("当前状态为:" + geYunTong.getStateCode());
        switch (geYunTong.getStateCode()) {
            case 0:
                mToggleButton.setChecked(false);
                break;
            case 1:
                mToggleButton.setChecked(true);
                runMap();
                break;
            case 2:
                Logger.e("比赛已经结束了的");
                break;
        }


    }

    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.GRAY);
        tracePolytion = new PolylineOptions();
        tracePolytion.width(40);
        tracePolytion.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        startLocation();
    }

    private void startLocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(2000);

            mLocationClient.setLocationOption(mLocationOption);

            mLocationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * 当定位发送改变会调用该方法
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                mLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(mLocation, aMapLocation.getAccuracy());//添加定位精度圆
                    addMarker(mLocation);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 18));
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLocation));
                    if (mToggleButton.isChecked()) {
                        mPathRecord.addpoint(aMapLocation);
                        mPolyoptions.add(mLocation);
                        mTracelocationlist.add(Util.parseTraceLocation(aMapLocation));
                        redrawline();
                        if (mTracelocationlist.size() > tracesize - 1) {
                            trace();
                        }
                    }
                } else {
                    mCircle.setCenter(mLocation);
                    mCircle.setRadius(aMapLocation.getAccuracy());
                    mLocMarker.setPosition(mLocation);
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLocation));
                }
                tvSpeed.setText(aMapLocation.getSpeed() * 3.6 + "KM");
                tvSpeed.setText(aMapLocation.getSpeed() * 3.6 + "KM");
            } else {
                CommonUitls.showToast(getActivity(), "定位失败");
            }
        }
    }

    /**
     * 画线
     */
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = mAMap.addPolyline(mPolyoptions);
            }
        }
    }

    /**
     * 添加圆形
     *
     * @param latlng
     * @param radius
     */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    /**
     * 添加汽车图标
     *
     * @param latlng
     */
    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.car)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
    }

    /**
     * 给服务器发送一条消息
     */
    public void sendMsg(String msg) {
        IoBuffer buffer = IoBuffer.allocate(100000);
        buffer.put(("[len=" + msg.length() + "]" + msg).getBytes());
        SessionManager.getInstance().writeToServer(buffer);
    }

    /**
     * 获取耗时时间
     *
     * @return
     */
    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    /**
     * 获取空距
     *
     * @param list
     * @return
     */
    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    /**
     * 获取平均速度
     *
     * @param distance
     * @return
     */
    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
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
                        mStartTime = System.currentTimeMillis();
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
                        mEndTime = System.currentTimeMillis();
                        stopRaceMonitor();
                    })
                    .setCancelText("取消")
                    .setCancelClickListener(sweetAlertDialog -> {
                        mToggleButton.setChecked(true);
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .show();
        }

    }


    private void runMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(getActivity());
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        mTraceoverlay = new TraceOverlay(mAMap);
        if (mPathRecord != null) {
            mPathRecord = null;
        }
        mPathRecord = new PathRecord();
        mPathRecord.setDate(getcueDate(mStartTime));
        getActivity().startService(new Intent(getActivity(), CoreService.class));

    }

    private void startRaceMonitor() {
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("type", "xiehui")
                .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                .build();

        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("type", "xiehui");
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
                        runMap();
                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误提示")
                                .setContentText(objectApiResponse.getMsg())
                                .setConfirmText("好的")
                                .show();
                        mToggleButton.setChecked(false);
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

    private void stopCarService() {
        mOverlayList.add(mTraceoverlay);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.queryProcessedTrace(2, Util.parseTraceLocationList(mPathRecord.getPathline()), LBSTraceClient.TYPE_AMAP, this);
        getActivity().stopService(new Intent(getActivity(), CoreService.class));
    }

    private void stopRaceMonitor() {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("温馨提示")
                .setContentText("是否将当前时间设为司放时间？")
                .setConfirmText("确认")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    ift = 1;
                    RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                            .addFormDataPart("type", "xiehui")
                            .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                            .addFormDataPart("ift", String.valueOf(ift))
                            .build();

                    Map<String, Object> postParams = new HashMap<String, Object>();
                    postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                    postParams.put("type", "xiehui");
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
                                    stopCarService();
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
                })
                .setCancelText("取消")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    ift = 0;
                    RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                            .addFormDataPart("type", "xiehui")
                            .addFormDataPart("rid", String.valueOf(geYunTong.getId()))
                            .addFormDataPart("ift", String.valueOf(ift))
                            .build();

                    Map<String, Object> postParams = new HashMap<String, Object>();
                    postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                    postParams.put("type", "xiehui");
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
                                    stopCarService();
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
                })
                .show();


    }

    @Override
    public void onRequestFailed(int i, String s) {
        mOverlayList.add(mTraceoverlay);
        mTraceoverlay = new TraceOverlay(mAMap);
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
                if (mLocMarker == null) {
                    mLocMarker = mAMap.addMarker(new MarkerOptions().position(linepoints.get(linepoints.size() - 1))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.point))
                            .title("距离：" + mDistance + "米"));
                    mLocMarker.showInfoWindow();
                } else {
                    mLocMarker.setTitle("距离：" + mDistance + "米");
                    mLocMarker.setPosition(linepoints.get(linepoints.size() - 1));
                    mLocMarker.showInfoWindow();
                }
            }
        } else if (lineID == 2) {
            if (linepoints != null && linepoints.size() > 0) {
                mAMap.addPolyline(new PolylineOptions()
                        .color(Color.RED)
                        .width(40).addAll(linepoints));
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


    @Override
    public void onPause() {
        super.onPause();
        Logger.e("onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("onPause");
    }
}
