package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.RaceImageOrVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 鸽运通视频
 * Created by Administrator on 2017/6/16.
 */

public class CarVideoAdapter extends BaseQuickAdapter<RaceImageOrVideo,BaseViewHolder>{
    public CarVideoAdapter(List<RaceImageOrVideo> data) {
        super(R.layout.item_car_video,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RaceImageOrVideo item) {
        ((JCVideoPlayerStandard)helper.getView(R.id.jcvideo))
                .setUp(item.getUrl(),JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, item.getTag());
        Picasso.with(mContext).load(item.getThumburl())
                .into(((JCVideoPlayerStandard)helper.getView(R.id.jcvideo)).thumbImageView);
    }
}
