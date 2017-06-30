package com.cpigeon.cpigeonhelper.modular.usercenter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.BuildConfig;
import com.cpigeon.cpigeonhelper.MainActivity;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;
import com.cpigeon.cpigeonhelper.ui.button.CircularProgressButton;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.EncryptionTool;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.cpigeon.cpigeonhelper.common.db.AssociationData.DEV;
import static com.cpigeon.cpigeonhelper.common.db.AssociationData.DEV_ID;
import static com.cpigeon.cpigeonhelper.common.db.AssociationData.VER;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.isAccountValid;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.isPasswordValid;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.simulateErrorProgress;
import static com.cpigeon.cpigeonhelper.utils.CommonUitls.simulateSuccessProgress;

/**
 * Created by Administrator on 2017/5/25.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.civ_user_head_img)
    CircleImageView civUserHeadImg;
    @BindView(R.id.et_username)
    AutoCompleteTextView etUsername;
    @BindView(R.id.iv_pass_show)
    AppCompatImageView ivPassShow;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_action_login)
    CircularProgressButton btnActionLogin;
    @BindView(R.id.tv_forget_pwd)
    TextView mForPwd;
//    private String[] accounts = {"18782050317", "15378196774"};//设置常用手机号


    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        btnActionLogin.setIndeterminateProgressMode(true);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts);
//        etUsername.setAdapter(arrayAdapter);//输入至少两个字符才会提示
    }

    private void startLogin() {
        checkIsCorrect();
    }

    /**
     * 登录输入时候的正确判断
     */
    private void checkIsCorrect() {


        boolean cancel = false;

        /**
         * 检查用户名是否合法
         */
        if (TextUtils.isEmpty(etUsername.getText().toString()) || !isAccountValid(etUsername.getText().toString())) {

            cancel = true;
        }
        /**
         * 检查用户名和密码是否合格
         */
        if (!cancel && (TextUtils.isEmpty(etPassword.getText().toString()) || !isPasswordValid(etPassword.getText().toString()))) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(mContext, "输入不正确", Toast.LENGTH_SHORT).show();
        } else {
            if (btnActionLogin.getProgress() == 0) {
                login();
            } else {
                btnActionLogin.setProgress(0);
                login();
            }

        }
    }

    /**
     * 登录
     */
    private void login() {
        long timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("u", etUsername.getText().toString().trim());
        postParams.put("p", EncryptionTool.encryptAES(etPassword.getText().toString().trim()));
        postParams.put("t", "1");
        postParams.put("lt", "cpmanhel");
        postParams.put("devid", DEV_ID);
        postParams.put("dev", DEV);
        postParams.put("ver", VER);
        postParams.put("appid", BuildConfig.APPLICATION_ID);
        btnActionLogin.setProgress(50);
        RetrofitHelper.getApi()
                .login(postParams, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userBeanApiResponse -> {
                    if (userBeanApiResponse.getErrorCode() == 0) {
                        if (RealmUtils.getInstance().existUserInfo()) {
                            RealmUtils.getInstance().deleteUserInfo();
                        }
                        UserBean bean = new UserBean();
                        bean.setSign(TextUtils.isEmpty(userBeanApiResponse.getData().getSign())
                                ? "这家伙很懒，什么都没有留下" : userBeanApiResponse.getData().getSign());
                        bean.setHeadimgurl(userBeanApiResponse.getData().getHeadimgurl());
                        bean.setNickname(TextUtils.isEmpty(userBeanApiResponse.getData().getNickname())
                                ? userBeanApiResponse.getData().getUsername()
                                : userBeanApiResponse.getData().getNickname());
                        bean.setPassword(EncryptionTool.encryptAES(etPassword.getText().toString().trim()));
                        bean.setSltoken(userBeanApiResponse.getData().getSltoken());
                        bean.setUsername(userBeanApiResponse.getData().getUsername());
                        bean.setToken(userBeanApiResponse.getData().getToken());
                        bean.setUid(userBeanApiResponse.getData().getUid());
                        bean.setAccountType(userBeanApiResponse.getData().getAccountType());
                        bean.setAtype("admin");
                        bean.setType("xiehui");
                        RealmUtils.getInstance().insertUserInfo(bean);
                        simulateSuccessProgress(btnActionLogin);
                        Observable.timer(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .compose(bindToLifecycle())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aLong -> {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                });
                    } else {
                        simulateErrorProgress(btnActionLogin);
                    }
                }, throwable -> {
                    if(throwable instanceof SocketTimeoutException){
                        CommonUitls.showToast(this,"连接超时了");
                    }else if(throwable instanceof ConnectException){
                        CommonUitls.showToast(this,"连接失败了");
                    }else if(throwable instanceof RuntimeException){
                        CommonUitls.showToast(this,"发生了不可预期的错误："+throwable.getMessage());
                    }
                });

    }

    @Override
    public void initToolBar() {

    }

    /**
     * 监听密码的输入
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @OnTextChanged(R.id.et_password)
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @OnTextChanged(value = R.id.et_password, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @OnTextChanged(value = R.id.et_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
    }

    /**
     * 监听用户名的输入
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @OnTextChanged(R.id.et_username)
    public void onNameEditTextChanged(CharSequence s, int start, int before, int count) {

    }

    @OnTextChanged(value = R.id.et_username, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    public void beforeNameEditTextChanged(CharSequence s, int start, int count, int after) {

    }

    @OnTextChanged(value = R.id.et_username, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterNameEditTextChanged(Editable s) {
        if (isAccountValid(etUsername.getText().toString().trim())) {
            RetrofitHelper.getApi()
                    .getUserHeadImg(etUsername.getText().toString().trim())
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringApiResponse -> {
                        Picasso.with(mContext).load(TextUtils.isEmpty(stringApiResponse.getData()) ? null : stringApiResponse.getData())
                                .placeholder(R.mipmap.logos)
                                .error(R.mipmap.logos)
                                .into(civUserHeadImg);
                    });
        } else if (etUsername.getText().toString().trim().length() < 11 || !isAccountValid(etUsername.getText().toString())) {
            Picasso.with(mContext).load(R.mipmap.logos).into(civUserHeadImg);
        }

    }

    @OnEditorAction(R.id.et_password)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND)
        {
            startLogin();
            hideSoftInput();
        }
        return true;
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }


    @Override
    public void onBackPressed() {
        AppManager.getAppManager().AppExit();
    }


    @OnClick({R.id.iv_pass_show, R.id.btn_action_login,R.id.tv_forget_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pass_show:
                if (view.getTag() == null || (boolean) view.getTag()) {
                    view.setTag(false);
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
                    ((AppCompatImageView) view).setImageResource(R.drawable.ic_visibility_on);
                } else {
                    view.setTag(true);
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
                    ((AppCompatImageView) view).setImageResource(R.drawable.ic_visibility_on);
                }
                break;
            case R.id.btn_action_login:
                startLogin();
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
                break;
        }
    }

}
