package com.cpigeon.cpigeonhelper.mina;

import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.orhanobut.logger.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import static com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarServiceFragment.isFirst;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;

public class SessionManager {
    private boolean first = true;
    private static SessionManager mInstance = null;

    private IoSession mSession;

    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager() {
    }

    public void setSession(IoSession session) {
        this.mSession = session;
    }

    public void writeToServer(IoBuffer buffer) {
        if (mSession != null) {

            buffer.flip();
            mSession.write(buffer);
            Logger.e("发送数据");
            isFirst = false;
        }
    }

    public void closeSession() {
        if (mSession != null) {
            mSession.closeOnFlush();
        }
    }

    public void removeSession() {
        this.mSession = null;
    }
}
