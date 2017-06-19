package com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean;

/**
 * Created by Administrator on 2017/6/14.
 */

public class FlyingArea {


    /**
     * longitude : 116.195665
     * faid : 3
     * area :  青华大学
     * latitude : 41.00346
     * alias : qqq
     */

    private double longitude;
    private int faid;
    private String area;
    private double latitude;
    private String alias;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getFaid() {
        return faid;
    }

    public void setFaid(int faid) {
        this.faid = faid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
