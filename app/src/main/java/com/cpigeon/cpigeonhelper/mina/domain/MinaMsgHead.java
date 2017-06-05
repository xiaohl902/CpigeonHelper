package com.cpigeon.cpigeonhelper.mina.domain;

/**
 * mina消息头（6位)
 */
public class MinaMsgHead {
    public short     event;//消息事件号 2位
    public int       bodyLen;//消息内容长度 4位
}
