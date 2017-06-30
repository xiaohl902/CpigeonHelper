package com.cpigeon.cpigeonhelper.modular.order.adapter;

import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.order.bean.Order;
import com.cpigeon.cpigeonhelper.modular.order.bean.OrderList;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */

public class OrderListAdapter extends BaseQuickAdapter<OrderList, BaseViewHolder> {
    public OrderListAdapter(List<OrderList> data) {
        super(R.layout.item_user_order_info, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderList item) {
        helper.setText(R.id.tv_item_time, item.getTime());
        helper.setText(R.id.tv_item_name, item.getItem());
        helper.setText(R.id.tv_item_order_number, String.format("订单编号：%s", item.getNumber()));
        int orderStatusColor = Color.parseColor("#8B8A8A");//默认颜色
        switch (item.getStatusname()) {
            case "交易完成":
                orderStatusColor = mContext.getResources().getColor(R.color.colorGreen);
                break;
            case "交易过期":
                orderStatusColor = mContext.getResources().getColor(R.color.colorDarkGrey);
                break;
            case "待支付":
                orderStatusColor = mContext.getResources().getColor(R.color.colorRed);
                break;
        }
        helper.setText(R.id.tv_item_order_status, item.getStatusname());
        helper.setTextColor(R.id.tv_item_order_status, orderStatusColor);
        helper.setText(R.id.tv_item_order_price, item.getPrice() > 0 && item.getScores() > 0 ?
                String.format("%.2f元/%d鸽币", item.getPrice(), item.getScores()) :
                item.getPrice() > 0 ? String.format("%.2f元", item.getPrice()) : String.format("%d鸽币", item.getScores()));

        helper.setText(R.id.tv_item_order_payway, String.format("%s", item.getPayway().replace("积分", "鸽币")));
        helper.getView(R.id.tv_item_order_payway).setVisibility(item.getIspay() == 0 ? View.VISIBLE : View.GONE);
    }
}
