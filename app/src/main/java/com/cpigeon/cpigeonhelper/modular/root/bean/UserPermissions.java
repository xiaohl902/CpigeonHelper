package com.cpigeon.cpigeonhelper.modular.root.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

public class UserPermissions {

    /**
     * authUserInfo : {"tel":"","phone":"18328 495932","sex":"保密 ","uid":14165,"name":"zg18328495932","headimgUrl":"http://www.cpigeon.com/Content/fa ces/20170503171819.png","nickname":" ","sign":"啦啦啦～ ","email":"2851551313@qq.com"}
     * authTime : 2017-05-19 09:39:31
     * permissions : [{"name":"基本信息修改","group":"通用","id":2,"explain":"修改 协会（公棚）基本信息的权限","enable":true}]
     * enable : true
     */

    private AuthUserInfoBean authUserInfo;
    private String authTime;
    private boolean enable;
    private List<PermissionsBean> permissions;

    public AuthUserInfoBean getAuthUserInfo() {
        return authUserInfo;
    }

    public void setAuthUserInfo(AuthUserInfoBean authUserInfo) {
        this.authUserInfo = authUserInfo;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<PermissionsBean> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionsBean> permissions) {
        this.permissions = permissions;
    }

    public static class AuthUserInfoBean {
        /**
         * tel :
         * phone : 18328 495932
         * sex : 保密
         * uid : 14165
         * name : zg18328495932
         * headimgUrl : http://www.cpigeon.com/Content/fa ces/20170503171819.png
         * nickname :
         * sign : 啦啦啦～
         * email : 2851551313@qq.com
         */

        private String tel;
        private String phone;
        private String sex;
        private int uid;
        private String name;
        private String headimgUrl;
        private String nickname;
        private String sign;
        private String email;

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

    public static class PermissionsBean {
        /**
         * name : 基本信息修改
         * group : 通用
         * id : 2
         * explain : 修改 协会（公棚）基本信息的权限
         * enable : true
         */

        private String name;
        private String group;
        private int id;
        private String explain;
        private boolean enable;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

    }
}
