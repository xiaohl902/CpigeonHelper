package com.cpigeon.cpigeonhelper.modular.root.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
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

                if (userInfoByTelBean.getAuthUid() == 0)
                {
                    baseViewHolder.setText(R.id.tv_rootlist_status,"未启用");
                    baseViewHolder.setTextColor(R.id.tv_rootlist_status,mContext.getResources().getColor(R.color.gray_btn_bg_color));
                }else if (userInfoByTelBean.getAuthUid() == AssociationData.getUserId())
                {
                    baseViewHolder.setText(R.id.tv_rootlist_status,"已添加");
                    baseViewHolder.setTextColor(R.id.tv_rootlist_status,mContext.getResources().getColor(R.color.colorGreen));

                }else if (userInfoByTelBean.getAuthUid() != AssociationData.getUserId()){
                    baseViewHolder.setText(R.id.tv_rootlist_status,"占用中");
                    baseViewHolder.setTextColor(R.id.tv_rootlist_status,mContext.getResources().getColor(R.color.colorPrimary));

                }




        }

}
