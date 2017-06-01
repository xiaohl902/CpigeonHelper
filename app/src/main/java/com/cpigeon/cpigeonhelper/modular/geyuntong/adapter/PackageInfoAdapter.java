package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.PackageInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class PackageInfoAdapter extends BaseQuickAdapter<PackageInfo,BaseViewHolder> {
    public PackageInfoAdapter(List<PackageInfo> data) {
        super(R.layout.item_packageinfo,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PackageInfo item) {
        helper.setText(R.id.tv_service_item,item.getPackageName());

        helper.getView(R.id.tv_service_item).setTag(item);
    }
}
