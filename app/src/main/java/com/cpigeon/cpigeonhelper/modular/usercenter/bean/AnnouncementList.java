package com.cpigeon.cpigeonhelper.modular.usercenter.bean;

/**
 * Created by Administrator on 2017/5/25.
 */

public class AnnouncementList {

    private String title;
    private String time;
    private String content;
    private int id;
    private int istop;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIstop() {
        return istop;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }
}
