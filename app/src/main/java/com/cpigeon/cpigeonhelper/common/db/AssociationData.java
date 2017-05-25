package com.cpigeon.cpigeonhelper.common.db;

import android.os.Build;
import android.text.TextUtils;


import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.MyApp;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;

import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/5/20.
 */

public class AssociationData {

    public static final String VER = String.valueOf(CommonUitls.getVersionCode(MyApp.getInstance()));
    public static final String DEV_ID = CommonUitls.getCombinedDeviceID(MyApp.getInstance());
    public static final String DEV = Build.MODEL;

    public static RealmResults<UserBean> info = RealmUtils.getInstance().queryUserInfo();
    /**
     * 判断用户是否登录
     * @return
     */
    public static boolean checkIsLogin(){

        for (UserBean userBean : info) {
            if (!TextUtils.isEmpty(userBean.getToken())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的uid
     * @return
     */
    public static int getUserId(){
        for (UserBean userBean : info){
            if (userBean.getUid()!=0){
                return userBean.getUid();
            }
        }
        return 0;
    }

    /**
     * 获取当前用户的昵称
     * @return
     */
    public static String getUserName(){
        for (UserBean userBean : info){
            if (!TextUtils.isEmpty(userBean.getNickname())){
                return userBean.getNickname();
            }
        }
        return getName();
    }

    /**
     * 获取名字
     * @return
     */
    public static String getName(){
        for (UserBean userBean : info){
            if (!TextUtils.isEmpty(userBean.getUsername())){
                return userBean.getUsername();
            }
        }
        return "请设置您的名字!";
    }


    /**
     * 获取当前用户的token
     */
    public static String getUserToken()
    {
        for (UserBean userBean : info){
            if (!TextUtils.isEmpty(userBean.getToken())){
                return userBean.getToken();
            }
        }
        return null;
    }

    /**
     * 获取用户输入的密码（AES加密过后）
     */
    public static String getUserPassword(){
        for (UserBean userBean : info)
        {
            if (!TextUtils.isEmpty(userBean.getPassword()))
            {
                return userBean.getPassword();
            }
        }
        return null;
    }

    /**
     * 获取用户的签名
     * @return
     */
    public static String getUserSign(){
        for (UserBean userBean : info)
        {
            if (!TextUtils.isEmpty(userBean.getSign()))
            {
                return userBean.getSign();
            }
        }
        return "这家伙很懒，啥都没写";
    }

    public static String getUserImgUrl(){
        for (UserBean userBean : info)
        {
            if (!TextUtils.isEmpty(userBean.getHeadimgurl()))
            {
                return userBean.getHeadimgurl();
            }
        }
        return "";
    }

    public static String getUsetType(){
        for (UserBean userBean : info)
        {
            if (!TextUtils.isEmpty(userBean.getAccountType()))
            {
                return userBean.getAccountType();
            }
        }
        return "";
    }


}
