package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.ui.button.CircularProgressButton;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tech.michaelx.authcode.AuthCode;
import tech.michaelx.authcode.CodeConfig;

import static com.cpigeon.cpigeonhelper.utils.CommonUitls.isAccountValid;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.showToast;

/**
 * Created by Administrator on 2017/7/10.
 */

public class PayPwdActivity extends ToolbarBaseActivity {

    @BindView(R.id.iv_icon_phonernumber)
    AppCompatImageView ivIconPhonernumber;
    @BindView(R.id.et_phonenumber)
    AutoCompleteTextView etPhonenumber;
    @BindView(R.id.iv_icon_checkcode)
    AppCompatImageView ivIconCheckcode;
    @BindView(R.id.et_checkcode)
    EditText etCheckcode;
    @BindView(R.id.btn_sendcheckcode)
    Button btnSendcheckcode;
    @BindView(R.id.iv_icon_newpwd)
    AppCompatImageView ivIconNewpwd;
    @BindView(R.id.et_newpwd)
    AutoCompleteTextView etNewpwd;
    @BindView(R.id.btn_confim)
    CircularProgressButton btnConfim;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pay_pwd;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);//最后一个参数代表了透明度，0位全部透明
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_sendcheckcode, R.id.btn_confim})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sendcheckcode:
                if (TextUtils.isEmpty(etPhonenumber.getText().toString().trim()) || !isAccountValid(etPhonenumber.getText().toString().trim())) {
                    CommonUitls.showToast(this, "输入的手机号错误，请重新输入");
                } else {
                    CheckCode();
                }
                break;
            case R.id.btn_confim:
                if (btnConfim.getProgress() == 0) {
                    if (!TextUtils.isEmpty(etPhonenumber.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etCheckcode.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etNewpwd.getText().toString().trim())
                           ) {
                        changePassWord();
                    } else {
                        showToast(this, "您的输入不正确，请检查重新输入");
                    }

                } else {
                    btnConfim.setProgress(0);
                    if (!TextUtils.isEmpty(etPhonenumber.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etCheckcode.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etNewpwd.getText().toString().trim())
                            ) {
                        changePassWord();
                    } else {
                        showToast(this, "您的输入不正确，请检查重新输入");
                    }
                }
                break;
        }
    }

    private void changePassWord() {

        long timestamp = System.currentTimeMillis() / 1000;
        RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("u", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("p", EncryptionTool.encryptAES(etNewpwd.getText().toString().trim()))
                .addFormDataPart("y", etCheckcode.getText().toString().trim())
                .addFormDataPart("t", etPhonenumber.getText().toString().trim())
                .build();

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("u", String.valueOf(AssociationData.getUserId()));
        postParams.put("p", EncryptionTool.encryptAES(etNewpwd.getText().toString().trim()));
        postParams.put("y", etCheckcode.getText().toString().trim());
        postParams.put("t", etPhonenumber.getText().toString().trim());

        RetrofitHelper.getApi()
                .setUserPayPwd(AssociationData.getUserToken(),mRequestBody,timestamp,CommonUitls.getApiSign(timestamp,postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiResponse -> {
                    if (apiResponse.getErrorCode() == 0)
                    {
                       new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                               .setTitleText("修改成功")
                               .setConfirmText("好的")
                               .setConfirmClickListener(sweetAlertDialog -> {
                                   sweetAlertDialog.dismiss();
                                   finish();
                               })
                               .show();
                    }else {
                        CommonUitls.showToast(this,apiResponse.getMsg());
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时");
                    }else if (throwable instanceof ConnectException)
                    {
                        CommonUitls.showToast(this,"连接失败，请检查连接");
                    }else if (throwable instanceof RuntimeException)
                    {
                        CommonUitls.showToast(this,"发生了不可预期的错误:"+throwable.getMessage());
                    }
                });
    }

    private void CheckCode() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS)
                .compose(bindToLifecycle())
                .subscribe(granted -> {
                    if (granted) {
                        sendCheckCode();
                    } else {
                        sendCheckCode();
                    }
                });
    }

    private void sendCheckCode() {
        long timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("p", etPhonenumber.getText().toString().trim());
        postParams.put("t", "3");
        RetrofitHelper.getApi()
                .sendVerifyCode(postParams, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkCodeApiResponse -> {
                    if (checkCodeApiResponse.getErrorCode() == 0) {
                        timer.start();
                        CodeConfig config = new CodeConfig.Builder()
                                .codeLength(4) // 设置验证码长度
                                .smsFromStart(106) // 设置验证码发送号码前几位数字
                                .smsBodyStartWith("温馨提醒：") // 设置验证码短信开头文字
                                .smsBodyContains("验证码：") // 设置验证码短信内容包含文字
                                .build();
                        AuthCode.getInstance().with(PayPwdActivity.this).config(config).into((EditText) findViewById(R.id.et_checkcode));
                    } else {
                        CommonUitls.showToast(mContext, "获取失败");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时");
                    }else if (throwable instanceof ConnectException)
                    {
                        CommonUitls.showToast(this,"无法连接到服务器");
                    }else if (throwable instanceof RuntimeException){
                        CommonUitls.showToast(this,"发生了不可预知的错误"+throwable.getMessage());
                    }
                });
    }


    private CountDownTimer timer = new CountDownTimer(50000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

            btnSendcheckcode.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
            btnSendcheckcode.setClickable(false);
        }

        @Override
        public void onFinish() {
            btnSendcheckcode.setClickable(true);
            btnSendcheckcode.setText("获取验证码");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


}
