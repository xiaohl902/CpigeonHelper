package com.cpigeon.cpigeonhelper.modular.setting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.setting.bean.OperatorLog;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class OperatorAdapter extends BaseQuickAdapter<OperatorLog,BaseViewHolder> {
    public OperatorAdapter(List<OperatorLog> data) {
        super(R.layout.item_operatorlogs,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OperatorLog item) {
        helper.setText(R.id.tv_operator_ip,"IP："+item.getIp());
        helper.setText(R.id.tv_operator_time,item.getTime());
        helper.setText(R.id.tv_operator_operatorlog,"操作内容："+item.getContent());
        helper.setText(R.id.tv_operator_userid,"用户ID："+item.getUserInfo().getName());
    }
}
