package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

/**
 * Created by Administrator on 2017/6/7.
 */

public class GeYunTong {

    /**
     * id : 11
     * raceName : 秦岭赛
     * latitude : 103.02562251
     * longitude : 35.2545255
     * flyingArea : 秦岭
     * createTime : 2017-05-19 09:39:31
     * flyingTime : 2017-05-19 09:39:31
     * stateCode : 0
     * state : 未开始监控|监控中|监控结束|未知状态
     */

    private int id;
    private String raceName;
    private double latitude;
    private double longitude;
    private String flyingArea;
    private String createTime;
    private String flyingTime;
    private int stateCode;
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
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

    public String getFlyingArea() {
        return flyingArea;
    }

    public void setFlyingArea(String flyingArea) {
        this.flyingArea = flyingArea;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFlyingTime() {
        return flyingTime;
    }

    public void setFlyingTime(String flyingTime) {
        this.flyingTime = flyingTime;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
