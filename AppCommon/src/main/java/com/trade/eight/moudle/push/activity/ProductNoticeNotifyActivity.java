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
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/8.
 * 行情提醒到达
 */

public class ProductNoticeNotifyActivity extends BaseActivity implements View.OnClickListener{

    TextView text_pnn_name;
    TextView text_pnn_price;
    TextView text_pnn_rate;
    TextView text_pnn_time;
    Button text_pnn_gooptional;
    Button text_pnn_goorder;
    TextView text_pnn_iknow;
    PushExtraObj productNoticeNotify;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void startProductNoticeActivity(Context context,PushExtraObj productNoticeNotify){
        Intent intent = new Intent();
        intent.putExtra("productNoticeNotify",productNoticeNotify);
        intent.setClass(context,ProductNoticeNotifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //必须设置是新启动
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        setContentView(R.layout.layout_productnotice_notify);
        initData();
        initView();
    }

    private void initData(){
        productNoticeNotify = (PushExtraObj) getIntent().getSerializableExtra("productNoticeNotify");
    }

    private void initView(){

        text_pnn_name= (TextView) findViewById(R.id.text_pnn_name);
        text_pnn_price= (TextView) findViewById(R.id.text_pnn_price);
        text_pnn_rate= (TextView) findViewById(R.id.text_pnn_rate);
        text_pnn_time= (TextView) findViewById(R.id.text_pnn_time);
        text_pnn_gooptional= (Button) findViewById(R.id.text_pnn_gooptional);
        text_pnn_gooptional.setOnClickListener(this);
        text_pnn_goorder= (Button) findViewById(R.id.text_pnn_goorder);
        text_pnn_goorder.setOnClickListener(this);
        text_pnn_iknow= (TextView) findViewById(R.id.text_pnn_iknow);
        text_pnn_iknow.setOnClickListener(this);

        text_pnn_name.setText(productNoticeNotify.getName());
        text_pnn_time.setText(simpleDateFormat.format(new Date(productNoticeNotify.getTime())));
        if(Double.parseDouble(productNoticeNotify.getMargin())>0){
            text_pnn_price.setTextColor(getResources().getColor(R.color.color_opt_gt));
            text_pnn_rate.setTextColor(getResources().getColor(R.color.color_opt_gt));
        }else if(Double.parseDouble(productNoticeNotify.getMargin())<0){
            text_pnn_price.setTextColor(getResources().getColor(R.color.color_opt_lt));
            text_pnn_rate.setTextColor(getResources().getColor(R.color.color_opt_lt));
        }else{
            text_pnn_price.setTextColor(getResources().getColor(R.color.color_opt_eq));
            text_pnn_rate.setTextColor(getResources().getColor(R.color.color_opt_eq));
        }
        text_pnn_price.setText(productNoticeNotify.getReminderProfit());
        if(Double.parseDouble(productNoticeNotify.getMargin())>0){
            text_pnn_rate.setText("+"+productNoticeNotify.getMargin()+"("+productNoticeNotify.getMq()+")");
        }else{
            text_pnn_rate.setText(productNoticeNotify.getMargin()+"("+productNoticeNotify.getMq()+")");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.text_pnn_gooptional:
                ProductActivity.start(ProductNoticeNotifyActivity.this,productNoticeNotify.getExcode(),productNoticeNotify.getCode(), Optional.TRADEFLAG_YES);
                this.finish();
                break;
            case R.id.text_pnn_goorder:
//                Intent intent = new Intent(ProductNoticeNotifyActivity.this, MainActivity.class);
//                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

                Intent intent = new Intent(ProductNoticeNotifyActivity.this, MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 1);
                intent.putExtra("excode", productNoticeNotify.getExcode());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
            case R.id.text_pnn_iknow:
                this.finish();
                break;
        }
    }


}
