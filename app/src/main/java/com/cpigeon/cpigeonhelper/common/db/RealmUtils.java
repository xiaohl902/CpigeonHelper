package com.cpigeon.cpigeonhelper.common.db;


import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PathRecord;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/5/16.
 */

public class RealmUtils {
    public static final String DB_NAME = "cpigeon.realm";
    private Realm mRealm;
    private static RealmUtils instance;

    private RealmUtils() {
    }

    public static RealmUtils getInstance() {
        if (instance == null) {
            synchronized (RealmUtils.class) {
                if (instance == null)
                    instance = new RealmUtils();
            }
        }
        return instance;
    }


    protected Realm getRealm() {
        if (mRealm == null || mRealm.isClosed())
            mRealm = Realm.getDefaultInstance();
        return mRealm;
    }
    ///////////////////////////////////////////////////////////////////////////
    // 登录相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 添加用户信息
     * @param userBean
     */
    public void insertUserInfo(UserBean userBean)
    {
        getRealm().beginTransaction();//开启事务
        getRealm().copyToRealm(userBean);
        getRealm().commitTransaction();
    }

    /**
     * 查询用户信息
     * @return
     */
    public RealmResults<UserBean> queryUserInfo(){

        return  getRealm().where(UserBean.class).findAll();
    }

    /**
     * 判断是否存在用户信息
     * @return
     */
    public boolean existUserInfo(){
        RealmResults<UserBean> results = getRealm().where(UserBean.class).findAll();
        if (results!=null && results.size()>0)
        {
            return true;
        }
        return false;
    }

    public void deleteUserInfo() {
        RealmResults<UserBean> results = getRealm().where(UserBean.class).findAll();
        getRealm().executeTransaction(realm -> results.deleteAllFromRealm());
    }

    public RealmResults<GYTService> queryGTYInfo(){
        return getRealm().where(GYTService.class).findAll();
    }

    public void insertGYTService(GYTService gytService) {
        getRealm().beginTransaction();//开启事务
        getRealm().copyToRealm(gytService);
        getRealm().commitTransaction();
    }

    public boolean existGYTInfo(){
        RealmResults<GYTService> results = getRealm().where(GYTService.class).findAll();
        if (results!=null && results.size()>0)
        {
            return true;
        }
        return false;
    }

    public void deleteGYTInfo() {
        RealmResults<GYTService> results = getRealm().where(GYTService.class).findAll();
        getRealm().executeTransaction(realm -> results.deleteAllFromRealm());
    }



}
