package com.cpigeon.cpigeonhelper.modular.order.bean;

/**
 * Created by Administrator on 2017/6/20.
 */

public class Order {


    /**
     * id : 4198
     * time : 2017-05-2514:08:46
     * number : ZHCXA2017052501695200014
     * item : 足环查询服务_普通套餐
     * price : 8
     * scores : 1000
     */

    private int id;
    private String time;
    private String number;
    private String item;
    private double price;
    private int scores;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
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
}
