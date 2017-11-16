package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.QiHuoExplainWordData;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/8/4.
 * 名词解释
 */

public class ExplainWordAdapter extends ArrayAdapter<QiHuoExplainWordData> {

    List<QiHuoExplainWordData> list;
    Context context;

    public ExplainWordAdapter(Context context, int resource, List<QiHuoExplainWordData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.item_qihuo_explainword, null);
        }

        TextView text_explain_title = MyViewHolder.get(view, R.id.text_explain_title);
        TextView text_explain_content = MyViewHolder.get(view, R.id.text_explain_content);
        QiHuoExplainWordData qiHuoExplainWordData = list.get(position);
        text_explain_title.setText(qiHuoExplainWordData.getTitle());
        text_explain_content.setText(qiHuoExplainWordData.getContent());
        return view;
    }
}
