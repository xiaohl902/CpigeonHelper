package com.cpigeon.cpigeonhelper.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Administrator on 2017/7/4.
 */

public class MyCodecFactory implements ProtocolCodecFactory {
    private MyDataDecoder decoder;
    private MyDataEncoder encoder;
    public MyCodecFactory() {
        encoder = new MyDataEncoder();
        decoder = new MyDataDecoder();
    }
    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}