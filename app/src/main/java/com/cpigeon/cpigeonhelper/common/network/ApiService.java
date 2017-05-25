package com.cpigeon.cpigeonhelper.common.network;

import com.cpigeon.cpigeonhelper.modular.root.bean.OrgInfo;
import com.cpigeon.cpigeonhelper.modular.root.bean.OrgNameApplyStatus;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootList;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootManagerList;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserPermissions;
import com.cpigeon.cpigeonhelper.modular.home.bean.Ad;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.AnnouncementList;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.CheckCode;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.DeviceBean;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootUserBean;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface ApiService {
    ///////////////////////////////////////////////////////////////////////////
    // 用户信息接口
    ///////////////////////////////////////////////////////////////////////////
    @FormUrlEncoded
    @POST("GAPI/V1/Login")
    Observable<ApiResponse<UserBean>> login(@FieldMap Map<String,Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign );

    //获取登录的设备信息
    @FormUrlEncoded
    @POST("GAPI/V1/GetLoginInfo")
    Observable<ApiResponse<DeviceBean>> getDeviceInfo(@Header("auth") String token, @FieldMap Map<String,Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    //获取头像
    @GET("GAPI/V1/GetUserHeadImg")
    Observable<ApiResponse<String>> getUserHeadImg(@Query("u") String u);

    //通过手机号获取用户信息
    @FormUrlEncoded
    @POST("GAPI/V1/QueryUserByPhone")
    Observable<ApiResponse<List<RootUserBean>>> getUserInfoByTel(@Header("auth") String token,
                                                                 @Field("p") String tel,
                                                                 @Query("timestamp") long timestamp,
                                                                 @Query("sign") String sign);

    //修改密码（通过验证码）
    @FormUrlEncoded
    @POST("/GAPI/V1/FindPwd")
    Observable<ApiResponse<Object>> getLoginPassword(@FieldMap Map<String,Object> params,@Query("timestamp") long timestamp,@Query("sign") String sign);

    //修改密码
    @FormUrlEncoded
    @POST("/GAPI/V1/SetUserPwd")
    Observable<ApiResponse<Object>> changePassword(@FieldMap Map<String,Object> params,@Query("timestamp") long timestamp,@Query("sign") String sign);

    //发送验证码
    @FormUrlEncoded
    @POST("GAPI/V1/SendVerifyCode")
    Observable<ApiResponse<CheckCode>> sendVerifyCode(@FieldMap Map<String,Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    ///////////////////////////////////////////////////////////////////////////
    // 主页数据
    ///////////////////////////////////////////////////////////////////////////
    @GET("GAPI/V1/GetAd")
    Observable<ApiResponse<List<Ad>>> getAllAd();


    @GET("CHAPI/V1/GetAnnouncementTop")
    Observable<ApiResponse<AnnouncementList>> getAnnouncementTop();

    ///////////////////////////////////////////////////////////////////////////
    // 权限
    ///////////////////////////////////////////////////////////////////////////


    //获取网站公告信息
    @GET("GAPI/V1/GetAnnouncementList")
    Observable<ApiResponse<List<AnnouncementList>>> getAnnouncementList(@Query("type") String type);


    //获取用户关联的组织信息
    @GET("/CHAPI/V1/GetOrgInfo")
    Observable<ApiResponse<OrgInfo>> getOrgInfo(
            @Header("auth") String token,
            @Query("uid") int uid);


    //设置用户关联的组织信息
    @FormUrlEncoded
    @POST("CHAPI/V1/SetOrgInfo")
    Observable<ApiResponse<OrgInfo>> setOrgInfo(@Header("auth") String token,
                                                @FieldMap Map<String,Object> params,
                                                @Query("timestamp") long timestamp,
                                                @Query("sign") String sign);
    //提交修改组织申请
    @POST("CHAPI/V1/SubmitOrgNameApply")
    Observable<ApiResponse<Object>> submitOrgNameApply(@Header("auth") String token,
                                                       @Body RequestBody body,
                                                       @Query("timestamp") long timestamp,
                                                       @Query("sign") String sign);
    //获取组织名称修改申请状态
    @GET("CHAPI/V1/GetOrgNameApplyStatus")
    Observable<ApiResponse<OrgNameApplyStatus>> getOrgNameApplyStatus(@Header("auth") String token,
                                                                      @QueryMap Map<String,Object> urlParams,
                                                                      @Query("sign") String sign);
    //获取被授权的用户列表
    @GET("GAPI/V1/GetAuthUsers")
    Observable<ApiResponse<List<RootList>>> getAuthUsers(@Header("auth") String token,
                                                         @Query("uid") String uid);
    //设置用户 的权限，仅管理员操作
    @POST("GAPI/V1/SetAuthUserPermissions")
    Observable<ApiResponse<RootManagerList>> setAuthUserPermissions(@Header("auth") String token,
                                                                    @Body RequestBody body,
                                                                    @Query("timestamp") long timestamp,
                                                                    @Query("sign") String sign);

    //获取用户 的权限,仅管理员操作
    @GET("GAPI/V1/GetAuthUserPermissions")
    Observable<ApiResponse<UserPermissions>> getAuthUserPermissions(@Header("auth") String token,
                                                                    @QueryMap Map<String,Object> urlParams);
}
