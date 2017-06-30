package com.cpigeon.cpigeonhelper.modular.order.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.order.bean.PackageInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class PackageInfoAdapter extends BaseQuickAdapter<PackageInfo,BaseViewHolder> {
    public PackageInfoAdapter(List<PackageInfo> data) {
        super(R.layout.item_geyuntongservice,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PackageInfo item) {
        helper.setText(R.id.tv_gytservice_name,item.getPackageName());
        helper.setText(R.id.tv_gytservice_details,item.getBrief());
        helper.setText(R.id.tv_gytservice_price,String.valueOf(item.getPrice())+"元/"+item.getUnitname());
        switch (item.getPackageName())
        {
            case "普通用户":
                ((ConstraintLayout)helper.getView(R.id.constraintlayout)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_normal));
                ((TextView)helper.getView(R.id.tv_open_gytservice)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_normal));
                break;
            case "VIP用户":
                ((ConstraintLayout)helper.getView(R.id.constraintlayout)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_vip));
                ((TextView)helper.getView(R.id.tv_open_gytservice)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_btn_vip));
                break;
            case "SVIP用户":
                ((ConstraintLayout)helper.getView(R.id.constraintlayout)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_svip));
                ((TextView)helper.getView(R.id.tv_open_gytservice)).setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_btn_svip));
                break;
        }

        helper.addOnClickListener(R.id.tv_open_gytservice);
    }
}
