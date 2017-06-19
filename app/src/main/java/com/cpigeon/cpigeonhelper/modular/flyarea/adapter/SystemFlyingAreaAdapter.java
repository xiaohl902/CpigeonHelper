package com.cpigeon.cpigeonhelper.modular.flyarea.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.flyarea.fragment.bean.FlyingArea;

import java.util.List;

/**
 * 我的司放地的适配器
 * Created by Administrator on 2017/6/14.
 */

public class SystemFlyingAreaAdapter extends BaseQuickAdapter<FlyingArea, BaseViewHolder> {
    public SystemFlyingAreaAdapter(List<FlyingArea> data) {
        super(R.layout.item_systemflyingarea, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FlyingArea item) {
        helper.setText(R.id.tv_system_flyingarea_place, item.getArea());
        helper.setText(R.id.tv_system_flyingarea_longitude, String.format("%.6f", item.getLatitude()) + "/" + String.format("%.6f", item.getLongitude()));

    }
}
