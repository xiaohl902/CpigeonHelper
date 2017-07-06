package com.cpigeon.cpigeonhelper.common.network;

import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.ImgTag;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.LocationInfoReports;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.RaceImageOrVideo;
import com.cpigeon.cpigeonhelper.modular.home.bean.Ad;
import com.cpigeon.cpigeonhelper.modular.order.bean.Order;
import com.cpigeon.cpigeonhelper.modular.order.bean.OrderList;
import com.cpigeon.cpigeonhelper.modular.order.bean.PackageInfo;
import com.cpigeon.cpigeonhelper.modular.order.bean.PayRequest;
import com.cpigeon.cpigeonhelper.modular.root.bean.OrgNameApplyStatus;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootList;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootManagerList;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserInfoByTelBean;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserPermissions;
import com.cpigeon.cpigeonhelper.modular.setting.UpdateBean;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.AnnouncementList;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.CheckCode;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.DeviceBean;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;
import com.cpigeon.cpigeonhelper.modular.xiehui.bean.OrgInfo;

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
    Observable<ApiResponse<UserBean>> login(@FieldMap Map<String, Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    //获取登录的设备信息
    @FormUrlEncoded
    @POST("GAPI/V1/GetLoginInfo")
    Observable<ApiResponse<DeviceBean>> getDeviceInfo(@Header("auth") String token, @FieldMap Map<String, Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    //获取头像
    @GET("GAPI/V1/GetUserHeadImg")
    Observable<ApiResponse<String>> getUserHeadImg(@Query("u") String u);

    //通过手机号获取用户信息
    @FormUrlEncoded
    @POST("GAPI/V1/QueryUserByPhone")
    Observable<ApiResponse<List<UserInfoByTelBean>>> getUserInfoByTel(@Header("auth") String token,
                                                                      @Field("p") String tel,
                                                                      @Query("timestamp") long timestamp,
                                                                      @Query("sign") String sign);

    //修改密码（通过验证码）
    @FormUrlEncoded
    @POST("/GAPI/V1/FindPwd")
    Observable<ApiResponse<Object>> getLoginPassword(@FieldMap Map<String, Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    //修改密码
    @FormUrlEncoded
    @POST("/GAPI/V1/SetUserPwd")
    Observable<ApiResponse<Object>> changePassword(@FieldMap Map<String, Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

    //发送验证码
    @FormUrlEncoded
    @POST("GAPI/V1/SendVerifyCode")
    Observable<ApiResponse<CheckCode>> sendVerifyCode(@FieldMap Map<String, Object> params, @Query("timestamp") long timestamp, @Query("sign") String sign);

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
                                                @FieldMap Map<String, Object> params,
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
                                                                      @QueryMap Map<String, Object> urlParams,
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
    @POST("GAPI/V1/GetAuthUserPermissions")
    Observable<ApiResponse<UserPermissions>> getAuthUserPermissions(@Header("auth") String token,
                                                                    @Body RequestBody body);

    //获取服务信息
    @GET("GAPI/V1/GetServicePackageInfo")
    Observable<ApiResponse<List<PackageInfo>>> getServicePackageInfo(@Query("key") String key);


    //创建服务类订单
    @POST("GAPI/V1/CreateServiceOrder")
    Observable<ApiResponse<Order>> createServiceOrder(@Header("auth") String token,
                                                      @Body RequestBody body,
                                                      @Query("timestamp") long timestamp,
                                                      @Query("sign") String sign);
    @POST("GAPI/V1/GetMyOrderList")
    Observable<ApiResponse<List<OrderList>>> getOrderList(@Header("auth") String token,
                                                          @Body RequestBody body,
                                                          @Query("timestamp") long timestamp,
                                                          @Query("sign") String sign);

    //创建微信预支付订单
    @POST("GAPI/V1/GetWXPrePayOrder")
    Observable<ApiResponse<PayRequest>> createWxOrder(@Header("auth") String token,
                                                      @Body RequestBody body,
                                                      @Query("timestamp") long timestamp,
                                                      @Query("sign") String sign);

    //余额支付
    @POST("GAPI/V1/OrderPayByBalance")
    Observable<ApiResponse<Object>> orderPayByBalance(@Header("auth") String token,
                                                       @Body RequestBody body,
                                                       @Query("timestamp") long timestamp,
                                                       @Query("sign") String sign);



    //获取鸽运通比赛列表
    @GET("CHAPI/V1/GetGYTRaceList")
    Observable<ApiResponse<List<GeYunTong>>> getGeYunTongRaceList(@Header("auth") String token,
                                                                  @QueryMap Map<String, Object> urlParams);

    //添加鸽运通比赛
    @POST("CHAPI/V1/CreateGYTRace")
    Observable<ApiResponse<GeYunTong>> createGeYunTongRace(@Header("auth") String token,
                                                           @Body RequestBody body,
                                                           @Query("timestamp") long timestamp,
                                                           @Query("sign") String sign);

    //修改鸽运通比赛
    @POST("CHAPI/V1/UpdateGYTRace")
    Observable<ApiResponse<GeYunTong>> updateGeYunTongRace(@Header("auth") String token,
                                                           @Body RequestBody body,
                                                           @Query("timestamp") long timestamp,
                                                           @Query("sign") String sign);

    //删除鸽运通比赛
    @POST("CHAPI/V1/DeleteGYTRace")
    Observable<ApiResponse<Object>> deleteGeYunTongRace(@Header("auth") String token,
                                                        @Body RequestBody body,
                                                        @Query("timestamp") long timestamp,
                                                        @Query("sign") String sign);

    //批量删除鸽运通比赛
    @POST("CHAPI/V1/DeleteGYTRaces")
    Observable<ApiResponse<Integer>> deleteGeYunTongRaces(@Header("auth") String token,
                                                          @Body RequestBody body,
                                                          @Query("timestamp") long timestamp,
                                                          @Query("sign") String sign);

    //创建司放地
    @POST("CHAPI/V1/CreateFlyingArea")
    Observable<ApiResponse<FlyingArea>> createFlyingArea(@Header("auth") String token,
                                                         @Body RequestBody body,
                                                         @Query("timestamp") long timestamp,
                                                         @Query("sign") String sign);

    //修改司放地
    @POST("/CHAPI/V1/ModifyFlyingArea")
    Observable<ApiResponse<FlyingArea>> modifyflyingarea(@Header("auth") String token,
                                                         @Body RequestBody body,
                                                         @Query("timestamp") long timestamp,
                                                         @Query("sign") String sign);


    //获取司放地
    @GET("CHAPI/V1/GetFlyingAreas")
    Observable<ApiResponse<List<FlyingArea>>> getFlyingAreas(@Query("uid") int uid,
                                                             @QueryMap Map<String, Object> map);


    //删除司放地
    @POST("CHAPI/V1/DeleteFlyingArea")
    Observable<ApiResponse<Object>> deleteFlyingArea(@Header("auth") String token,
                                                     @Body RequestBody body,
                                                     @Query("timestamp") long timestamp,
                                                     @Query("sign") String sign);

    //开始鸽运通监控
    @POST("CHAPI/V1/StartRaceMonitor")
    Observable<ApiResponse<Object>> startRaceMonitor(@Header("auth") String token,
                                                     @Body RequestBody body,
                                                     @Query("timestamp") long timestamp,
                                                     @Query("sign") String sign);

    //结束鸽运通监控
    @POST("CHAPI/V1/StopRaceMonitor")
    Observable<ApiResponse<Object>> stopRaceMonitor(@Header("auth") String token,
                                                    @Body RequestBody body,
                                                    @Query("timestamp") long timestamp,
                                                    @Query("sign") String sign);

    //获取监控定位数据
    @POST("CHAPI/V1/GetGYTLocationInfoReports")
    Observable<ApiResponse<List<LocationInfoReports>>> getGeYunTongLocationInfoReports(@Header("auth") String token,
                                                                                       @Body RequestBody body,
                                                                                       @Query("timestamp") long timestamp,
                                                                                       @Query("sign") String sign);


    //图片视频的上传
    @POST("CHAPI/V1/UploadGYTRaceImageOrVideo")
    Observable<ApiResponse<Object>> raceImageOrVideo(@Header("auth") String token,
                                                     @Body RequestBody body,
                                                     @Query("timestamp") long timestamp,
                                                     @Query("sign") String sign);
    //获取标签
    @GET("GAPI/V1/GetTAG")
    Observable<ApiResponse<List<ImgTag>>> getTag(@Query("type") String type);

    //获取照片或视频
    @GET("CHAPI/V1/GetGYTRaceImageOrVideo")
    Observable<ApiResponse<List<RaceImageOrVideo>>> getGYTRaceImageOrVideo(@Header("auth") String token,
                                                                           @QueryMap Map<String,Object> urlParams );


    //获取用户开通鸽运通的信息
    @GET("CHAPI/V1/GetGYTinfo")
    Observable<ApiResponse<GYTService>> getGYTInfo(@Header("auth") String token,
                                                         @QueryMap Map<String,Object> urlParams);

    //检查更新
    @GET("GAPI/V1/Version?id=com.cpigeon.cpigeonhelper")
    Observable<List<UpdateBean>> checkForUpdate();

}
