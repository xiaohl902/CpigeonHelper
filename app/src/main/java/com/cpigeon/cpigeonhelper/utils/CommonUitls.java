package com.cpigeon.cpigeonhelper.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.ui.button.CircularProgressButton;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/5/25.
 */

public class CommonUitls {
    private static final String KEY_API_SIGN = "iy087234ho78fuqy49TR23jk4h";    //只有url参数的签名算法
    public static final String KEY_SERVER_PWD = "9834h5ok2j32hKgjvmn34JHG89";
    private static String DeviceID;
    private static Toast toast;
    public  static  final  String HOST = "192.168.0.5";
    public  static  final  int POST = 5555;
    public  static  final  int IDLE_TIME = 20;
    public static String getApiSign(Map<String, Object> urlParams) {
        Map<String, String> map = new TreeMap<>();

        if (urlParams != null && urlParams.size() > 0) {
            for (String key : urlParams.keySet()) {
                if (key != null && ("sign".equals(key.toLowerCase()) || TextUtils.isEmpty(urlParams.get(key).toString())))
                    continue;
                if (!map.containsKey("get_" + key)) {
                    map.put("get_" + key, urlParams.get(key).toString());
                }

            }
        }


        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder.append(key + "=" + map.get(key) + "&");
        }
        stringBuilder.append("key=" + KEY_API_SIGN);
        String result = stringBuilder.toString();
        Logger.d(result);
        result = EncryptionTool.MD5(result);
        return result;
    }

    //只有timestamp为url参数的签名算法
    public static String getApiSign(long timestamp, Map<String, Object> postParams) {
        Map<String, Object> map = new TreeMap<>();
        map.put("get_timestamp", timestamp);
        if (postParams != null && postParams.size() > 0) {
            for (String key : postParams.keySet()) {
                if (!map.containsKey("post_" + key) && postParams.get(key) != null && !TextUtils.isEmpty(postParams.get(key).toString())) {
                    map.put("post_" + key, postParams.get(key).toString());
                }

            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder.append(key + "=" + map.get(key) + "&");
        }
        stringBuilder.append("key=" + KEY_API_SIGN);
        String result = stringBuilder.toString();
        Logger.d(result);
        result = EncryptionTool.MD5(result);
        return result;
    }

    //包含urlParams和postParams
    public static String getApiSign(Map<String, Object> urlParams, Map<String, Object> postParams) {
        Map<String, String> map = new TreeMap<>();

        if (urlParams != null && urlParams.size() > 0) {
            for (String key : urlParams.keySet()) {
                if (key != null && ("sign".equals(key.toLowerCase()) || TextUtils.isEmpty(urlParams.get(key).toString())))
                    continue;
                if (!map.containsKey("get_" + key)) {
                    map.put("get_" + key, urlParams.get(key).toString());
                }

            }
        }

        if (postParams != null && postParams.size() > 0) {
            for (String key : postParams.keySet()) {
                if (!map.containsKey("post_" + key) && postParams.get(key) != null && !TextUtils.isEmpty(postParams.get(key).toString())) {
                    map.put("post_" + key, postParams.get(key).toString());
                }

            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder.append(key + "=" + map.get(key) + "&");
        }
        stringBuilder.append("key=" + KEY_API_SIGN);
        String result = stringBuilder.toString();
        Logger.d(result);
        result = EncryptionTool.MD5(result);
        return result;
    }

    /**
     * 获得当前应用的版本号名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 获得当前应用的版本号数字
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int verCode;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            verCode = 0;
        }
        return verCode;
    }

    /**
     * 获取设备IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = telephonyManager.getDeviceId();
        return IMEI;
    }

    /**
     * 安装应用
     *
     * @param context
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        Logger.d(filePath);
        File _file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", _file);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fileUri = Uri.fromFile(_file);
        }
        Logger.d(fileUri);
        intent.setDataAndType(fileUri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 退出
     *
     * @param activity
     */
    public static void exitApp(Activity activity) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
        activity.finish();
        System.exit(0);
    }

    public static void hideIME(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }


    /**
     * 通用正则表达式调用方法
     *
     * @param Content    需要检查的字符串
     * @param PatternStr 正则表达式
     * @return
     */
    public static boolean Compile(String Content, String PatternStr) {
        Pattern p = Pattern.compile(PatternStr);
        Matcher m = p.matcher(Content);
        return m.matches();
    }

    /**
     * 结合多种方式计算设备唯一码
     *
     * @param context
     * @return
     */
    public static String getCombinedDeviceID(Context context) {
        if (!TextUtils.isEmpty(DeviceID)) return DeviceID;
        //IMEI
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String m_szImei = null;
        try {
            m_szImei = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            m_szImei = "";
        }

        //Pseudo-Unique ID
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        //Android ID
        String m_szAndroidID = null;
        try {
            m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
            m_szAndroidID = "";
        }

        //WIFI MAC Address string

//        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        String m_szWLANMAC = getMacAddr();
        //BT MAC Address string
        String m_szBTMAC = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");

        String m_szLongID = m_szImei + m_szDevIDShort
                + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
        //compute md5
        DeviceID = EncryptionTool.MD5(m_szLongID).toLowerCase();
        return DeviceID;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 判断是否是合法手机号
     *
     * @param name
     * @return
     */
    public static boolean isAccountValid(String name) {
        Pattern pattern = Pattern.compile("^(13[0-9]|14[5|7]|15\\d|17[6|7]|18[\\d])\\d{8}$");
        return pattern.matcher(name).matches();
    }


    /**
     * 判断密码是否大于6位
     *
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password) {
        return password.length() > 6;
    }


    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    public static void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
    }

    public static double GPS2AjLocation(double gpsLocationValue) {
        int du = (int) gpsLocationValue;
        double temp = (gpsLocationValue - du) * 60;//需要转换的部分（分）
        int fen = (int) temp;
        temp = (temp - fen) * 60;//秒
        return du + (double) fen / 100 + temp / 10000;
    }
    public static double Aj2GPSLocation(double ajLocationValue)
    {
        int du = (int)ajLocationValue;
        double temp = (ajLocationValue - du) * 100;//需要转换的部分（分）
        int fen = (int)temp;
        temp = (temp - fen) * 100;//秒
        return du + (double)fen / 60 + temp / 3600;
    }
}



