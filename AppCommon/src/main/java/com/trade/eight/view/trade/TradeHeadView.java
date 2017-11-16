package com.trade.eight.view.trade;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.chatroom.activity.ChatRoomActivity;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.QiHuoMyAccountAct;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/5.
 * 交易的头部view
 */

public class TradeHeadView extends LinearLayout {
    View line_root;
    View view_spilt;
    LinearLayout line_content;
    TextView text_moneyzy_rate, text_money_dqqy, text_moneyzy_kyzj, text_moneyzy_dyyk;
    ImageView img_hometrade_help, img_tradeinfo_dangerous;

    TradeInfoData tradeInfoData;

    private Context context;

    BaseActivity baseActivity;

    TradeLoginDlg tradeLoginDlg;


    public TradeHeadView(Context context) {
        super(context);
        init(context);
    }

    public TradeHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TradeHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        this.context = context;
        View v = View.inflate(context, R.layout.view_trade_head, null);
        addView(v);
        initView(v);
    }

    private void initView(View view) {
        line_root = view.findViewById(R.id.line_root);
        view_spilt  = view.findViewById(R.id.view_spilt);
        line_content  = (LinearLayout) view.findViewById(R.id.line_content);
        text_moneyzy_rate = (TextView) view.findViewById(R.id.text_moneyzy_rate);
        text_money_dqqy = (TextView) view.findViewById(R.id.text_money_dqqy);
        text_moneyzy_kyzj = (TextView) view.findViewById(R.id.text_moneyzy_kyzj);
        text_moneyzy_dyyk = (TextView) view.findViewById(R.id.text_moneyzy_dyyk);
        img_hometrade_help = (ImageView) view.findViewById(R.id.img_hometrade_help);
        img_tradeinfo_dangerous = (ImageView) view.findViewById(R.id.img_tradeinfo_dangerous);
        img_hometrade_help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                QiHuoExplainWordActivity.startAct(baseActivity,
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_MONEY_EXPLAIN);

                DialogUtilForTrade.showQiHuoExplainDlg(baseActivity,

                                new String[]{QiHuoExplainWordConfig.FXL,
                                QiHuoExplainWordConfig.DQQY,
                                QiHuoExplainWordConfig.KYZJ,
                                QiHuoExplainWordConfig.ZCCYK});
            }
        });
        img_tradeinfo_dangerous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseActivity == null) {
                    return;
                }
                DialogUtil.showTitleAndContentDialog(baseActivity,
                        context.getResources().getString(R.string.tradeinfonotify_4),
                        context.getResources().getString(R.string.tradeinfonotify_5, tradeInfoData.getCapitalProportion()),
                        context.getResources().getString(R.string.unifypwd_posbtn_oldaccount),
                        context.getResources().getString(R.string.tradeinfonotify_chargenow),
                        true,
                        new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                return false;
                            }
                        },
                        new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                CashInAndOutAct.startAct(context);
                                return false;
                            }
                        });
            }
        });

        line_root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseActivity == null) {
                    return;
                }
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_MONEY_BORD);

                UserInfoDao userInfoDao = new UserInfoDao(context);
                if(!userInfoDao.isLogin()){
                    context.startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                //本地先验证token 过期了
                if (!TradeHelp.isTokenEnable(baseActivity)) {
                    if (tradeLoginDlg == null) {
                        tradeLoginDlg = new TradeLoginDlg(baseActivity);
                    }
                    if (!tradeLoginDlg.isShowingDialog()) {
                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                    }
                    return;
                }

                if (tradeInfoData != null) {
                    QiHuoMyAccountAct.statartAct(context, tradeInfoData);
                }
            }
        });
    }

    /**
     * 控制顶部的间隙是否可显示
     * @param isVisible
     */
    public void controlViewSpiltVisible(boolean isVisible){
        if(isVisible){
            view_spilt.setVisibility(VISIBLE);
        }else {
            view_spilt.setVisibility(GONE);
        }
    }

    /**
     * 控制整体内容的背景
     * @param drawable
     */
    public void controlLineContentBG(int drawable){
        if(drawable==-1){
            line_content.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            return;
        }
        line_content.setBackgroundResource(drawable);
    }

    public void controlBtnHelpVisible(boolean isVisible){
        if(isVisible){
            img_hometrade_help.setVisibility(VISIBLE);
        }else {
            img_hometrade_help.setVisibility(GONE);
        }
    }

    /**
     * 设置整个view是否可点击
     * @param isCanClick
     */
    public void setLineRootCanClick(boolean isCanClick) {
        if (isCanClick) {
            line_root.setClickable(true);
        } else {
            line_root.setClickable(false);
        }
    }

    /**
     * 展示头部信息
     *
     * @param tradeInfoData
     */
    public void displayView(TradeInfoData tradeInfoData) {
        this.tradeInfoData = tradeInfoData;
        img_tradeinfo_dangerous.setVisibility(GONE);
        if (tradeInfoData == null) {
            text_moneyzy_rate.setText("--");
            text_money_dqqy.setText("--");
            text_moneyzy_kyzj.setText("--");
            text_moneyzy_dyyk.setText("--");
            return;
        }
        text_moneyzy_rate.setText(tradeInfoData.getCapitalProportion());
        text_money_dqqy.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getRightsInterests()).doubleValue()));
        text_moneyzy_kyzj.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getAvailable()).doubleValue()));
        if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() > 0) {
            text_moneyzy_dyyk.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_moneyzy_dyyk.setText("+" + StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() < 0) {
            text_moneyzy_dyyk.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_moneyzy_dyyk.setText(StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else {
            text_moneyzy_dyyk.setTextColor(context.getResources().getColor(R.color.c_464646));
            text_moneyzy_dyyk.setText(StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        }
        if (tradeInfoData.getTradeInfoSafeOrDangerous() != 0) {
            img_tradeinfo_dangerous.setVisibility(VISIBLE);
            switch (tradeInfoData.getTradeInfoSafeOrDangerous()) {
                case TradeInfoData.TRADEINFO_DANGEROUS:
                    img_tradeinfo_dangerous.setClickable(false);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_2);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_1:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_2:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_3:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;

            }
        }

    }

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }
}
