package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.os.Handler;
import android.os.Message;

import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.KEY_SERVER_PWD;

/**
 * Created by Administrator on 2017/6/2.
 */

public class TcpManager {
    protected static final int STATE_FROM_SERVER_OK = 0;
    private static String dsName = "192.168.0.5";
    private static int dstPort = 5555;
    private static Socket socket;
    private boolean first = true;
    private static TcpManager instance;

    private TcpManager() {
    };

    public static TcpManager getInstance() {
        if (instance == null) {
            synchronized (TcpManager.class) {
                if (instance == null) {
                    instance = new TcpManager();
                }
            }
        }
        return instance;
    }

    /**
     * 连接
     *
     * @return
     */
    public boolean connect(final Handler handler) {

        if (socket == null || socket.isClosed()) {
            new Thread(() -> {

                try {
                    socket = new Socket(dsName, dstPort);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Logger.e("错误消息");
                }

                try {
                    // 输入流，为了获取客户端发送的数据
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        final String result = new String(buffer, 0, len);

                        Message msg = Message.obtain();
                        msg.obj = result;
                        msg.what = STATE_FROM_SERVER_OK;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
        }

        return true;
    }



    /**
     * 发送信息
     *
     * @param content
     */
    public void sendMessage(String content) {
        OutputStream os = null;
        try {
            if (socket != null) {
                os = socket.getOutputStream();
                if (first){
                    os.write(EncryptionTool.encryptAES(AssociationData.getUserToken(),EncryptionTool.MD5(KEY_SERVER_PWD).toLowerCase()).getBytes());
                    first = false;
                }
                os.write(content.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("发送失败:" + e.getMessage());
        }
        //此处不能关闭
//      finally {
//          if (os != null) {
//              try {
//                  os.close();
//              } catch (IOException e) {
//                  throw new RuntimeException("未正常关闭输出流:" + e.getMessage());
//              }
//          }
//      }
    }

    /**
     * 关闭连接
     */
    public void disConnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException("关闭异常:" + e.getMessage());
            }
            socket = null;
        }
    }
}