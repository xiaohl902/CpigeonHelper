package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

import android.location.Location;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andysong on 2017/6/28.
 */

public class PathRecord {
    private AMapLocation mStartPoint;
    private AMapLocation mEndPoint;
    private List<Location> mPathLinePoints = new ArrayList<>();
    private String mDistance;
    private String mDuration;
    private String mAveragespeed;
    private String mDate;
    private int mId = 0;

    public PathRecord() {

    }

    public AMapLocation getmStartPoint() {
        return mStartPoint;
    }

    public void setmStartPoint(AMapLocation mStartPoint) {
        this.mStartPoint = mStartPoint;
    }

    public AMapLocation getmEndPoint() {
        return mEndPoint;
    }

    public void setmEndPoint(AMapLocation mEndPoint) {
        this.mEndPoint = mEndPoint;
    }

    public List<Location> getmPathLinePoints() {
        return mPathLinePoints;
    }

    public void setmPathLinePoints(List<Location> mPathLinePoints) {
        this.mPathLinePoints = mPathLinePoints;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getmAveragespeed() {
        return mAveragespeed;
    }

    public void setmAveragespeed(String mAveragespeed) {
        this.mAveragespeed = mAveragespeed;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public void addpoint(Location point) {
        mPathLinePoints.add(point);
    }

    @Override
    public String toString() {
        StringBuilder record = new StringBuilder();
        record.append("recordSize:" + getmPathLinePoints().size() + ", ");
        record.append("distance:" + getmDistance() + "m, ");
        record.append("duration:" + getmDuration() + "s");
        return record.toString();
    }
}
