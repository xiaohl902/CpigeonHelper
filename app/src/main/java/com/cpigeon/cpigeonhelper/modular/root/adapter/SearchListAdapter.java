package com.cpigeon.cpigeonhelper.modular.root.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserInfoByTelBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/5/27.
 */

public class SearchListAdapter extends BaseQuickAdapter<UserInfoByTelBean,BaseViewHolder> {
    public SearchListAdapter(List<UserInfoByTelBean> data) {
        super(R.layout.layout_item,data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, UserInfoByTelBean userInfoByTelBean) {
        baseViewHolder.setText(R.id.tv_rootlist_username, TextUtils.isEmpty(userInfoByTelBean.getNickname())?userInfoByTelBean.getName():userInfoByTelBean.getNickname());
        baseViewHolder.setText(R.id.tv_rootlist_usertel,userInfoByTelBean.getPhone());
        Picasso.with(mContext)
                .load(userInfoByTelBean.getHeadimgUrl())
                .placeholder(R.mipmap.logos)
                .error(R.mipmap.logos)
                .into((CircleImageView) baseViewHolder.getView(R.id.iv_rootlist_usericon));
    }
}
