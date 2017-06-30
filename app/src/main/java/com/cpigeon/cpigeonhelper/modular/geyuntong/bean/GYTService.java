package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2017/6/30.
 */

public class GYTService extends RealmObject{
    /**
     * openTime : 2017-05-1909:39:31
     * expireTime : 2018-05-19 09:39:31
     * isClosed : false
     * reason : 关闭原因
     * isExpired : false
     */
    private String openTime;
    private String expireTime;
    private boolean isClosed;
    private String reason;
    private boolean isExpired;

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isIsExpired() {
        return isExpired;
    }

    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

}
