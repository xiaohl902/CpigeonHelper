package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.mina.ConnectionConfig;
import com.cpigeon.cpigeonhelper.mina.ConnectionManager;
import com.cpigeon.cpigeonhelper.mina.SessionManager;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;

import org.apache.mina.core.buffer.IoBuffer;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;


public class CoreService extends Service {
    public static final String TAG="CoreService";
    private ConnectionThread thread;
    ConnectionManager mManager;
    private Handler handler = new Handler();
    public CoreService() {
    }

    @Override
    public void onCreate() {
        ConnectionConfig config = new ConnectionConfig.Builder(getApplicationContext())
                .setIp("192.168.0.5")//连接的IP地址
                .setPort(5555)//连接的端口号
                .setReadBufferSize(1024)
                .setConnectionTimeout(10000).builder();
        mManager = new ConnectionManager(config);
        thread = new ConnectionThread();
        thread.start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class ConnectionThread extends Thread {
        boolean isConnection;
        @Override
        public void run() {
            for (;;){
                isConnection = mManager.connect();
                if (isConnection) {
                    Log.d(TAG,"连接成功跳出循环");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String s =  EncryptionTool.encryptAES(AssociationData.getUserToken(), EncryptionTool.MD5(KEY_SERVER_PWD).toLowerCase());
                            IoBuffer buffer = IoBuffer.allocate(100000);
                            buffer.put(
                                    ("[len="+s.length()+"]"+s)
                                    .getBytes());
                            SessionManager.getInstance().writeToServer(buffer);
                        }
                    });

                    break;
                }
                try {
                    Log.d(TAG,"尝试重新连接");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void disConnect() {
        mManager.disConnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disConnect();
        thread = null;
    }
}
