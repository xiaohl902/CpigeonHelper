package com.cpigeon.cpigeonhelper.modular.xiehui.bean;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2017/5/18.
 */

public class OrgInfo extends RealmObject{


    /**
     * shortname :
     * closed : 0
     * addr :
     * isAdmin : true
     * expireTime : 2018-05-17 00:00:00
     * type : xiehui
     * registTime : 2017-05-17 10:32:38
     * setupTime : 2017-05-17 10:32:38
     * phone : 18782050317
     * contacts : cs
     * uid : 17059
     * name : 中鸽网 APP 测试
     * email :
     */
    @PrimaryKey
    private int uid;
    private String shortname;
    private int closed;
    private String addr;
    private boolean isAdmin;
    private String expireTime;
    private String type;
    private String registTime;
    private String setupTime;
    private String phone;
    private String contacts;
    private String name;
    private String email;
    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(String setupTime) {
        this.setupTime = setupTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
