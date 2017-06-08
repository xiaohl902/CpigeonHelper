package com.cpigeon.cpigeonhelper.modular.root.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.root.bean.RootList;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/5/23.
 */

public class RootListAdapter extends BaseQuickAdapter<RootList, BaseViewHolder> {


    public RootListAdapter(List<RootList> data) {
        super(R.layout.layout_item, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, RootList rootList) {
        baseViewHolder.setText(R.id.tv_rootlist_username, TextUtils.isEmpty(rootList.getAuthUserInfo().getNickname())?rootList.getAuthUserInfo().getName():rootList.getAuthUserInfo().getNickname());
        baseViewHolder.setText(R.id.tv_rootlist_usertel,rootList.getAuthUserInfo().getPhone());
        baseViewHolder.setVisible(R.id.tv_rootlist_status,true);
        if (rootList.isEnable())
        {
            baseViewHolder.setText(R.id.tv_rootlist_status,"已启动");
            baseViewHolder.setTextColor(R.id.tv_rootlist_status,mContext.getResources().getColor(R.color.colorGreen));
        }else {
            baseViewHolder.setText(R.id.tv_rootlist_status,"已停用");
            baseViewHolder.setTextColor(R.id.tv_rootlist_status,mContext.getResources().getColor(R.color.colorLayoutSplitLineGray));
        }
        Picasso.with(mContext).load(rootList.getAuthUserInfo().getHeadimgUrl())
                .placeholder(R.mipmap.logos)
                .error(R.mipmap.logos)
                .into((CircleImageView) baseViewHolder.getView(R.id.iv_rootlist_usericon));
    }
}
