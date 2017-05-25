package com.cpigeon.cpigeonhelper.modular.home.bean;

/**
 * Created by Administrator on 2017/5/25.
 */
public class HomeAd {
    private int id;
    private String adImageUrl;
    private String adUrl;

    public HomeAd() {
    }

    public HomeAd(String adImageUrl, String adUrl) {
        this.adImageUrl = adImageUrl;
        this.adUrl = adUrl;
    }

    public HomeAd(int id, String adImageUrl, String adUrl) {
        this.id = id;
        this.adImageUrl = adImageUrl;
        this.adUrl = adUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }
}
