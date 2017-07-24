package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.mina.CoreService;
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.MyLocation;

import com.cpigeon.cpigeonhelper.ui.ViewExpandAnimation;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.cpigeon.cpigeonhelper.utils.Util;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;


/**
 *
 * Created by Administrator on 2017/5/31.
 *
 */

public class CarServiceFragment extends BaseFragment implements LocationSource, AMapLocationListener, WeatherSearch.OnWeatherSearchListener, TraceListener {
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
    @BindView(R.id.tv_race_status)
    TextView tvRaceStatus;
    @BindView(R.id.iv_expand_details)
    AppCompatImageView ivExpandDetails;
    @BindView(R.id.cardView_details_expand)
    CardView cardViewDetailsExpand;
    @BindView(R.id.cardView_details)
    CardView cardViewDetails;
    @BindView(R.id.cardView_start_race)
    CardView cardViewStartRace;
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
    private int mDistance = 0;
    private AMap aMap;
    private int ift;
    private AMapLocation aMapLocation;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private int tracesize = 30;
    public static CarServiceFragment newInstance() {

        return new CarServiceFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_service;
    }

    @Override
    public void finishCreateView(Bundle state) {
        cardViewDetails.setVisibility(View.GONE);
        mMapView.onCreate(state);
        if (!TextUtils.isEmpty(geYunTong.getFlyingArea()))
        {
            tvSifangLocation.setText(geYunTong.getLatitude()+"/"+geYunTong.getLongitude());
        }
        switch (geYunTong.getStateCode()) {
            case 0:
                tvRaceStatus.setText("开启监控");//表示还未开启监控
                break;
            case 1:
                tvRaceStatus.setText("结束监控");//表示已经开始监控了
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
    }

    private void initMap() {
        getActivity().startService(new Intent(getActivity(), CoreService.class));
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
        aMap.clear(true);
        mStartTime = System.currentTimeMillis();
        mTraceoverlay = new TraceOverlay(aMap);
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
    }

    /**
     * 最后获取总距离
     *
     * @return
     */
    private int getTotalDistance() {
        int distance = 0;
        for (TraceOverlay to : mOverlayList) {
            distance = distance + to.getDistance();
        }
        return distance;
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
                        tvRaceStatus.setText("开始监控");
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
        mEndTime = System.currentTimeMillis();
        mOverlayList.add(mTraceoverlay);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        Logger.e(
                decimalFormat.format(getTotalDistance() / 1000d) + "KM");
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
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

    /**
     * 结束监控
     */
    public void stopRace() {
        getActivity().stopService(new Intent(getActivity(), CoreService.class));
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
                        getActivity().finish();
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

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    /**
     * 实时轨迹画线
     */
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
                tvMileage.setText(mDistance*0.001+"");
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

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int code) {
        if (code == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {

                weatherlive = localWeatherLiveResult.getLiveResult();
                tvNowWeather.setText( weatherlive.getWeather());
                sendMsg(aMapLocation.getLatitude() + "|" + aMapLocation.getLongitude() + "|" +
                        aMapLocation.getSpeed()*3.6 + "|" + System.currentTimeMillis() / 1000 + "|" +
                        weatherlive.getWeather() + "|" + weatherlive.getWindDirection() + "|" +
                        weatherlive.getWindPower() + "|" + weatherlive.getReportTime() + "|" +
                        weatherlive.getTemperature());
            }
        }
    }


    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    /**
     * 给服务器发送一条消息
     */
    public void sendMsg(String msg) {
        String s = EncryptionTool.encryptAES(AssociationData.getUserToken(), KEY_SERVER_PWD);
        String sign = EncryptionHeader(s) + s;
        String content = EncryptionContent(msg) + msg;
        IoBuffer buffer = IoBuffer.allocate(100000);
        buffer.put(sign.getBytes());
        buffer.put(content.getBytes());
        SessionManager.getInstance().writeToServer(buffer);
    }

    public String EncryptionHeader(String msg) {
        return "[len=" + msg.length() + "&typ=1&sign=" + EncryptionTool.MD5(
                "len=" + msg.length() + "&typ=1&" + msg + "&soiDuo3inKjSdi") + "]";
    }

    public String EncryptionContent(String msg) {
        return "[len=" + msg.length() + "&typ=2&sign=" + EncryptionTool.MD5(
                "len=" + msg.length() + "&typ=2&" + msg + "&soiDuo3inKjSdi") + "]";
    }

    @OnClick({R.id.cardView_start_race, R.id.iv_expand_details, R.id.cardView_details_expand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cardView_start_race:
                if (tvRaceStatus.getText().toString().trim().equals("开启监控")) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("温馨提示")
                            .setContentText("确认立即开启鸽车监控？")
                            .setConfirmText("确认")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                tvRaceStatus.setText("结束监控");
                                startRaceMonitor();
                            })
                            .setCancelText("取消")
                            .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("警告")
                            .setContentText("是否退出鸽车监控，退出之后无法再开启")
                            .setConfirmText("确认")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                tvRaceStatus.setText("开始监控");
                                stopRaceMonitor();
                            })
                            .setCancelText("取消")
                            .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                }
                break;
            case R.id.iv_expand_details:
                expandinfo();
                break;
            case R.id.cardView_details_expand:
                expandinfo();
                break;
        }
    }

    /**
     * 展开详细信息
     */
    public void expandinfo() {
        if (cardViewDetails.getVisibility() == View.VISIBLE) {
            cardViewDetails.setVisibility(View.GONE);
            ivExpandDetails.setRotation(0);
        } else {
            ivExpandDetails.setRotation(180);
            cardViewDetails.setVisibility(View.VISIBLE);
        }
        ViewExpandAnimation expandAnimation = new ViewExpandAnimation(cardViewDetails);
        cardViewDetails.startAnimation(expandAnimation);
    }

    /**
     * 启动定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(4000);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();

        }
    }

    /**
     * 定位被取消
     */
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
     * 定位之后的回调
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                LatLng mylocation = new LatLng(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(mylocation));
                if (tvRaceStatus.getText().toString().trim().equals("结束监控")) {
                    mPolyoptions.add(mylocation);
                    mTracelocationlist.add(Util.parseTraceLocation(aMapLocation));
                    redrawline();
                    if (mTracelocationlist.size() > tracesize - 1) {
                        trace();
                    }
                    mquery = new WeatherSearchQuery(aMapLocation.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
                    mweathersearch = new WeatherSearch(getActivity());
                    mweathersearch.setOnWeatherSearchListener(this);
                    mweathersearch.setQuery(mquery);
                    mweathersearch.searchWeatherAsyn(); //异步搜索
                }
                tvSpeed.setText(CommonUitls.doubleformat(aMapLocation.getSpeed() *3.6,2)+"Km/H");
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

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

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }
}
