package com.cpigeon.cpigeonhelper.utils;

import android.content.Context;
import android.widget.ImageView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.home.bean.HomeAd;
import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Administrator on 2017/5/17.
 */

public class PicassoImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Picasso.with(context)
                .load( ((HomeAd) path).getAdImageUrl())
                .placeholder(R.mipmap.logos)
                .error(R.mipmap.logos)
                .into(imageView);
    }
}
