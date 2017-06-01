package com.cpigeon.cpigeonhelper.modular.root.adapter;

import android.widget.CompoundButton;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.root.bean.UserPermissions;

import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */

public class PermissionAdapter extends BaseQuickAdapter<UserPermissions.PermissionsBean,BaseViewHolder>{
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    public void setmOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    public PermissionAdapter(List<UserPermissions.PermissionsBean> data) {
        super(R.layout.layout_permission,data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, UserPermissions.PermissionsBean permissionsBean) {
        baseViewHolder.setText(R.id.sw_root_status,permissionsBean.getName());
        ((Switch)baseViewHolder.getView(R.id.sw_root_status)).setChecked(permissionsBean.isEnable());
        ((Switch)baseViewHolder.getView(R.id.sw_root_status)).setTag(permissionsBean);
        ((Switch)baseViewHolder.getView(R.id.sw_root_status)).setOnCheckedChangeListener(mOnCheckedChangeListener);
    }


}
