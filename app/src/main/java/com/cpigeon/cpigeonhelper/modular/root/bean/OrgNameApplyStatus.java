package com.cpigeon.cpigeonhelper.modular.root.bean;

/**
 * Created by Administrator on 2017/5/22.
 */

public class OrgNameApplyStatus {


    /**
     * status : 审核中
     * statusCode : 0
     * failExplain : 失败原因
     */

    private String status;
    private int statusCode;
    private String failExplain;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getFailExplain() {
        return failExplain;
    }

    public void setFailExplain(String failExplain) {
        this.failExplain = failExplain;
    }

}
