package com.cpigeon.cpigeonhelper.modular.flyarea.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;

import java.util.List;

/**
 *
 * Created by Administrator on 2017/7/14.
 */

public class SimpleFlyingAreaAdapter extends BaseQuickAdapter<FlyingArea, BaseViewHolder> {
    public SimpleFlyingAreaAdapter(List<FlyingArea> data) {
        super(R.layout.item_simpleflyingarea,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FlyingArea item) {
        helper.setText(R.id.tv_flyingarea_place, item.getArea());
        helper.setText(R.id.tv_flyingarea_longitude, String.format("%.6f", item.getLatitude()) + "/" + String.format("%.6f", item.getLongitude()));
    }
}
