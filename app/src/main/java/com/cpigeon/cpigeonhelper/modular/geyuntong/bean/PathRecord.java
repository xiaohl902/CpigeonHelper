package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;


import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */

public class PathRecord{
    private int mId = 0;
    private AMapLocation mStartPoint;
    private AMapLocation mEndPoint;
    private List<AMapLocation> mPathLinePoints = new ArrayList<AMapLocation>();
    private String mDistance;
    private String mDuration;
    private String mAveragespeed;
    private String mDate;

    public PathRecord() {

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public AMapLocation getStartpoint() {
        return mStartPoint;
    }

    public void setStartpoint(AMapLocation startpoint) {
        this.mStartPoint = startpoint;
    }

    public AMapLocation getEndpoint() {
        return mEndPoint;
    }

    public void setEndpoint(AMapLocation endpoint) {
        this.mEndPoint = endpoint;
    }

    public List<AMapLocation> getPathline() {
        return mPathLinePoints;
    }

    public void setPathline(List<AMapLocation> pathline) {
        this.mPathLinePoints = pathline;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getAveragespeed() {
        return mAveragespeed;
    }

    public void setAveragespeed(String averagespeed) {
        this.mAveragespeed = averagespeed;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void addpoint(AMapLocation point) {
        mPathLinePoints.add(point);
    }

    @Override
    public String toString() {
        StringBuilder record = new StringBuilder();
        record.append("recordSize:" + getPathline().size() + ", ");
        record.append("distance:" + getDistance() + "m, ");
        record.append("duration:" + getDuration() + "s");
        return record.toString();
    }
}
