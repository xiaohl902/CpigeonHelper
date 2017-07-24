package com.cpigeon.cpigeonhelper.modular.order.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/20.
 */

public class OrderList implements Serializable{

    /**
     * id : 4198
     * time : 2017-05-2514:08:46
     * payway :
     * item :  足 环 查 询 服 务 _ 普 通 套 餐
     * number : ZHCXA2017052501695200014
     * statusname :  待 支 付
     * price : 8
     * scores : 1000
     * ispay : 0
     * serviceid : 5
     */

    private int id;
    private String time;
    private String payway;
    private String item;
    private String number;
    private String statusname;
    private double price;
    private int scores;
    private int ispay;
    private int serviceid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPayway() {
        return payway;
    }

    public void setPayway(String payway) {
        this.payway = payway;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatusname() {
        return statusname;
    }

    public void setStatusname(String statusname) {
        this.statusname = statusname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public int getIspay() {
        return ispay;
    }

    public void setIspay(int ispay) {
        this.ispay = ispay;
    }

    public int getServiceid() {
        return serviceid;
    }

    public void setServiceid(int serviceid) {
        this.serviceid = serviceid;
    }
}
