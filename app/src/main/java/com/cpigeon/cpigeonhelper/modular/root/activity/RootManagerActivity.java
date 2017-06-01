package com.cpigeon.cpigeonhelper.modular.root.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.root.adapter.PermissionAdapter;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserPermissions;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/5/27.
 */

public class RootManagerActivity extends ToolbarBaseActivity implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.iv_rootlist_usericon)
    CircleImageView ivRootlistUsericon;
    @BindView(R.id.tv_rootlist_username)
    TextView tvRootlistUsername;
    @BindView(R.id.tv_rootlist_usertel)
    TextView tvRootlistUsertel;
    @BindView(R.id.sw_root)
    Switch swRoot;
    @BindView(R.id.recyclerview_privilegelist)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_delete_root)
    Button btnDeleteRoot;
    private int enablestatus = 1;//当前启动的状态,默认值是1，表示已经启用了
    private int auuid;//被授权的用户的ID
    private StringBuilder stringBuilder;
    private int remove = 0;//是否删除，默认值为0,1则代表删除
    private long timestamp;
    private PermissionAdapter mAdapter;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_rootmanager;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("修改权限");
        this.setTopLeftButton(R.drawable.ic_back, () -> RootManagerActivity.this.finish());
        this.setTopRightButton("保存",
                R.drawable.ic_addroot, () -> setAuthUserPermission());
        loadData();
        initRecyclerView();
    }

    @Override
    public void loadData() {
        swRoot.setOnCheckedChangeListener(this);
        Intent intent = getIntent();
        auuid = intent.getIntExtra("auuid", 0);
        stringBuilder = new StringBuilder(",");
        RequestBody rqb = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("auuid", String.valueOf(auuid))
                .addFormDataPart("type", "ZGZS")
                .build();
        RetrofitHelper.getApi()
                .getAuthUserPermissions(AssociationData.getUserToken(), rqb)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userPermissionsApiResponse -> {
                    if (userPermissionsApiResponse.isStatus()) {

                        enablestatus = userPermissionsApiResponse.getData().isEnable() ? 0 : 1;


                        swRoot.setChecked(userPermissionsApiResponse.getData().isEnable());
                        Picasso.with(mContext)
                                .load(userPermissionsApiResponse.getData().getAuthUserInfo().getHeadimgUrl())
                                .into(ivRootlistUsericon);
                        tvRootlistUsername.setText(userPermissionsApiResponse.getData().getAuthUserInfo().getNickname());
                        tvRootlistUsertel.setText(userPermissionsApiResponse.getData().getAuthUserInfo().getPhone());
                        mAdapter.setNewData(userPermissionsApiResponse.getData().getPermissions());
                        for (UserPermissions.PermissionsBean bean : userPermissionsApiResponse.getData().getPermissions())
                        {
                            if (bean.isEnable())
                            {
                                stringBuilder.append(bean.getId());
                                stringBuilder.append(",");

                            }

                        }
                        Logger.e(stringBuilder.toString());
                    }
                }, throwable -> {

                });
    }

    @OnClick({R.id.sw_root, R.id.btn_delete_root})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sw_root:
                break;
            case R.id.btn_delete_root:
                if (remove == 0){
                    remove = 1;
                }
                break;
        }
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new PermissionAdapter(null);
        mAdapter.setmOnCheckedChangeListener((buttonView, isChecked) -> {
            int id = ((UserPermissions.PermissionsBean) buttonView.getTag()).getId();
            int index = stringBuilder.indexOf("," + String.valueOf(id) + ",");
            if (index > -1) {
                if (!isChecked) {
                    stringBuilder.delete(index + 1, index + 2 + String.valueOf(id).length());
                }
            } else {
                if (isChecked) {
                    stringBuilder.append(String.valueOf(id) + ",");
                }
            }
            Logger.e(stringBuilder.toString());
            Logger.e(stringBuilder.toString().substring(1, stringBuilder.length() > 2 ? stringBuilder.length() - 1 : stringBuilder.length()));
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setAuthUserPermission() {
        timestamp = System.currentTimeMillis() / 1000;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                .addFormDataPart("auuid", String.valueOf(auuid))
                .addFormDataPart("enable", String.valueOf(enablestatus))
                .addFormDataPart("per", stringBuilder.toString().substring(1, stringBuilder.length() > 2 ? stringBuilder.length() - 1 : stringBuilder.length()))
                .addFormDataPart("remove", String.valueOf(remove))
                .addFormDataPart("type", "ZGZS")
                .build();
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", String.valueOf(AssociationData.getUserId()));
        postParams.put("auuid", String.valueOf(auuid));
        postParams.put("enable", String.valueOf(enablestatus));
        postParams.put("per", stringBuilder.toString().substring(1, stringBuilder.length() > 2 ? stringBuilder.length() - 1 : stringBuilder.length()));
        postParams.put("remove",String.valueOf(remove));
        postParams.put("type","ZGZS");

        RetrofitHelper.getApi()
                .setAuthUserPermissions(AssociationData.getUserToken(),
                        requestBody,
                        timestamp, CommonUitls.getApiSign(timestamp, postParams))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rootManagerListApiResponse -> {
                            if (rootManagerListApiResponse.isStatus()) {
                                Logger.e("修改成功了");

                            } else {
                                Logger.e("修改失败了");
                            }
                            super.finish();
                        }, throwable -> {
                            Logger.e("错误代码" + throwable.getMessage());
                        }
                );
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_root:
                if (swRoot.isChecked()) {
                    enablestatus = 1;
                } else {
                    enablestatus = 0;
                }
                break;
        }
    }
}
