package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.moudle.outterapp.WebActivity;


public class AboutUsActivity extends BaseActivity {

    private TextView guanwangTV, guanwang_lableTV, wx_lableTV, wx_tvTV, destvTV;

    private TextView phoneTV;
    boolean isIndex = false;

    public static void startIndexAct(Context context){
        Intent intent = new Intent(context,AboutUsActivity.class);
        intent.putExtra("isIndex",true);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        isIndex = getIntent().getBooleanExtra("isIndex", false);
        if (isIndex) {
            setContentView(R.layout.activity_about_us_4index);
            initView4Index();

        } else {
            setContentView(R.layout.activity_about_us);
            initView();
        }

    }

    public void initView4Index() {
        setAppCommonTitle("关于我们");
//        ImageView img_appIcon = (ImageView) findViewById(R.id.img_appIcon);
//        if (img_appIcon != null)
//            img_appIcon.setImageDrawable(getResources().getDrawable(R.drawable.app_icon));
//        TextView tv_appName = (TextView) findViewById(R.id.tv_appName);
//        if (tv_appName != null)
//            tv_appName.setText(getResources().getString(R.string.app_name));

        TextView tv_appVersion = (TextView) findViewById(R.id.tv_appVersion);
        if (tv_appVersion != null)
            tv_appVersion.setText("V" + AppSetting.getInstance(this).getAppVersion());

    }

    /**
     * 不能删掉  xml 绑定的 click
     * @param view
     */
    public void itemClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_aboutus) {
//            startActivity(new Intent(this, AboutUsActivity.class));
            WebActivity.start(this, getString(R.string.aboutus_lable), AndroidAPIConfig.URL_ABOUT_US);
        } else if (id == R.id.ll_law) {
            startActivity(new Intent(this, LawActivity.class));
        }
//        else if (id == R.id.ll_job) {
//            startActivity(new Intent(this, LawActivity.class).putExtra("isJob", true));
//        }
    }

    public void initView() {
    }

    public void back(View v) {
        this.finish();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
