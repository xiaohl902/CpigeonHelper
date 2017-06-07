package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.MyApp;

/**
 * Created by Administrator on 2017/6/6.
 */

public class InfoWinAdapter implements AMap.InfoWindowAdapter {
    private Context mContext = MyApp.getInstance();
    private LatLng latLng;
    private String snippet;
    private String agentName;
    private TextView mAreaDistance,mAreaName,mAreaLocation;
    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }

    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_maker, null);
        mAreaDistance = (TextView) view.findViewById(R.id.tv_area_distance);
        mAreaName = (TextView) view.findViewById(R.id.tv_area_name);
        mAreaLocation = (TextView) view.findViewById(R.id.tv_area_location);

        mAreaDistance.setText(snippet);
        mAreaName.setText(agentName);
        mAreaLocation.setText(latLng.toString());
        return view;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
        agentName = marker.getTitle();
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
