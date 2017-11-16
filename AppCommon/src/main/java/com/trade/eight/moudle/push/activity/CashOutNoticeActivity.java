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
import com.trade.eight.moudle.trade.activity.CashHistoryAct;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/8.
 * 出金推送
 */

public class CashOutNoticeActivity extends BaseActivity {

    TextView tv_msg;
    Button btnPos;

    TextView tv_title;
    TextView tv_content;
    Button btnNav;

    PushMsgObj pushMsgObj;
    PushExtraObj pushExtraObj;

    public static void startCashOutNoticeActivity(Context context, PushMsgObj pushMsgObj) {
        Intent intent = new Intent();
        intent.putExtra("pushMsgObj", pushMsgObj);
        intent.setClass(context, CashOutNoticeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, Utils.getVirtualBarHeigh(this));
        WindowManager.LayoutParams lp = win.getAttributes();
        int width = (int) getResources().getDimension(R.dimen.margin_270dp);
        if (width >= Utils.getScreenW(this) * 0.9)
            lp.width = (int) (Utils.getScreenW(this) * 0.75);
        else
            lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

//        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        setStatusBarTintResource(R.color.transparent);
        setFinishOnTouchOutside(false);
        initData();

        if (pushExtraObj != null) {
            if (pushExtraObj.getType() == PushExtraObj.CASHOUT_SUCCESS) {//成功
                setContentView(R.layout.app_dialog_show_msg);
            } else {
                setContentView(R.layout.app_dialog_show_titlecontent_2btn);
            }
        } else {
            doMyfinish();
        }
        initView();
    }

    private void initData() {
        pushMsgObj = (PushMsgObj) getIntent().getSerializableExtra("pushMsgObj");
        pushExtraObj = pushMsgObj.getData();
    }

    private void initView() {

        if (pushExtraObj != null) {
            if (pushExtraObj.getType() == PushExtraObj.CASHOUT_SUCCESS) {//成功
                tv_msg = (TextView) findViewById(R.id.tv_msg);
                btnPos = (Button) findViewById(R.id.btnPos);

                tv_msg.setText(pushMsgObj.getBody());
                btnPos.setText(getResources().getString(R.string.unifypwd_posbtn_oldaccount));
                btnPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doMyfinish();
                    }
                });
            } else {
                tv_title = (TextView) findViewById(R.id.tv_title);
                tv_content = (TextView) findViewById(R.id.tv_content);
                btnPos = (Button) findViewById(R.id.btnPos);
                btnNav = (Button) findViewById(R.id.btnNav);
                tv_title.setText(pushMsgObj.getBody());
                tv_content.setText(getResources().getString(R.string.cashout_reason,pushExtraObj.getMark()));
                btnNav.setText(getResources().getString(R.string.close_btn_neg));
                btnPos.setText(getResources().getString(R.string.cashout_lookdetail));

                btnNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doMyfinish();
                    }
                });

                btnPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CashHistoryAct.startAct(CashOutNoticeActivity.this);
                        doMyfinish();
                    }
                });
            }
        }

    }

}
