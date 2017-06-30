package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/6/7.
 */

public class GeYunTong implements Parcelable{

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
     * muid : 0
     * mTime : 2017-05-19 09:39:31
     * mEndTime : 2017-05-19 09:39:31
     * raceImage : url 绝对路径
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
    private int muid;
    private String mTime;
    private String mEndTime;
    private String raceImage;

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

    public int getMuid() {
        return muid;
    }

    public void setMuid(int muid) {
        this.muid = muid;
    }

    public String getMTime() {
        return mTime;
    }

    public void setMTime(String mTime) {
        this.mTime = mTime;
    }

    public String getMEndTime() {
        return mEndTime;
    }

    public void setMEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public String getRaceImage() {
        return raceImage;
    }

    public void setRaceImage(String raceImage) {
        this.raceImage = raceImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(raceName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(flyingArea);
        dest.writeString(createTime);
        dest.writeString(flyingTime);
        dest.writeInt(stateCode);
        dest.writeInt(muid);
        dest.writeString(state);
        dest.writeString(mTime);
        dest.writeString(mEndTime);
        dest.writeString(raceImage);
    }

    public static final Creator<GeYunTong> CREATOR = new Creator<GeYunTong>() {
        @Override
        public GeYunTong createFromParcel(Parcel source) {
            return new GeYunTong(source);
        }

        @Override
        public GeYunTong[] newArray(int size) {
            return new GeYunTong[size];
        }
    };

    public GeYunTong(Parcel in)
    {
        id = in.readInt();
        raceName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        flyingArea = in.readString();
        createTime = in.readString();
        flyingTime = in.readString();
        stateCode = in.readInt();
        state = in.readString();
        raceImage = in.readString();
        mEndTime = in.readString();
        mTime = in.readString();
        muid = in.readInt();

    }




}
