package com.trade.eight.tools.trade;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;

/**
 * Created by fangzhu on 16/8/12.
 * 1-10手的view
 */
public class TradeCountUtil {

    private BaseActivity context;
    private View rootView;
    View sizeOtherView;
    LinearLayout sizeOther01, sizeOther02;
    TextView selectedView;
    int count = 1;//默认
    boolean isUp;

    public TradeCountUtil(BaseActivity context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        initView();
    }

    public void initView() {
        sizeOtherView = rootView.findViewById(R.id.sizeOtherView);
        sizeOther01 = (LinearLayout) rootView.findViewById(R.id.sizeOther01);
        sizeOther02 = (LinearLayout) rootView.findViewById(R.id.sizeOther02);
        int index = 1;
        for (int i = 0; i < sizeOther01.getChildCount(); i++) {
            View v = sizeOther01.getChildAt(i);
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                textView.setText(index + "手");

                if (isUp) {
                    textView.setBackgroundResource(R.drawable.bg_item_quick_trade);
                    textView.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
                } else {
                    textView.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
                    textView.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
                }

                if (selectedView == null)
                    selectedView = textView;
                if (count == index)
                    selectedView = textView;
                else
                    textView.setSelected(false);
                final int size = index;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView = (TextView) view;
                        selectedView.setSelected(false);
                        selectedView = textView;
                        selectedView.setSelected(true);
                        count = size;
                        if (onItemClickListener != null)
                            onItemClickListener.onClick(view);
                    }
                });
                index++;

            }
        }
        for (int i = 0; i < sizeOther02.getChildCount(); i++) {
            View v = sizeOther02.getChildAt(i);
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                textView.setText(index + "手");
                if (isUp) {
                    textView.setBackgroundResource(R.drawable.bg_item_quick_trade);
                    textView.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
                } else {
                    textView.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
                    textView.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
                }

                if (count == index)
                    selectedView = textView;
                else
                    textView.setSelected(false);
                final int size = index;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView = (TextView) view;
                        selectedView.setSelected(false);
                        selectedView = textView;
                        selectedView.setSelected(true);
                        count = size;
                        if (onItemClickListener != null)
                            onItemClickListener.onClick(view);
                    }
                });
                index++;

            }
        }
        selectedView.setSelected(true);
    }


    public void hide() {
        sizeOtherView.setVisibility(View.GONE);
    }

    public void show() {
        sizeOtherView.setVisibility(View.VISIBLE);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    View.OnClickListener onItemClickListener;

    public View.OnClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
        initView();
    }
}
