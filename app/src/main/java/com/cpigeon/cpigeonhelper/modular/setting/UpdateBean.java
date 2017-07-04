package com.cpigeon.cpigeonhelper.modular.setting;

/**
 * Created by Administrator on 2017/7/4.
 */

public class UpdateBean {


    /**
     * appName : 中鸽助手
     * packageName : com.cpigeon.cpigeonhelper
     * url : http://114.141.132.146:818/Apk/201703100-release.apk
     * verName : 1.1.1
     * verCode : 201703100
     * updateTime : 2017/3/10
     * updateExplain : 1.新增用户个人信息中心，可进行个人信息的设置 2.新增用户签到 3.新增余额充值入口，余额充值更方便 4.新增余额充值记录，充值记录更直观 5.优化界面,修复BUG
     * force : true
     */

    private String appName;
    private String packageName;
    private String url;
    private String verName;
    private int verCode;
    private String updateTime;
    private String updateExplain;
    private boolean force;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateExplain() {
        return updateExplain;
    }

    public void setUpdateExplain(String updateExplain) {
        this.updateExplain = updateExplain;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
