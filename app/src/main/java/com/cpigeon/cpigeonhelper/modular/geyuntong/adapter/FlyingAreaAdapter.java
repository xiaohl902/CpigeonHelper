package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.FlyingArea;

import java.util.List;

/**
 * 我的司放地的适配器
 * Created by Administrator on 2017/6/14.
 */

public class FlyingAreaAdapter extends BaseQuickAdapter<FlyingArea,BaseViewHolder> {
    public FlyingAreaAdapter(List<FlyingArea> data) {
        super(R.layout.item_myflyingarea,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FlyingArea item) {
        helper.setText(R.id.tv_flyingarea_place,item.getArea());
        helper.setText(R.id.tv_flyingarea_latitude,String.valueOf(item.getLatitude()));
        helper.setText(R.id.tv_flyingarea_longitude,String.valueOf(item.getLongitude()));
        helper.setText(R.id.tv_flyingarea_discription,item.getAlias());
        helper.setText(R.id.tv_flyingarea_number,String.valueOf(item.getNumber()));
    }
}
