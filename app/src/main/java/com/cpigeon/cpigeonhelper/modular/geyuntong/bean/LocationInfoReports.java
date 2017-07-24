package com.cpigeon.cpigeonhelper.modular.geyuntong.bean;

/**
 * Created by Administrator on 2017/6/14.
 */

public class LocationInfoReports {


    private int id;
    private String time;
    private double lo;
    private double speed;
    private WeatherBean weather;
    private double la;
    private double lc;


    public LocationInfoReports() {
    }

    public double getLc() {
        return lc;
    }

    public void setLc(double lc) {
        this.lc = lc;
    }


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

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public WeatherBean getWeather() {
        return weather;
    }

    public void setWeather(WeatherBean weather) {
        this.weather = weather;
    }

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public static class WeatherBean {
        /**
         * windPower : 7
         * weather : 多云
         * temperature : 32
         * windDirction : 东南
         * time : 2017-06-07 15:00:00
         */

        private String windPower;
        private String weather;
        private int temperature;
        private String windDirction;
        private String time;

        public String getWindPower() {
            return windPower;
        }

        public void setWindPower(String windPower) {
            this.windPower = windPower;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public String getWindDirction() {
            return windDirction;
        }

        public void setWindDirction(String windDirction) {
            this.windDirction = windDirction;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
