package com.trade.eight.moudle.me.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.view.AppTitleView;

/**
 * Created by fangzhu on 2015/2/26.
 */
public class LawActivity extends BaseActivity {
    TextView contentText = null;
    AppTitleView title_view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.law_activity);

        init();
    }

    void init () {
//        TextView title_tv = (TextView)findViewById(R.id.title_tv);
//        if (title_tv != null)
//            title_tv.setText(getString(R.string.law_lable));

        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(LawActivity.this);
        title_view.setAppCommTitle(R.string.law_lable);



        contentText = (TextView)findViewById(R.id.contentText);

    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
