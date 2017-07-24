package com.cpigeon.cpigeonhelper.modular.xiehui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.xiehui.bean.OrgInfo;
import com.cpigeon.cpigeonhelper.modular.xiehui.fragment.MyDialogFragment;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * 协会信息
 * Created by Administrator on 2017/5/31.
 */

public class XieHuiInfoActivity extends ToolbarBaseActivity {


    @BindView(R.id.tv_xiehui_name)
    TextView tvXiehuiName;
    @BindView(R.id.tv_xiehui_shotname)
    EditText tvXiehuiShotname;
    @BindView(R.id.tv_xiehui_yuming)
    EditText tvXiehuiYuming;
    @BindView(R.id.tv_xiehui_user)
    EditText tvXiehuiUser;
    @BindView(R.id.tv_xiehui_tel)
    EditText tvXiehuiTel;
    @BindView(R.id.tv_xiehui_mail)
    EditText tvXiehuiMail;
    @BindView(R.id.tv_xiehui_address)
    EditText tvXiehuiAddress;
    @BindView(R.id.tv_xiehui_registertime)
    TextView tvXiehuiRegistertime;
    @BindView(R.id.tv_xiehui_createtime)
    TextView tvXiehuiCreatetime;
    @BindView(R.id.tv_xiehui_endtime)
    TextView tvXiehuiEndtime;
    @BindView(R.id.ll_xiehui_name)
    LinearLayout llXiehuiNameLayout;
    @BindView(R.id.ll_xiehui_createtime)
    LinearLayout llXiehuiCreatetimeLayout;

    @Override
    protected void swipeBack() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_xiehuiinfo;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("信息修改");
        this.setTopLeftButton(R.drawable.ic_back, this::finish);
        this.setTopRightButton("保存", this::saveInfo);
        loadData();
    }

    @Override
    public void loadData() {

        if (RealmUtils.getInstance().queryOrgInfo(AssociationData.getUserId())!=null)
        {
            RealmResults<OrgInfo> orgInfos = RealmUtils.getInstance().queryOrgInfo(AssociationData.getUserId());
            tvXiehuiName.setText(orgInfos.get(0).getName());

            tvXiehuiAddress.setText(orgInfos.get(0).getAddr());

            tvXiehuiShotname.setText(orgInfos.get(0).getShortname());

            tvXiehuiUser.setText(orgInfos.get(0).getContacts());

            tvXiehuiTel.setText(orgInfos.get(0).getPhone());

            tvXiehuiMail.setText(orgInfos.get(0).getEmail());

            tvXiehuiYuming.setText(orgInfos.get(0).getDomain());

            tvXiehuiRegistertime.setText(orgInfos.get(0).getRegistTime());

            tvXiehuiCreatetime.setText(orgInfos.get(0).getSetupTime());

            tvXiehuiEndtime.setText(orgInfos.get(0).getExpireTime());
        }else{
            RetrofitHelper.getApi()
                    .getOrgInfo(AssociationData.getUserToken(), AssociationData.getUserId())
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(orgInfoApiResponse -> {
                        if (orgInfoApiResponse.getErrorCode() == 0) {
                            tvXiehuiName.setText(orgInfoApiResponse.getData().getName());

                            tvXiehuiAddress.setText(orgInfoApiResponse.getData().getAddr());

                            tvXiehuiShotname.setText(orgInfoApiResponse.getData().getShortname());

                            tvXiehuiUser.setText(orgInfoApiResponse.getData().getContacts());

                            tvXiehuiTel.setText(orgInfoApiResponse.getData().getPhone());

                            tvXiehuiMail.setText(orgInfoApiResponse.getData().getEmail());

                            tvXiehuiYuming.setText(orgInfoApiResponse.getData().getDomain());

                            tvXiehuiRegistertime.setText(orgInfoApiResponse.getData().getRegistTime());

                            tvXiehuiCreatetime.setText(orgInfoApiResponse.getData().getSetupTime());

                            tvXiehuiEndtime.setText(orgInfoApiResponse.getData().getExpireTime());
                        } else {
                            CommonUitls.showToast(this, orgInfoApiResponse.getMsg());
                        }
                    }, throwable -> {
                        if (throwable instanceof SocketTimeoutException) {
                            CommonUitls.showToast(this, "获取失败，连接超时");
                        } else if (throwable instanceof ConnectException) {
                            CommonUitls.showToast(this, "获取失败，连接异常");
                        } else if (throwable instanceof RuntimeException) {
                            CommonUitls.showToast(this, "获取失败");
                        }
                    });
        }

    }


    @OnClick({R.id.ll_xiehui_name, R.id.tv_xiehui_shotname, R.id.tv_xiehui_yuming, R.id.tv_xiehui_user, R.id.tv_xiehui_tel, R.id.tv_xiehui_mail, R.id.tv_xiehui_address, R.id.ll_xiehui_createtime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_xiehui_name:
                startActivity(new Intent(XieHuiInfoActivity.this,ChangeNameActivity.class));
                break;
            case R.id.tv_xiehui_shotname:
                tvXiehuiShotname.setCursorVisible(true);
                tvXiehuiShotname.setFocusable(true);
                tvXiehuiShotname.setFocusableInTouchMode(true);
                tvXiehuiShotname.requestFocus();
                break;
            case R.id.tv_xiehui_yuming:
                tvXiehuiYuming.setCursorVisible(true);
                tvXiehuiYuming.setFocusable(true);
                tvXiehuiYuming.setFocusableInTouchMode(true);
                tvXiehuiYuming.requestFocus();
                break;
            case R.id.tv_xiehui_user:
                tvXiehuiUser.setCursorVisible(true);
                tvXiehuiUser.setFocusable(true);
                tvXiehuiUser.setFocusableInTouchMode(true);
                tvXiehuiUser.requestFocus();
                break;
            case R.id.tv_xiehui_tel:
                tvXiehuiTel.setCursorVisible(true);
                tvXiehuiTel.setFocusable(true);
                tvXiehuiTel.setFocusableInTouchMode(true);
                tvXiehuiTel.requestFocus();
                break;
            case R.id.tv_xiehui_mail:
                tvXiehuiMail.setCursorVisible(true);
                tvXiehuiMail.setFocusable(true);
                tvXiehuiMail.setFocusableInTouchMode(true);
                tvXiehuiMail.requestFocus();
                break;
            case R.id.tv_xiehui_address:
                tvXiehuiAddress.setCursorVisible(true);
                tvXiehuiAddress.setFocusable(true);
                tvXiehuiAddress.setFocusableInTouchMode(true);
                tvXiehuiAddress.requestFocus();
                break;
            case R.id.ll_xiehui_createtime:
                MyDialogFragment.getInstance(MyDialogFragment.DIALOG_TYPE_DATE).show(getFragmentManager(), "提示");
                break;
        }
    }

    /**
     * 保存修改
     */
    public void saveInfo() {
        long timestamp = System.currentTimeMillis() / 1000;
        if (TextUtils.isEmpty(tvXiehuiShotname.getText().toString().trim()) ||
                TextUtils.isEmpty(tvXiehuiAddress.getText().toString().trim()) ||
                TextUtils.isEmpty(tvXiehuiTel.getText().toString().trim()) ||
                TextUtils.isEmpty(tvXiehuiMail.getText().toString().trim()) ||
                TextUtils.isEmpty(tvXiehuiUser.getText().toString().trim()) ||
                TextUtils.isEmpty(tvXiehuiRegistertime.getText().toString().trim())) {
            CommonUitls.showToast(this, "修改失败，请检查输入是否完整");
        } else {
            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("type", AssociationData.getUserType());
            postParams.put("contacts", tvXiehuiUser.getText().toString().trim());
            postParams.put("phone", tvXiehuiTel.getText().toString().trim());
            postParams.put("email", tvXiehuiMail.getText().toString().trim());
            postParams.put("addr", tvXiehuiAddress.getText().toString().trim());
            postParams.put("sname", tvXiehuiShotname.getText().toString().trim());
            postParams.put("setuptime", tvXiehuiCreatetime.getText().toString().trim());
            RetrofitHelper.getApi()
                    .setOrgInfo(AssociationData.getUserToken(), postParams, timestamp,
                            CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(orgInfoApiResponse -> {
                        if (orgInfoApiResponse.getErrorCode() == 0) {
                            RealmUtils.getInstance().deleteXieHuiInfo();
                            RealmUtils.getInstance().insertXieHuiInfo(orgInfoApiResponse.getData());
                            CommonUitls.showToast(this, "修改成功");
                            this.finish();
                        } else {
                            CommonUitls.showToast(this, orgInfoApiResponse.getMsg());
                        }
                    }, throwable -> {
                        if (throwable instanceof SocketTimeoutException) {
                            CommonUitls.showToast(this, "修改失败，连接超时");
                        } else if (throwable instanceof ConnectException) {
                            CommonUitls.showToast(this, "修改失败，连接失败");
                        } else if (throwable instanceof RuntimeException) {
                            CommonUitls.showToast(this, "修改失败");
                        }
                    });
        }
    }

}
