package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

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
        helper.setText(R.id.tv_geyuntong_place,item.getFlyingArea());
        helper.setText(R.id.tv_geyuntong_time,item.getCreateTime());
        helper.setText(R.id.tv_geyuntong_status,item.getState());
    }
}
