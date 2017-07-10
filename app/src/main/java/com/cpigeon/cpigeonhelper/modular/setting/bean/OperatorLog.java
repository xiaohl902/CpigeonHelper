package com.cpigeon.cpigeonhelper.modular.setting.bean;

/**
 * Created by Administrator on 2017/7/10.
 */

public class OperatorLog {

    /**
     * ip : 192.168.0.40
     * uid : 17059
     * userInfo : {"headimgUrl":"http://www.cpigeon.com/Content/faces/20170505093321.png","name":"","uid":17059,"sex":"保密","sign":" "}
     * time : 2017-06-14 16:00:23
     * content : 创建司放地【香港】司放地别名【香港好】
     */

    private String ip;
    private int uid;
    private UserInfoBean userInfo;
    private String time;
    private String content;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
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

    public static class UserInfoBean {
        /**
         * headimgUrl : http://www.cpigeon.com/Content/faces/20170505093321.png
         * name :
         * uid : 17059
         * sex : 保密
         * sign :
         */

        private String headimgUrl;
        private String name;
        private int uid;
        private String sex;
        private String sign;

        public String getHeadimgUrl() {
            return headimgUrl;
        }

        public void setHeadimgUrl(String headimgUrl) {
            this.headimgUrl = headimgUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }

}
