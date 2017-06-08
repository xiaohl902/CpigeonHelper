package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GeYunTong;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class GeYunTongListAdapter extends BaseQuickAdapter<GeYunTong,BaseViewHolder> {
    public GeYunTongListAdapter(List<GeYunTong> data) {
        super(R.layout.item_geyuntong,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GeYunTong item) {
        helper.setText(R.id.tv_geyuntong_name,item.getRaceName());
        helper.setText(R.id.tv_geyuntong_place,"司放地点："+item.getFlyingArea());
        helper.setText(R.id.tv_geyuntong_time,"司放时间："+item.getCreateTime());
        helper.setText(R.id.tv_geyuntong_status,item.getState());
        helper.setText(R.id.tv_geyuntong_lati,"经度："+item.getLongitude()+" 纬度："+item.getLatitude());
        Picasso.with(mContext)
                .load(TextUtils.isEmpty(item.getRaceImage())?"1":item.getRaceImage())
                .placeholder(R.drawable.default_geyuntong)
                .error(R.drawable.default_geyuntong)
                .into((ImageView) helper.getView(R.id.iv_geyuntong_img));
        switch (item.getState())
        {
            case "监控中":
                helper.setTextColor(R.id.tv_geyuntong_status,mContext.getResources().getColor(R.color.colorPrimary));
                break;
            case "未开始监控":
                helper.setTextColor(R.id.tv_geyuntong_status,mContext.getResources().getColor(R.color.colorGreen));
                break;
        }
    }
}
