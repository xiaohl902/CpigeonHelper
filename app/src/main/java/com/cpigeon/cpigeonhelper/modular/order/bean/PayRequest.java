package com.cpigeon.cpigeonhelper.modular.order.bean;

import com.google.gson.annotations.SerializedName;
import com.tencent.mm.opensdk.modelpay.PayReq;

/**
 * Created by Administrator on 2017/6/28.
 */

public class PayRequest {

    /**
     * appid : wxfa9493e635225d92
     * partnerid : 1481650362
     * prepayid : wx201706281705310ac807af100128987922
     * package : Sign=WXPay
     * noncestr : 5737034557ef5b8c02c0e46513b98f90
     * timestamp : 1498640729
     * sign : 2B23138B163AD0C9B6C3F6C17681204D
     */

    private String appid;
    private String partnerid;
    private String prepayid;
    @SerializedName("package")
    private String packageX;
    private String noncestr;
    private String timestamp;
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 获取微信支付请求数据
     * @return
     */
    public PayReq getWxPayReq() {
        PayReq payReq = new PayReq();
        payReq.appId = getAppid();
        payReq.partnerId = getPartnerid();
        payReq.prepayId = getPrepayid();
        payReq.packageValue = getPackageX();
        payReq.nonceStr = getNoncestr();
        payReq.timeStamp = getTimestamp();
        payReq.sign = getSign();
        return payReq;
    }

}
