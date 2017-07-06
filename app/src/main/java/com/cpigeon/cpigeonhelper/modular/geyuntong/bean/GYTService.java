package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2017/6/30.
 */

public class GYTService extends RealmObject {

    /**
     * grade : svip
     * authNumber : 0
     * openTime : 2017-06-22 16:09:21
     * reason :
     * isClosed : false
     * usefulTime : 2017-07-30 16:21:20
     * isExpired : false
     * expireTime : 2017-07-30 16:09:30
     */

    private String grade;
    private int authNumber;
    private String openTime;
    private String reason;
    private boolean isClosed;
    private String usefulTime;
    private boolean isExpired;
    private String expireTime;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(int authNumber) {
        this.authNumber = authNumber;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getUsefulTime() {
        return usefulTime;
    }

    public void setUsefulTime(String usefulTime) {
        this.usefulTime = usefulTime;
    }

    public boolean isIsExpired() {
        return isExpired;
    }

    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

}
