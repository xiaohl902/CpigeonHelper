package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
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
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.ui.Util;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.SensorEventHelper;

import org.apache.mina.core.buffer.IoBuffer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(getActivity());
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        mTraceoverlay = new TraceOverlay(mAMap);
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
                tvSpeed.setText(aMapLocation.getSpeed()*3.6+"KM");
                tvSpeed.setText(aMapLocation.getSpeed()*3.6+"KM");
            } else {
                CommonUitls.showToast(getActivity(), "定位失败");
            }
        }
    }

    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = mAMap.addPolyline(mPolyoptions);
            }
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
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
            if (mPathRecord != null) {
                mPathRecord = null;
            }

            mPathRecord = new PathRecord();
            mStartTime = System.currentTimeMillis();
            mPathRecord.setDate(getcueDate(mStartTime));
            getActivity().startService(new Intent(getActivity(), CoreService.class));
            showUsingTime();

        } else {
            mEndTime = System.currentTimeMillis();
            mOverlayList.add(mTraceoverlay);
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
            mTraceClient.queryProcessedTrace(2, Util.parseTraceLocationList(mPathRecord.getPathline()) , LBSTraceClient.TYPE_AMAP,this);
            getActivity().stopService(new Intent(getActivity(), CoreService.class));
        }

    }

    private void showUsingTime() {

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
            if (linepoints != null && linepoints.size()>0) {
                mTraceoverlay.add(linepoints);
                mDistance += distance;
                mTraceoverlay.setDistance(mTraceoverlay.getDistance()+distance);
                if (mLocMarker == null) {
                    mLocMarker = mAMap.addMarker(new MarkerOptions().position(linepoints.get(linepoints.size() - 1))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.point))
                            .title("距离：" + mDistance+"米"));
                    mLocMarker.showInfoWindow();
                } else {
                    mLocMarker.setTitle("距离：" + mDistance+"米");
                    mLocMarker.setPosition(linepoints.get(linepoints.size() - 1));
                    mLocMarker.showInfoWindow();
                }
            }
        } else if (lineID == 2) {
            if (linepoints != null && linepoints.size()>0) {
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
        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size()-1);
        mTracelocationlist.clear();
        mTracelocationlist.add(lastlocation);
    }
}
