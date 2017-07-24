package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

/**
 * Created by Administrator on 2017/7/20.
 */

public class StatisticalData {

    /**
     * TotalMileage : 1510.17
     * TotalSeconds : 78815
     * MaxSpeed : 3
     * AvgSpeed : 0.019160946520332425
     */

    private double TotalMileage;
    private int TotalSeconds;
    private int MaxSpeed;
    private double AvgSpeed;
    private int MonitorRaceCount;

    public int getMonitorRaceCount() {
        return MonitorRaceCount;
    }

    public void setMonitorRaceCount(int monitorRaceCount) {
        MonitorRaceCount = monitorRaceCount;
    }

    public double getTotalMileage() {
        return TotalMileage;
    }

    public void setTotalMileage(double TotalMileage) {
        this.TotalMileage = TotalMileage;
    }

    public int getTotalSeconds() {
        return TotalSeconds;
    }

    public void setTotalSeconds(int TotalSeconds) {
        this.TotalSeconds = TotalSeconds;
    }

    public int getMaxSpeed() {
        return MaxSpeed;
    }

    public void setMaxSpeed(int MaxSpeed) {
        this.MaxSpeed = MaxSpeed;
    }

    public double getAvgSpeed() {
        return AvgSpeed;
    }

    public void setAvgSpeed(double AvgSpeed) {
        this.AvgSpeed = AvgSpeed;
    }

}
