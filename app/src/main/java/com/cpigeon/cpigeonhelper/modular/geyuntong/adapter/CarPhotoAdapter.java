package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.RaceImageOrVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 鸽运通照片
 * Created by Administrator on 2017/6/16.
 */

public class CarPhotoAdapter extends BaseQuickAdapter<RaceImageOrVideo,BaseViewHolder>{
    public CarPhotoAdapter(List<RaceImageOrVideo> data) {
        super(R.layout.item_car_photo,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RaceImageOrVideo item) {
        Picasso.with(mContext)
                .load(TextUtils.isEmpty(item.getThumburl())?item.getUrl():item.getThumburl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into((ImageView) helper.getView(R.id.iv_car_photo_img));
        helper.setText(R.id.tv_car_photo_status,item.getTag());
    }
}
