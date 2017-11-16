package com.trade.eight.moudle.push.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.moudle.push.entity.PushMsgObj;
import com.trade.eight.moudle.trade.activity.FXBTGCashInH5Act;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/8.
 * 爆仓警告
 */

public class TradeFXNoticeActivity extends BaseActivity implements View.OnClickListener {

    TextView text_tradefx_noticetitle;
    TextView text_tradefx_noticetips;
    Button text_tradefx_goorder;
    Button text_tradefx_gocashin;
    TextView text_tradefx_iknow;

    PushMsgObj pushMsgObj;

    public static void startAct(Context context, PushMsgObj pushMsgObj) {
        Intent intent = new Intent();
        intent.putExtra("pushMsgObj", pushMsgObj);
        intent.setClass(context, TradeFXNoticeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, Utils.getVirtualBarHeigh(this));
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        setStatusBarTintResource(R.color.transparent);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.layout_tradefx_notify);
        initData();
        initView();
    }

    private void initData() {
        pushMsgObj = (PushMsgObj) getIntent().getSerializableExtra("pushMsgObj");
    }

    private void initView() {
        text_tradefx_noticetitle = (TextView) findViewById(R.id.text_tradefx_noticetitle);
        text_tradefx_noticetips = (TextView) findViewById(R.id.text_tradefx_noticetips);
        text_tradefx_goorder = (Button) findViewById(R.id.text_tradefx_goorder);
        text_tradefx_gocashin = (Button) findViewById(R.id.text_tradefx_gocashin);
        text_tradefx_iknow = (TextView) findViewById(R.id.text_tradefx_iknow);

        text_tradefx_goorder.setOnClickListener(this);
        text_tradefx_gocashin.setOnClickListener(this);
        text_tradefx_iknow.setOnClickListener(this);

        text_tradefx_noticetitle.setText(pushMsgObj.getBody());
        PushExtraObj pushExtraObj = pushMsgObj.getData();
        if (pushExtraObj != null) {
            text_tradefx_noticetips.setText(pushExtraObj.getTimeOutTxt());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text_tradefx_gocashin:
                FXBTGCashInH5Act.startCashin(TradeFXNoticeActivity.this);
                this.finish();
                break;
            case R.id.text_tradefx_goorder:
                Intent intent = new Intent(TradeFXNoticeActivity.this, MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.text_tradefx_iknow:
                this.finish();
                break;
        }
    }


}
