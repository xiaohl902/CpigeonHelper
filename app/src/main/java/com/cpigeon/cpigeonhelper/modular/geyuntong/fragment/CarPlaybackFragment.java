package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseFragment;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.LocationInfoReports;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.MyLocation;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.DateUtils;
import com.cpigeon.cpigeonhelper.utils.PointsUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/6/9.
 */

public class CarPlaybackFragment extends BaseFragment {

    @BindView(R.id.displaybtn)
    ToggleButton mDisplaybtn;
    @BindView(R.id.mapView)
    TextureMapView mMapView;
    @BindView(R.id.tv_total_mileage)
    TextView tvTotalMileage;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_sifang_location)
    TextView tvSifangLocation;
    @BindView(R.id.tv_sifang_weather)
    TextView tvSifangWeather;
    @BindView(R.id.tv_now_location)
    TextView tvNowLocation;
    @BindView(R.id.tv_now_weather)
    TextView tvNowWeather;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    private Bundle state;
    private AMap aMap;
    private GeYunTong geYunTong;
    private List<LatLng> list;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        geYunTong = ((ACarServiceActivity) activity).getGeYunTong();
    }

    public static CarPlaybackFragment newInstance() {
        return new CarPlaybackFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_car_playback;
    }

    @Override
    public void finishCreateView(Bundle state) {
        this.state = state;
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        mMapView.onCreate(state);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        list = readLatLngs();
        isPrepared = false;
        mDisplaybtn.setOnClickListener(v -> {
            if (mDisplaybtn.isChecked()) {
                move();
            }
        });
    }

    public void move() {
        addPolylineInPlayGround();
        // 获取轨迹坐标点

        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0 ; i < list.size(); i++) {
            b.include(list.get(i));
        }
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        final SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        // 设置滑动的图标
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.car));

        LatLng drivePoint = list.get(0);
        Pair<Integer, LatLng> pair = PointsUtil.calShortestDistancePoint(list, drivePoint);
        list.set(pair.first, drivePoint);
        List<LatLng> subList = list.subList(pair.first, list.size());

        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);
        smoothMarker.setTotalDuration(40);
        smoothMarker.startSmoothMove();
    }


    private void addPolylineInPlayGround() {
        List<Integer> colorList = new ArrayList<Integer>();
        List<BitmapDescriptor> bitmapDescriptors = new ArrayList<BitmapDescriptor>();

        int[] colors = new int[]{Color.argb(255, 0, 255, 0), Color.argb(255, 255, 255, 0), Color.argb(255, 255, 0, 0)};

        //用一个数组来存放纹理
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));

        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(0);//对应上面的第0个纹理
        texIndexList.add(1);
        texIndexList.add(2);

        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            colorList.add(colors[random.nextInt(3)]);
            bitmapDescriptors.add(textureList.get(0));

        }

        aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)) //setCustomTextureList(bitmapDescriptors)
                .addAll(list)
                .useGradient(true)
                .width(18));
    }



    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<>();
        RealmResults<MyLocation> locations = RealmUtils.getInstance().queryLocation(geYunTong.getId());
        if (locations!=null && locations.size()>4)
        {
            tvTotalTime.setText(locations.get(locations.size()-1).getTime()-locations.get(0).getTime()+"相差");
            for (MyLocation myLocation: locations) {
                points.add(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
            }
        }else {
            Map<String,Object> urlParams = new HashMap<>();
            urlParams.put("uid", AssociationData.getUserId());
            urlParams.put("rid", geYunTong.getId());
            urlParams.put("hw", "y");
            RetrofitHelper.getApi()
                    .getGeYunTongLocationInfoReports(AssociationData.getUserToken(),urlParams)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listApiResponse -> {
                       if (listApiResponse.getErrorCode() == 0 && listApiResponse.getData()!=null&&
                               listApiResponse.getData().size()>4)
                       {
                           for (LocationInfoReports locationInfoReports : listApiResponse.getData())
                           {
                               points.add(new LatLng(locationInfoReports.getLa(), locationInfoReports.getLo()));
                           }

                           List<LocationInfoReports> data = listApiResponse.getData();
                           LocationInfoReports lastLocationInfo = data.get(data.size()-1);

                           tvTotalTime.setText("已监控："+DateUtils.compareDate(lastLocationInfo.getTime(),data.get(0).getTime()));

                           tvTotalMileage.setText("总里程："+CommonUitls.doubleformat(lastLocationInfo.getLc()*0.001,2)+"KM");

                           if (geYunTong.getLongitude()!=0&&geYunTong.getLatitude()!=0)
                           {
                               tvSifangLocation.setText("司放地坐标："+geYunTong.getLatitude()+"/"+geYunTong.getLongitude());

                           }else {
                               tvSifangLocation.setText("司放地坐标："+lastLocationInfo.getLa()+"/"+lastLocationInfo.getLo());
                           }

                           tvSpeed.setText("速度："+data.get(0).getSpeed()+"Km/h");
                           tvNowLocation.setText("当前坐标："+data.get(0).getLa()+"/"+data.get(0).getLo());
                           tvNowWeather.setText("当前天气："+data.get(0).getWeather().getWeather());

                           tvSifangWeather.setText("司放地天气："+lastLocationInfo.getWeather().getWeather()+"/"
                           +lastLocationInfo.getWeather().getTemperature()+"°"+"/"+lastLocationInfo.getWeather().getWindDirction()
                           +"/"+lastLocationInfo.getWeather().getWindPower());

                           tvDistance.setText("空距："+CommonUitls.doubleformat(CommonUitls.getDistance(data.get(0).getLa(),data.get(0).getLo(),
                                   lastLocationInfo.getLa(),lastLocationInfo.getLo())*0.001,2)+"Km");


                       }
                    }, throwable -> {
                        if (throwable instanceof SocketTimeoutException)
                        {
                            CommonUitls.showToast(getActivity(),"连接超时，网络状态可能不太好");
                        }else if (throwable instanceof ConnectException)
                        {
                            CommonUitls.showToast(getActivity(),"连接断开，请检查连接");
                        }else if (throwable instanceof RuntimeException)
                        {
                            CommonUitls.showToast(getActivity(),"发生了不可预期的错误，错误代码:"+throwable.getMessage());
                        }
                    });
        }
        return points;
    }
}
