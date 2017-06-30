package com.cpigeon.cpigeonhelper.common.network;

/**
 *
 * Created by Administrator on 2017/5/25.
 */

public class ApiResponse<T> {
    private boolean status;
    private int errorCode;
    private String msg;
    private T data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}