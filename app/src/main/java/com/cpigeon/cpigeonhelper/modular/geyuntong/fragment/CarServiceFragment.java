package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.utils.SensorEventHelper;
import com.orhanobut.logger.Logger;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CarServiceFragment extends BaseFragment implements LocationSource, AMapLocationListener {


    @BindView(R.id.mapView)
    MapView mapView;
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
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocMarker;
    private Circle mCircle;
    private boolean mFirstFix = false;
    private SensorEventHelper mSensorHelper;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

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
                break;
            case R.id.button:
                break;
        }
    }


    public static CarServiceFragment newInstance() {

        return new CarServiceFragment();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
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

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.car)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("andy");
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(location));
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Logger.e(errText);
            }
        }
    }
}
