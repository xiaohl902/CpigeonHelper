package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;


/**
 *
 * Created by Administrator on 2017/5/26.
 */

public class PackageInfo {

    /**
     * id : 10
     * unitname : 年
     * scores : 0
     * name : 鸽运通开通
     * detial :
     * price : 580
     * package : 普通用户
     * brief : 鸽运通开通
     * opentime : 2017-05-25 12:01:05
     * servicetimes : 0
     * expiretime : 2999-12-31 00:00:00
     * flag : 1
     */

    private int id;
    private String unitname;
    private int scores;
    private String name;
    private String detial;
    private double price;
    private double  originalPrice;
    private String packageName;
    private String brief;
    private String opentime;

    private int servicetimes;
    private String expiretime;
    private int flag;


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetial() {
        return detial;
    }

    public void setDetial(String detial) {
        this.detial = detial;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public int getServicetimes() {
        return servicetimes;
    }

    public void setServicetimes(int servicetimes) {
        this.servicetimes = servicetimes;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
