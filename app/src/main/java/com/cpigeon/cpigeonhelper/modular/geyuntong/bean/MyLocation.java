package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 *
 * Created by andysong on 2017/6/29.
 *
 */

public class MyLocation extends RealmObject{

    private int raceId;//赛事id
    private double longitude;//经度
    private double latitude;//纬度
    private String speed;//速度
    private String weather;//天气
    private String windPower;//风力
    private String windDirection;//风向
    private String temperature;//温度
    private String areDistance;//空距
    private long time;//时间

    public MyLocation() {
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getAreDistance() {
        return areDistance;
    }

    public void setAreDistance(String areDistance) {
        this.areDistance = areDistance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
