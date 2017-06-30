package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.DbAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.LocationInfoReports;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.ui.Util;
import com.cpigeon.cpigeonhelper.utils.TraceRePlay;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/6/9.
 */

public class CarPlaybackFragment extends BaseFragment implements AMap.OnMapLoadedListener,TraceListener, View.OnClickListener {

    private final static int AMAP_LOADED = 2;
    @BindView(R.id.displaybtn)
    ToggleButton mDisplaybtn;
    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.mapView)
    TextureMapView mMapView;
    @BindView(R.id.record_show_activity_origin_radio_button)
    RadioButton mOriginRadioButton;
    @BindView(R.id.record_show_activity_grasp_radio_button)
    RadioButton mGraspRadioButton;
    @BindView(R.id.record_show_activity_trace_group)
    RadioGroup recordShowActivityTraceGroup;
    private DbAdapter mDataBaseHelper;
    private List<PathRecord> mAllRecord = new ArrayList<PathRecord>();
    private AMap mAMap;
    private Marker mOriginStartMarker, mOriginEndMarker, mOriginRoleMarker;
    private Marker mGraspStartMarker, mGraspEndMarker, mGraspRoleMarker;
    private Polyline mOriginPolyline, mGraspPolyline;
    private int mRecordItemId;
    private List<LatLng> mOriginLatLngList;
    private List<LatLng> mGraspLatLngList;
    private boolean mGraspChecked = false;
    private boolean mOriginChecked = true;
    private ExecutorService mThreadPool;
    private TraceRePlay mRePlay;
    private int id;

    public static CarPlaybackFragment newInstance() {
        return new CarPlaybackFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_playback;
    }

    @Override
    public void finishCreateView(Bundle state) {
        mMapView.onCreate(state);
        setUpMap();
        mOriginRadioButton.setOnClickListener(this);
        mGraspRadioButton.setOnClickListener(this);
    }

    private void setUpMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.setOnMapLoadedListener(this);
        }
    }


    private void startMove() {
        if(mRePlay !=null){
            mRePlay.stopTrace();
        }
        if (mOriginChecked) {
            mRePlay = rePlayTrace(mOriginLatLngList, mOriginRoleMarker);
        } else if (mGraspChecked) {
            mRePlay = rePlayTrace(mGraspLatLngList, mGraspRoleMarker);
        }
    }

    /**
     * 轨迹回放方法
     */
    private TraceRePlay rePlayTrace(List<LatLng> list, final Marker updateMarker) {
        TraceRePlay replay = new TraceRePlay(list, 100,
                new TraceRePlay.TraceRePlayListener() {

                    @Override
                    public void onTraceUpdating(LatLng latLng) {
                        if (updateMarker != null) {
                            updateMarker.setPosition(latLng); // 更新小人实现轨迹回放
                        }
                    }

                    @Override
                    public void onTraceUpdateFinish() {
                        mDisplaybtn.setChecked(false);
                        mDisplaybtn.setClickable(true);
                    }
                });
        mThreadPool.execute(replay);
        return replay;
    }

    /**
     * 将纠偏后轨迹小人设置到起点
     */
    private void resetGraspRole() {
        if (mGraspLatLngList == null) {
            return;
        }
        LatLng startLatLng = mGraspLatLngList.get(0);
        if (mGraspRoleMarker != null) {
            mGraspRoleMarker.setPosition(startLatLng);
        }
    }

    /**
     * 将原始轨迹小人设置到起点
     */
    private void resetOriginRole() {
        if (mOriginLatLngList == null) {
            return;
        }
        LatLng startLatLng = mOriginLatLngList.get(0);
        if (mOriginRoleMarker != null) {
            mOriginRoleMarker.setPosition(startLatLng);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AMAP_LOADED:
                    setupRecord();
                    break;
                default:
                    break;
            }
        }

    };

    public void onBackClick(View view) {
        getActivity().finish();
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }

    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();

    }

    /**
     * 轨迹数据初始化
     *
     */
    private void setupRecord() {
        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(
                getApplicationContext());
        DbAdapter dbhelper = new DbAdapter(this.getApplicationContext());
        dbhelper.open();
        PathRecord mRecord = dbhelper.queryRecordById(mRecordItemId);
        dbhelper.close();
        if (mRecord != null) {
            List<AMapLocation> recordList = mRecord.getPathline();
            AMapLocation startLoc = mRecord.getStartpoint();
            AMapLocation endLoc = mRecord.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(),
                    startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(),
                    endLoc.getLongitude());
            mOriginLatLngList = Util.parseLatLngList(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

            List<TraceLocation> mGraspTraceLocationList = Util
                    .parseTraceLocationList(recordList);
            // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
            mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList,
                    LBSTraceClient.TYPE_AMAP, this);
        } else {
        }

    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(
                Color.BLUE).addAll(originList));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.end)));

        try {
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(),
                    50));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mOriginRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.car))));
    }

    /**
     * 设置是否显示原始轨迹
     *
     * @param enable
     */
    private void setOriginEnable(boolean enable) {
        mDisplaybtn.setClickable(true);
        if (mOriginPolyline == null || mOriginStartMarker == null
                || mOriginEndMarker == null || mOriginRoleMarker == null) {
            return;
        }
        if (enable) {
            mOriginPolyline.setVisible(true);
            mOriginStartMarker.setVisible(true);
            mOriginEndMarker.setVisible(true);
            mOriginRoleMarker.setVisible(true);
        } else {
            mOriginPolyline.setVisible(false);
            mOriginStartMarker.setVisible(false);
            mOriginEndMarker.setVisible(false);
            mOriginRoleMarker.setVisible(false);
        }
    }

    /**
     * 地图上添加纠偏后轨迹线路及起终点、轨迹动画小人
     *
     */
    private void addGraspTrace(List<LatLng> graspList, boolean mGraspChecked) {

        if (graspList == null || graspList.size() < 2) {
            return;
        }

        LatLng startPoint = graspList.get(0);
        LatLng endPoint = graspList.get(graspList.size() - 1);

        mGraspPolyline = mAMap.addPolyline(new PolylineOptions()
                .setCustomTexture(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.grasp_trace_line))
                .width(40).addAll(graspList));

        mGraspStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));

        mGraspEndMarker = mAMap.addMarker(new MarkerOptions()
                .position(endPoint).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.end)));

        mGraspRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.car))));

        if (!mGraspChecked) {
            mGraspPolyline.setVisible(false);
            mGraspStartMarker.setVisible(false);
            mGraspEndMarker.setVisible(false);
            mGraspRoleMarker.setVisible(false);
        }
    }

    /**
     * 设置是否显示纠偏后轨迹
     *
     * @param enable
     */
    private void setGraspEnable(boolean enable) {
        mDisplaybtn.setClickable(true);
        if (mGraspPolyline == null || mGraspStartMarker == null
                || mGraspEndMarker == null || mGraspRoleMarker == null) {
            return;
        }
        if (enable) {
            mGraspPolyline.setVisible(true);
            mGraspStartMarker.setVisible(true);
            mGraspEndMarker.setVisible(true);
            mGraspRoleMarker.setVisible(true);
        } else {
            mGraspPolyline.setVisible(false);
            mGraspStartMarker.setVisible(false);
            mGraspEndMarker.setVisible(false);
            mGraspRoleMarker.setVisible(false);
        }
    }

    @Override
    public void onMapLoaded() {
        Message msg = handler.obtainMessage();
        msg.what = AMAP_LOADED;
        handler.sendMessage(msg);
    }

    /**
     * 轨迹纠偏完成数据回调
     */
    @Override
    public void onFinished(int arg0, List<LatLng> list, int arg2, int arg3) {
        addGraspTrace(list, mGraspChecked);
        mGraspLatLngList = list;
    }

    @Override
    public void onRequestFailed(int arg0, String arg1) {
        Toast.makeText(this.getApplicationContext(), "轨迹纠偏失败:" + arg1,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTraceProcessing(int arg0, int arg1, List<LatLng> arg2) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.displaybtn:
                if (mDisplaybtn.isChecked()) {
                    startMove();
                    mDisplaybtn.setClickable(false);
                }
                break;
            case R.id.record_show_activity_grasp_radio_button:
                mGraspChecked = true;
                mOriginChecked = false;
                mGraspRadioButton.setChecked(true);
                mOriginRadioButton.setChecked(false);
                setGraspEnable(true);
                setOriginEnable(false);
                mDisplaybtn.setChecked(false);
                resetGraspRole();
                break;
            case R.id.record_show_activity_origin_radio_button:
                mOriginChecked = true;
                mGraspChecked = false;
                mGraspRadioButton.setChecked(false);
                mOriginRadioButton.setChecked(true);
                setGraspEnable(false);
                setOriginEnable(true);
                mDisplaybtn.setChecked(false);
                resetOriginRole();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }
}
