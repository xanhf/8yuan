package com.trade.eight.moudle.outterapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.entity.QiHuoExplainWordData;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.Utils;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/24.
 */

public class QiHuoExplainWordActivity extends BaseActivity {

    TextView text_qhlogin_cancle;
    ListView recycle_explain;
//    View view_virtualbar;
    /**
     * recyclerView 必须设置 LinearLayoutManager
     */
    LinearLayoutManager linearLayoutManager = null;

    protected int activityOpenEnterAnimation;
    protected int activityCloseExitAnimation;

    public static void startAct(Context context, String[] keys) {
        Intent intent = new Intent(context, QiHuoExplainWordActivity.class);
        intent.putExtra("keys", keys);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, Utils.getVirtualBarHeigh(QiHuoExplainWordActivity.this));
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT - Utils.getVirtualBarHeigh(QiHuoExplainWordActivity.this);
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.act_qihuo_explainword);

        // 窗口设置透明
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityOpenEnterAnimation, android.R.attr.activityCloseExitAnimation});
        activityOpenEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();

        initView();
        initData();
    }

    private void initView() {
        text_qhlogin_cancle = (TextView) findViewById(R.id.text_qhlogin_cancle);
        text_qhlogin_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doMyfinish();
                overridePendingTransition(activityOpenEnterAnimation, activityCloseExitAnimation);

            }
        });

        recycle_explain = (ListView) findViewById(R.id.recycle_explain);

    }

    private void initData() {
        String[] keys = getIntent().getStringArrayExtra("keys");
        List<QiHuoExplainWordData> list = QiHuoExplainWordConfig.getDisplayList(keys);
        ExplainWordAdapter explainWordAdapter = new ExplainWordAdapter(QiHuoExplainWordActivity.this, 0, list);
        recycle_explain.setAdapter(explainWordAdapter);
//        recycle_explain.setItemViewCacheSize(explainWordAdapter.getItemCount());
//        int height = getListviewHeight(explainWordAdapter);
//        if(height>Utils.getScreenH(this)-Utils.dip2px(this,80)){
//            ViewGroup.LayoutParams listParams = recycle_explain.getLayoutParams();
//            listParams.height = height;
//            recycle_explain.setLayoutParams(listParams);
//        }
    }

    /**
     * 获取listview 高度
     *
     * @return
     */
    private int getListviewHeight(BaseAdapter explainWordAdapter) {
        int totalHeight = 0;
        for (int i = 0; i < explainWordAdapter.getCount(); i++) {
            View listItem = explainWordAdapter.getView(i, null, recycle_explain);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        int height = totalHeight + (recycle_explain.getDividerHeight() * (explainWordAdapter.getCount() - 1));
        return height;
    }

    class ExplainWordAdapter extends ArrayAdapter<QiHuoExplainWordData> {

        List<QiHuoExplainWordData> list;

        public ExplainWordAdapter(Context context, int resource, List<QiHuoExplainWordData> objects) {
            super(context, resource, objects);
            this.list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = View.inflate(QiHuoExplainWordActivity.this, R.layout.item_qihuo_explainword, null);
            }

            TextView text_explain_title = MyViewHolder.get(view, R.id.text_explain_title);
            TextView text_explain_content = MyViewHolder.get(view, R.id.text_explain_content);
            QiHuoExplainWordData qiHuoExplainWordData = list.get(position);
            text_explain_title.setText(qiHuoExplainWordData.getTitle());
            text_explain_content.setText(qiHuoExplainWordData.getContent());
            return view;
        }
    }

//    class ExplainWordAdapter extends RecyclerView.Adapter<ExplainWordAdapter.MyViewHolder> {
//
//        List<QiHuoExplainWordData> list;
//
//        public ExplainWordAdapter(List<QiHuoExplainWordData> list) {
//            this.list = list;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View rootView = View.inflate(parent.getContext(), R.layout.item_qihuo_explainword, null);
//            return new MyViewHolder(rootView);
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder holder, int position) {
//            if (holder.text_explain_title == null) {
//                return;
//            }
//            QiHuoExplainWordData qiHuoExplainWordData = list.get(position);
//            holder.text_explain_title.setText(qiHuoExplainWordData.getTitle());
//            holder.text_explain_content.setText(qiHuoExplainWordData.getContent());
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//
//            TextView text_explain_title;
//            TextView text_explain_content;
//
//
//            public MyViewHolder(View itemView) {
//                super(itemView);
//                text_explain_title = (TextView) itemView.findViewById(R.id.text_explain_title);
//                text_explain_content = (TextView) itemView.findViewById(R.id.text_explain_content);
//            }
//
//        }
//    }
}
