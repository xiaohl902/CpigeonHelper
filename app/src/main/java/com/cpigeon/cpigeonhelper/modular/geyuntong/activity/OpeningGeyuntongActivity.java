package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.PackageInfoAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PackageInfo;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/26.
 */

public class OpeningGeyuntongActivity extends ToolbarBaseActivity {


    @BindView(R.id.tv_xiehui_name)
    TextView tvXiehuiName;
    @BindView(R.id.tv_Price)
    TextView tvPrice;
    @BindView(R.id.tv_originalPrice)
    TextView tvOriginalPrice;
    @BindView(R.id.btn_confim)
    Button btnConfim;
    @BindView(R.id.iv_account_name)
    ImageView ivAccountName;
    @BindView(R.id.ic_account_type)
    ImageView icAccountType;
    @BindView(R.id.recyclerview_packageinfo)
    RecyclerView recyclerviewPackageinfo;
    private PackageInfoAdapter mAdapter;
    private List<PackageInfo> packageInfoList;
    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_geyuntong;
    }

    @Override
    protected void setStatusBar() {
        mColor = mContext.getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor,0);//最后一个参数代表了透明度，0位全部透明
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.setTitle("支付");
        this.setTopLeftButton(R.drawable.ic_back, () -> OpeningGeyuntongActivity.this.finish());
        this.setTopRightButton("完成", () -> Toast.makeText(this, "点击了右上角按钮！", Toast.LENGTH_LONG).show());
        loadData();
        this.initRecyclerView();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getApi()
                .getServicePackageInfo("gytopen")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(packageInfoApiResponse -> {
                    if (packageInfoApiResponse.isStatus()) {
                        tvXiehuiName.setText(packageInfoApiResponse.getData().get(0).getId() + "");
                        tvPrice.setText(packageInfoApiResponse.getData().get(0).getOpentime() + "");
                        if (packageInfoApiResponse.getData()!=null && packageInfoApiResponse.getData().size()>=1){
                            packageInfoList = new ArrayList<PackageInfo>();
                            for (PackageInfo packageInfo :packageInfoApiResponse.getData())
                            {
                                packageInfoList.add(packageInfo);
                            }
                            Logger.e(packageInfoList.get(0).getName());
                            mAdapter.setNewData(packageInfoList);
                        }
                        tvXiehuiName.setText(packageInfoApiResponse.getData().get(0).getExpiretime());
                    }
                }, throwable -> {

                });
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new PackageInfoAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            PackageInfo packageInfo = (PackageInfo) adapter.getData().get(position);
            tvPrice.setText(String.valueOf(packageInfo.getPrice()));
            tvOriginalPrice.setText(String.valueOf(packageInfo.getOriginalPrice()));
        });
        recyclerviewPackageinfo.setAdapter(mAdapter);
        recyclerviewPackageinfo.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
    }


    @OnClick(R.id.btn_confim)
    public void onViewClicked() {
    }
}
