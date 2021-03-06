package com.cpigeon.cpigeonhelper.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Administrator on 2017/7/4.
 */

public class SessionManager {
    private static SessionManager mInstance=null;

    private IoSession mSession;
    public static SessionManager getInstance(){
        if(mInstance==null){
            synchronized (SessionManager.class){
                if(mInstance==null){
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager(){}

    public void setSession(IoSession session){
        this.mSession = session;
    }

    public void writeToServer(IoBuffer buffer){
        if(mSession!=null){
            buffer.flip();
            mSession.write(buffer);
        }
    }

    public void closeSession(){
        if(mSession!=null){
            mSession.closeOnFlush();
        }
    }

    public void removeSession(){
        this.mSession=null;
    }
}
