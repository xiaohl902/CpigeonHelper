package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
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
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.ui.Util;
import com.cpigeon.cpigeonhelper.ui.ViewExpandAnimation;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;


/**
 * Created by Administrator on 2017/5/31.
 */

public class CarServiceFragment extends BaseFragment implements AMap.OnMyLocationChangeListener, TraceListener, GeocodeSearch.OnGeocodeSearchListener, WeatherSearch.OnWeatherSearchListener {
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
    @BindView(R.id.iv_expand_details )
    AppCompatImageView ivExpandDetails;
    @BindView(R.id.cardView_details_expand)
    CardView cardViewDetailsExpand;
    @BindView(R.id.cardView_details)
    CardView cardViewDetails;
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
    private PathRecord record;
    private int mDistance = 0;
    private int i = 1;
    private AMap aMap;
    private int ift;
    private GeocodeSearch geocodeSearch;
    private LatLng mylocation;
    private MyLocation mMyLocation = new MyLocation();
    private Location location;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;

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
        mPolyoptions.width(40);
        mPolyoptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
    }

    private void initMap() {
        getActivity().startService(new Intent(getActivity(), CoreService.class));
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

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));

        mweathersearch = new WeatherSearch(getActivity());
        mweathersearch.setOnWeatherSearchListener(this);

        geocodeSearch = new GeocodeSearch(getActivity());
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
        Logger.e("鸽运通id：" + geYunTong.getId());
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
        if (mMapView != null) {
            mMapView.onDestroy();
            aMap.clear();
        }
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

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {
            this.location = location;
            mylocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 10, GeocodeSearch.AMAP);
            geocodeSearch.getFromLocationAsyn(query);
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

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
        if (code == 1000) {
            mquery = new WeatherSearchQuery(regeocodeResult.getRegeocodeAddress().getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int code) {
        if (code == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                weatherlive = localWeatherLiveResult.getLiveResult();
                mMyLocation.setId(i);
                i++;
                mMyLocation.setRaceid(geYunTong.getId());
                mMyLocation.setLatitude(location.getLatitude());
                mMyLocation.setLongitude(location.getLongitude());
                mMyLocation.setGetReportTime(weatherlive.getReportTime());
                mMyLocation.setHumidity(weatherlive.getHumidity());
                mMyLocation.setTemperature(weatherlive.getTemperature());
                mMyLocation.setWindDirection(weatherlive.getWindDirection());
                mMyLocation.setWeather(weatherlive.getWeather());
                Logger.e("天气" + weatherlive.getWeather());
                RealmUtils.getInstance().insertLocation(mMyLocation);
                Logger.e("code run there");
                sendMsg(location.getLatitude() + "|" + location.getLongitude() + "|" +
                        3.00 + "|" + System.currentTimeMillis() / 1000 + "|" +
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
        String content = EncryptionHeader(msg) + msg;
        IoBuffer buffer = IoBuffer.allocate(100000);
        buffer.put(sign.getBytes());
        buffer.put(content.getBytes());
        SessionManager.getInstance().writeToServer(buffer);

    }

    public String EncryptionHeader(String msg) {
        return "[len=" + msg.length() + "&typ=2&sign=" + EncryptionTool.MD5(
                "len=" + msg.length() + "&typ=2&" + msg + "&soiDuo3inKjSdi") + "]";
    }

    @OnClick({R.id.btn_stop, R.id.iv_expand_details,R.id.cardView_details_expand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_stop:
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
                break;
            case R.id.iv_expand_details:
                expandinfo();
                break;
            case R.id.cardView_details_expand:
                expandinfo();
                break;
        }
    }
    public void expandinfo()
    {
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
     * 获取行驶总距离
     * @return
     */
    private int getTotalDistance() {
        int distance = 0;
        for (TraceOverlay to : mOverlayList) {
            distance = distance + to.getDistance();
        }
        return distance;
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
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

}
