package com.cpigeon.cpigeonhelper.modular.xiehui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.xiehui.fragment.EditDialogFragment;
import com.cpigeon.cpigeonhelper.ui.CustomEmptyView;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/31.
 */

public class XieHuiInfoActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_xiehui_type_name)
    TextView tvXiehuiTypeName;
    @BindView(R.id.tv_xiehui_name)
    TextView tvXiehuiName;
    @BindView(R.id.ll_xiehui_name)
    LinearLayout llXiehuiName;
    @BindView(R.id.tv_xiehui_shotname)
    TextView tvXiehuiShotname;
    @BindView(R.id.ll_xiehui_shotname)
    LinearLayout llXiehuiShotname;
    @BindView(R.id.tv_xiehui_yuming)
    TextView tvXiehuiYuming;
    @BindView(R.id.ll_xiehui_yuming)
    LinearLayout llXiehuiYuming;
    @BindView(R.id.tv_xiehui_user)
    TextView tvXiehuiUser;
    @BindView(R.id.ll_xiehui_user)
    LinearLayout llXiehuiUser;
    @BindView(R.id.tv_xiehui_tel)
    TextView tvXiehuiTel;
    @BindView(R.id.ll_xiehui_tel)
    LinearLayout llXiehuiTel;
    @BindView(R.id.tv_xiehui_mail)
    TextView tvXiehuiMail;
    @BindView(R.id.ll_xiehui_mail)
    LinearLayout llXiehuiMail;
    @BindView(R.id.tv_xiehui_address)
    TextView tvXiehuiAddress;
    @BindView(R.id.ll_xiehui_address)
    LinearLayout llXiehuiAddress;
    @BindView(R.id.tv_xiehui_registertime)
    TextView tvXiehuiRegistertime;
    @BindView(R.id.ll_xiehui_registertime)
    LinearLayout llXiehuiRegistertime;
    @BindView(R.id.tv_xiehui_createtime)
    TextView tvXiehuiCreatetime;
    @BindView(R.id.ll_xiehui_createtime)
    LinearLayout llXiehuiCreatetime;
    @BindView(R.id.tv_xiehui_endtime)
    TextView tvXiehuiEndtime;
    @BindView(R.id.ll_xiehui_endtime)
    LinearLayout llXiehuiEndtime;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_xiehuiinfo;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("支付");
        this.setTopLeftButton(R.drawable.ic_back, () -> XieHuiInfoActivity.this.finish());
        this.setTopRightButton("完成", () -> Toast.makeText(this, "点击了右上角按钮！", Toast.LENGTH_LONG).show());
        loadData();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getApi()
                .getOrgInfo(AssociationData.getUserToken(),AssociationData.getUserId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orgInfoApiResponse -> {
                    if (orgInfoApiResponse.isStatus()) {
                        tvXiehuiName.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getName()) ? "无名称" : orgInfoApiResponse.getData().getName());
                        tvXiehuiAddress.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getAddr()) ? "无地点" : orgInfoApiResponse.getData().getAddr());
                        tvXiehuiShotname.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getShortname()) ? "无地点" : orgInfoApiResponse.getData().getShortname());
                        tvXiehuiUser.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getContacts()) ? "无联系人" : orgInfoApiResponse.getData().getContacts());
                        tvXiehuiTel.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getPhone()) ? "无联系电话" : orgInfoApiResponse.getData().getPhone());
                        tvXiehuiMail.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getEmail()) ? "无邮箱" : orgInfoApiResponse.getData().getEmail());
                        tvXiehuiYuming.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getDomain()) ? "无二级域名" : orgInfoApiResponse.getData().getDomain());
                        tvXiehuiRegistertime.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getRegistTime()) ? "无注册时间" : orgInfoApiResponse.getData().getRegistTime());
                        tvXiehuiCreatetime.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getSetupTime()) ? "无建立时间" : orgInfoApiResponse.getData().getSetupTime());
                        tvXiehuiEndtime.setText(TextUtils.isEmpty(orgInfoApiResponse.getData().getExpireTime()) ? "无到期时间" : orgInfoApiResponse.getData().getExpireTime());

                    }
                },throwable -> {
                    initEmptyView("连接失败，请您检查网络");
                });
    }

    @OnClick({R.id.ll_xiehui_name, R.id.ll_xiehui_shotname, R.id.ll_xiehui_yuming, R.id.ll_xiehui_user, R.id.ll_xiehui_tel, R.id.ll_xiehui_mail, R.id.ll_xiehui_address, R.id.ll_xiehui_registertime, R.id.ll_xiehui_createtime, R.id.ll_xiehui_endtime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_xiehui_name:
                break;
            case R.id.ll_xiehui_shotname:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_SHORTNAME,"修改简称").show(getSupportFragmentManager(),"提示");
                break;
            case R.id.ll_xiehui_yuming:
                break;
            case R.id.ll_xiehui_user:
                break;
            case R.id.ll_xiehui_tel:
                break;
            case R.id.ll_xiehui_mail:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_EMAIL,"修改邮箱").show(getSupportFragmentManager(),"提示");
                break;
            case R.id.ll_xiehui_address:
                EditDialogFragment.getInstance(EditDialogFragment.DIALOG_TYPE_ADDRESS,"修改地址").show(getSupportFragmentManager(),"提示");
                break;
            case R.id.ll_xiehui_registertime:
                break;
            case R.id.ll_xiehui_createtime:
                break;
            case R.id.ll_xiehui_endtime:
                break;
        }
    }

    public void initEmptyView(String tips) {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mCustomEmptyView.setEmptyImage(R.mipmap.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText(tips);
    }
}
