package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.moudle.trade.activity.TradeRegAct;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.nav.UNavConfig;

/**
 * Created by fangzhu on 16/8/18.
 * <p/>
 * 步骤引导图的效果 点击直接关闭
 */
public class StepNavAct extends BaseActivity {
    public static final int TYPE_STP_03 = 12;
//    public static final int TYPE_STP_04 = 13;
    public static final int TYPE_JN_HOLD = 100;
    public static final int TYPE_JN_TRADE_LIST = 101;
    public static final int TYPE_PRODUCTNOTICE  = 102;//行情提醒
    //切换交易所的引导
    public static final int TYPE_CHANGE_TRAD = 9527;

    int mType = 0;
    View homeHelpLayout, btnSkip;
    ImageView imageView = null;
    Context context = this;

    public static void start(Context context, int type) {
        context.startActivity(new Intent(context, StepNavAct.class).putExtra("type", type));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_step);

        mType = getIntent().getIntExtra("type", -1);
        homeHelpLayout = findViewById(R.id.homeHelpLayout);
        btnSkip = findViewById(R.id.btnSkip);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (mType == TYPE_STP_03) {
            if (UNavConfig.isSkipStep(context)) {
                finish();
                return;
            }
            if (!UNavConfig.isShowStep03(context)) {
                finish();
                return;
            }
            showStep03();
        } else if (mType == TYPE_JN_HOLD) {
            showNavTrade(R.drawable.nav_img_jn_hold);
        } else if (mType == TYPE_JN_TRADE_LIST) {
            showNavTrade(R.drawable.nav_img_jn_list);
        } else if(mType == TYPE_PRODUCTNOTICE){
            showNavTrade(R.drawable.nav_productnotice);
        }else {
            finish();
        }
    }

    int navStep = 3;

    void showStep03() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                UNavConfig.setSkipStep(context, true);
            }
        });
        //第三步
        if (UNavConfig.isShowStep03(context)) {
            homeHelpLayout.setVisibility(View.VISIBLE);
            try {
                btnSkip.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.unav_step03);
            } catch (Exception e) {
                e.printStackTrace();
                UNavConfig.setShowStep03(context, false);
                finish();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                UNavConfig.setShowStep03(context, false);
                finish();
                System.gc();//通知gc回收
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        navStep++;
                        if (navStep == 4) {
                            UNavConfig.setShowStep03(context, false);
                            UNavConfig.setShowStep04(context, true);

                            int img = R.drawable.unav_step04;
                            imageView.setImageResource(img);
                        }
                        if (navStep == 5) {
                            UNavConfig.setShowStep04(context, false);
                            finish();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                        return;
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        finish();
                        return;
                    }
                }
            });
        }
    }

    void onClickEvent () {
        if (mType == TYPE_JN_HOLD) {

        }
        if (mType == TYPE_JN_TRADE_LIST) {
            startActivity(new Intent(context, TradeRegAct.class));
            finish();
        }
    }
    void showNavTrade(int id) {
        if (imageView != null) {
            try {
                imageView.setImageResource(id);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                finish();
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickEvent ();
                    finish();
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mType == TYPE_JN_HOLD) {
            PreferenceSetting.setBoolean(context, "trade_jn_hold_nav", true);
        }
        if (mType == TYPE_JN_TRADE_LIST) {
            PreferenceSetting.setBoolean(context, "trade_jn_plist", true);
        }
        if (mType == TYPE_PRODUCTNOTICE) {
            PreferenceSetting.setBoolean(context, "nav_productnotice", true);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 淡出淡入动画效果
    }
}
