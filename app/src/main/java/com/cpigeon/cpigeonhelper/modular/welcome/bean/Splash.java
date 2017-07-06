package com.cpigeon.cpigeonhelper.modular.welcome.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Splash implements Serializable {
    private static final long serialVersionUID = 7382351359868556980L;//这里需要写死 序列化Id
    public String url;//图片地址
    public String savePath;//图片的路径

    public Splash(String url, String savePath) {
        this.url = url;
        this.savePath = savePath;
    }
}
