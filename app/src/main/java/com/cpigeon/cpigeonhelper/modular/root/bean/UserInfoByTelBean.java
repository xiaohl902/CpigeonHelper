package com.cpigeon.cpigeonhelper.modular.root.bean;

/**
 * Created by Administrator on 2017/5/17.
 */

public class UserInfoByTelBean {
    /**
     * tel :
     * phone : 13628035975
     * sex : 保密
     * uid : 17458
     * name : zg13628035975
     * headimgUrl : http://www.cpigeo n.com/Content/faces/20170412161704.png
     * nickname : ado
     * sign :
     * email :
     */
    private String tel;
    private String phone;
    private String sex;
    private int uid;
    private String name;
    private Boolean authEnable;
    private String headimgUrl;
    private String nickname;
    private String sign;
    private String email;
    private int authUid;

    public int getAuthUid() {
        return authUid;
    }

    public void setAuthUid(int authUid) {
        this.authUid = authUid;
    }

    public Boolean getAuthEnable() {
        return authEnable;
    }

    public void setAuthEnable(Boolean authEnable) {
        this.authEnable = authEnable;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
