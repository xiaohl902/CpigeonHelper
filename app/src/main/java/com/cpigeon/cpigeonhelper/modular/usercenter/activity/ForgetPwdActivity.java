package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.common.network.ApiConstants;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.CheckCode;
import com.cpigeon.cpigeonhelper.ui.button.CircularProgressButton;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tech.michaelx.authcode.AuthCode;
import tech.michaelx.authcode.CodeConfig;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.simulateErrorProgress;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.simulateSuccessProgress;

/**
 * Created by Administrator on 2017/5/25.
 */

public class ForgetPwdActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_phonenumber)
    AutoCompleteTextView etPhonenumber;
    @BindView(R.id.et_checkcode)
    EditText etCheckcode;
    @BindView(R.id.btn_sendcheckcode)
    Button btnSendcheckcode;
    @BindView(R.id.et_newpwd)
    AutoCompleteTextView etNewpwd;
    @BindView(R.id.et_checknewpwd)
    AutoCompleteTextView etChecknewpwd;
    @BindView(R.id.btn_confim)
    CircularProgressButton btnConfim;
    private long timestamp;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resetpwd;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {
        mColor = getResources().getColor(R.color.colorPrimary);
        toolbar.setTitle("重置密码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

    }


    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, mColor);
    }


    public void changePassWord() {
        timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("t", etPhonenumber.getText().toString().trim());
        postParams.put("y", etCheckcode.getText().toString().trim());
        postParams.put("p", etNewpwd.getText().toString().trim());

        btnConfim.setProgress(50);
        RetrofitHelper
                .getApi()
                .getLoginPassword(postParams, timestamp, CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(objectApiResponse -> {
                    if (objectApiResponse.isStatus()) {
                        simulateSuccessProgress(btnConfim);
                    } else {
                        simulateErrorProgress(btnConfim);
                    }
                },throwable -> {
                    Logger.e("错误的消息"+throwable.getMessage());
                });
    }

    @OnClick({R.id.btn_sendcheckcode, R.id.btn_confim})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sendcheckcode:
                timer.start();
                getCheckCode();
                break;
            case R.id.btn_confim:
                if (btnConfim.getProgress() == 0) {
                    changePassWord();
                } else {
                    btnConfim.setProgress(0);
                    changePassWord();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /**
     * 获取验证码
     */
    public void getCheckCode() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS)
                .compose(bindToLifecycle())
                .subscribe(granted  -> {
                    if (granted){
                        sendCheckCode();
                    }else {
                        sendCheckCode();
                    }
                });
    }

    private void sendCheckCode() {
        timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("p", etPhonenumber.getText().toString().trim());
        postParams.put("t", "2");
        RetrofitHelper.getApi()
                .sendVerifyCode(postParams, timestamp, CommonUitls.getApiSign(timestamp,postParams))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkCodeApiResponse -> {
                    if (checkCodeApiResponse.isStatus()) {
                        CodeConfig config = new CodeConfig.Builder()
                                .codeLength(4) // 设置验证码长度
                                .smsFromStart(106) // 设置验证码发送号码前几位数字
                                .smsBodyStartWith("温馨提醒：") // 设置验证码短信开头文字
                                .smsBodyContains("验证码：") // 设置验证码短信内容包含文字
                                .build();
                        AuthCode.getInstance().with(ForgetPwdActivity.this).config(config).into((EditText) findViewById(R.id.et_checkcode));

                    } else {
                        switch (checkCodeApiResponse.getErrorCode()) {
                            case 1005:
                                Toast.makeText(mContext, "发送失败,同一个手机号每天获取最多两次哦", Toast.LENGTH_SHORT).show();
                                break;
                            case 1003:
                                Toast.makeText(mContext, "发送失败,手机号码已被使用", Toast.LENGTH_SHORT).show();
                                break;
                            case 1008:
                                Toast.makeText(mContext, "发送失败,该手机号码未绑定账号", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                },throwable -> {
                    Logger.e("错误消息"+throwable.getMessage());
                });
    }


    private CountDownTimer timer = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

            btnSendcheckcode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
            btnSendcheckcode.setClickable(false);
        }

        @Override
        public void onFinish() {
            btnSendcheckcode.setClickable(true);
            btnSendcheckcode.setText("获取验证码");
        }
    };
}