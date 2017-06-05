package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.mina.Event;
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.mina.domain.MinaMsgHead;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.cpigeon.cpigeonhelper.utils.SensorEventHelper;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CarServiceFragment extends BaseFragment implements LocationSource, AMapLocationListener, AMap.OnMapLoadedListener, AMap.OnMapClickListener, WeatherSearch.OnWeatherSearchListener {


    @BindView(R.id.mapView)
    TextureMapView mapView;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_using_time)
    TextView tvUsingTime;
    @BindView(R.id.tv_sifang_location)
    TextView tvSifangLocation;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.tv_now_location)
    TextView tvNowLocation;
    @BindView(R.id.tv_now_weather)
    TextView tvNowWeather;
    @BindView(R.id.tv_airdistance)
    TextView tvAirdistance;
    @BindView(R.id.tv_mileage)
    TextView tvMileage;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.fab_take_photo)
    FloatingActionButton fabTakePhoto;
    @BindView(R.id.button)
    Button button;
    private AMap aMap;
    private boolean first = true;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocMarker;
    private Circle mCircle;
    private boolean mFirstFix = false;
    private SensorEventHelper mSensorHelper;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private Circle ac;
    private Circle c;
    private long start;
    private final Interpolator interpolator1 = new LinearInterpolator();
    private TimerTask mTimerTask;
    private Timer mTimer = new Timer();
    private WeatherSearchQuery mQuery;
    private WeatherSearch mWeatherSearch;
    private LocalWeatherLive oldlocalWeatherLive;
    private LocalWeatherLive nowlocalWeatherLive;
    private StringBuilder stringBuilder;
    private Queue<AMapLocation> locationQueue;
    private long startTimeStamp;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_service;
    }

    @Override
    public void finishCreateView(Bundle state) {
        mapView.onCreate(state);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(getActivity());
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    private void setUpMap() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @OnClick({R.id.fab_take_photo, R.id.button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_take_photo:
                getActivity().stopService(new Intent(getActivity(), CoreService.class));
                break;
            case R.id.button:
                startTimeStamp = System.currentTimeMillis() / 1000;
                getActivity().startService(new Intent(getActivity(), CoreService.class));
                break;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case TcpManager.STATE_FROM_SERVER_OK:
                    String result = (String) msg.obj;
                    Logger.e("result" + result);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public static CarServiceFragment newInstance() {

        return new CarServiceFragment();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置为单次定位
            mLocationOption.setInterval(10000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private Marker addMarker(LatLng point) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.car);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        Marker marker = aMap.addMarker(new MarkerOptions().position(point).icon(des)
                .anchor(0.5f, 0.5f));
        return marker;
    }

    private void addMarkerNoReturn(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.car)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("Andy");
    }

    private void addLocationMarker(AMapLocation aMapLocation) {
        LatLng mylocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        float accuracy = aMapLocation.getAccuracy();
        if (mLocMarker == null) {
            mLocMarker = addMarker(mylocation);
            ac = aMap.addCircle(new CircleOptions().center(mylocation)
                    .fillColor(Color.argb(100, 255, 218, 185)).radius(accuracy)
                    .strokeColor(Color.argb(255, 255, 228, 185)).strokeWidth(5));
            c = aMap.addCircle(new CircleOptions().center(mylocation)
                    .fillColor(Color.argb(70, 255, 218, 185))
                    .radius(accuracy).strokeColor(Color.argb(255, 255, 228, 185))
                    .strokeWidth(0));
        } else {
            mLocMarker.setPosition(mylocation);
            ac.setCenter(mylocation);
            ac.setRadius(accuracy);
            c.setCenter(mylocation);
            c.setRadius(accuracy);
        }
        Scalecircle(c);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                tvNowLocation.setText("当前坐标 : " + amapLocation.getLatitude() + "/" + amapLocation.getLongitude());

                if (locationQueue ==null) locationQueue = new ConcurrentLinkedQueue<>();
                locationQueue.offer(amapLocation);


                mQuery = new WeatherSearchQuery(amapLocation.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
                mWeatherSearch = new WeatherSearch(getActivity());
                mWeatherSearch.setOnWeatherSearchListener(this);
                mWeatherSearch.setQuery(mQuery);
                mWeatherSearch.searchWeatherAsyn(); //异步搜索



                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
//                    addMarkerNoReturn(location);//添加定位图标
                    mLocMarker = addMarker(location);
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                    addLocationMarker(amapLocation);
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(location));
                    addLocationMarker(amapLocation);
                }


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Logger.e(errText);
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void Scalecircle(final Circle circle) {
        start = SystemClock.uptimeMillis();
        mTimerTask = new circleTask(circle, 1000);
        mTimer.schedule(mTimerTask, 0, 30);
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public synchronized void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        AMapLocation location = locationQueue.poll();
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                this.nowlocalWeatherLive = weatherLiveResult.getLiveResult();
            } else {
                Logger.e("请求失败了");
            }
        } else {
            Logger.e("天气找不到");
        }
        if (stringBuilder == null) stringBuilder = new StringBuilder();
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(location.getLatitude());
        stringBuilder.append("|");
        stringBuilder.append(location.getLongitude());
        stringBuilder.append("|");
        stringBuilder.append(location.getSpeed()*3.6);
        stringBuilder.append("|");
        stringBuilder.append(location.getTime() / 1000);
        if (oldlocalWeatherLive==null || !oldlocalWeatherLive.getCity().equals(weatherLiveResult.getLiveResult().getCity()) ||
                !oldlocalWeatherLive.getReportTime().equals(weatherLiveResult.getLiveResult().getReportTime()))
        {
            stringBuilder.append("|");
            if (weatherLiveResult!=null)
            {
                stringBuilder.append(weatherLiveResult.getLiveResult().getWeather());
                stringBuilder.append("|");
                stringBuilder.append(weatherLiveResult.getLiveResult().getWindDirection());
                stringBuilder.append("|");
                stringBuilder.append(weatherLiveResult.getLiveResult().getWindPower());
                stringBuilder.append("|");
                stringBuilder.append(weatherLiveResult.getLiveResult().getReportTime());
                stringBuilder.append("|");
                stringBuilder.append(weatherLiveResult.getLiveResult().getTemperature());
            }

            Logger.e("位置发生了改变");
        }else {
            stringBuilder.append("|");
            stringBuilder.append(oldlocalWeatherLive.getWeather());
            stringBuilder.append("|");
            stringBuilder.append(oldlocalWeatherLive.getWindDirection());
            stringBuilder.append("|");
            stringBuilder.append(oldlocalWeatherLive.getWindPower());
            stringBuilder.append("|");
            stringBuilder.append(oldlocalWeatherLive.getReportTime());
            stringBuilder.append("|");
            stringBuilder.append(oldlocalWeatherLive.getTemperature());
            Logger.e("位置无任何改变:"+oldlocalWeatherLive.getCity()+"---"+oldlocalWeatherLive.getProvince());
        }

        sendMsg(stringBuilder.toString());
        oldlocalWeatherLive = nowlocalWeatherLive;

    }

    /**
     * 给服务器发送一条消息
     */
    public void sendMsg(String msg){
        /**
         * 假定消息格式为：消息头（一个short类型：表示事件号、一个int类型：表示消息体的长度）+消息体
         */
//        MinaMsgHead msgHead = new MinaMsgHead();
//        msgHead.event = Event.EV_C_S_TEST;
//        msgHead.bodyLen = msg.length();//因为消息体是空的所以填0，根据消息体的长度而变

        //创建一个缓冲，缓冲大小为:消息头长度(6位)+消息体长度
        IoBuffer buffer = IoBuffer.allocate(100000);
        buffer.put(("[len="+msg.length()+"]"+msg).getBytes());
        SessionManager.getInstance().writeToServer(buffer);
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }


    private class circleTask extends TimerTask {
        private double r;
        private Circle circle;
        private long duration = 1000;

        public circleTask(Circle circle, long rate) {
            this.circle = circle;
            this.r = circle.getRadius();
            if (rate > 0) {
                this.duration = rate;
            }
        }

        @Override
        public void run() {
            try {
                long elapsed = SystemClock.uptimeMillis() - start;
                float input = (float) elapsed / duration;
//                外圈循环缩放
//                float t = interpolator.getInterpolation((float)(input-0.25));//return (float)(Math.sin(2 * mCycles * Math.PI * input))
//                double r1 = (t + 2) * r;
//                外圈放大后消失
                float t = interpolator1.getInterpolation(input);
                double r1 = (t + 1) * r;
                circle.setRadius(r1);
                if (input > 2) {
                    start = SystemClock.uptimeMillis();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


}
