package com.cpigeon.cpigeonhelper.modular.usercenter.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Administrator on 2017/5/25.
 */
public class UserBean extends RealmObject implements Serializable {
    /**
     * username : zg18782050317
     * uid : 17059
     * nickname : 💋💋💋💋💋💋💋💋💋💋
     * sltoken : 1350d7e283ec9efd1749216f1b687558
     * headimg : 20170505093321.png
     * headimgurl : http://www.cpigeon.com/Content/faces/20170505093321.png
     */
    @PrimaryKey
    private int uid;
    @Required
    private String username;
    @Required
    private String token;
    @Required
    private String nickname;
    private String sltoken;
    private String sign;
    private String headimgurl;
    private String accountType;
    private String password;
    private String type;
    private String atype;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSltoken() {
        return sltoken;
    }

    public void setSltoken(String sltoken) {
        this.sltoken = sltoken;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
}

