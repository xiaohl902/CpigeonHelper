package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;

import java.util.List;

/**
 *
 * Created by Administrator on 2017/6/29.
 *
 */

public class HomeGYTAdapter extends BaseQuickAdapter<GeYunTong,BaseViewHolder>{

    public HomeGYTAdapter(List<GeYunTong> data) {
        super(R.layout.item_gyt,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GeYunTong item) {
        helper.setText(R.id.tv_geyuntong_race,item.getRaceName());
        helper.setTextColor(R.id.tv_geyuntong_race,ContextCompat.getColor(mContext,R.color.colorRed));
        helper.setText(R.id.tv_geyuntong_race_status,item.getState());
        switch (item.getStateCode())
        {
            case 0:
                helper.setTextColor(R.id.tv_geyuntong_race_status, ContextCompat.getColor(mContext,R.color.colorRed));
                break;
            case 1:
                helper.setTextColor(R.id.tv_geyuntong_race_status, ContextCompat.getColor(mContext,R.color.colorBule));
                break;
            case 2:
                helper.setTextColor(R.id.tv_geyuntong_race_status, ContextCompat.getColor(mContext,R.color.colorGary));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
