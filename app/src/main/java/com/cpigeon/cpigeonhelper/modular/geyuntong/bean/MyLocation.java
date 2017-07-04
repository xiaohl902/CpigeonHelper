package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 *
 * Created by andysong on 2017/6/29.
 *
 */

public class MyLocation extends RealmObject{
    @PrimaryKey
    private int id;
    private int raceid;//赛事id
    private double latitude;
    private double longitude;
    private String windDirection;//风向
    private String humidity;//湿度
    private String getReportTime;//发布时间
    private String weather;//天气
    private String temperature;//温度

    public int getRaceid() {
        return raceid;
    }

    public void setRaceid(int raceid) {
        this.raceid = raceid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getGetReportTime() {
        return getReportTime;
    }

    public void setGetReportTime(String getReportTime) {
        this.getReportTime = getReportTime;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
