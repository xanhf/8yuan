package com.trade.eight.tools.trade;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.trade.TradeVoucher;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.StepNavAct;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.refresh.RefreshUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu
 * 下单弹窗
 */
public class TradeCreateUtil4GG extends TradeCreateUtil {
    public static final String TAG = "TradeCreateUtil4GG";
    public static final String MOBCLICK_TAG = "dlg_create";
    int typeBuy = TradeProduct.TYPE_BUY_UP;

    TradeProduct initProduct;//传入进来的选中的类型，确定是8，80，200
    public static final int COUNT_SIZE01 = 1;
    public static final int COUNT_SIZE02 = 5;
    public static final int COUNT_SIZE03 = 10;
    int countSize = COUNT_SIZE01;
    ImageView gobackView;
    View selectCountV, selectMoneyV, selectUpOrDownV;
    Button btn_submit;
    TextView tv_price01, tv_rate, tv_rateChange, tv_size01, tv_balance, tv_quanCount, tv_unit,
            tv_size02, tv_size03, tv_mongey01, tv_mongey02, tv_mongey03, tv_sizeOther;
    TextView tv_yin;
    TextView tv_zhisunValue, tv_zhiyingValue, tv_zhisunMoney, tv_zhiyingMoney, tv_maxZhisun, tv_maxZhiying;
    SeekBar seekBarZhisun, seekBarZhiying;
    View upView, downView;
    TextView text_byuprate, text_bydownrate, text_ratetips;
    Dialog dialog;
    BaseActivity context;
    TextView tv_totalMoney, tv_fee;
    RefreshUtil refreshUtil;
    View quickSizeView;
    TradeCountUtil countUtil;//暂时用不到；使用外面的 list 一起更新
    View line_check_usecasher, line_check_usequan;
    ImageView check_usecasher, check_usequan;
    TextView text_giftname;
    TextView text_giftremark;
    //    ImageView imgQuanSwitch;
    boolean isQuan = false;//是否使用代金券
    Button btnZhisunLess, btnZhisunAdd, btnZhiyingLess, btnZhiyingAdd;
    SeekBarUtil seekBarUtil;

    List<List<TradeProduct>> initList;

    /**
     * 建仓页面dlg
     *
     * @param c
     * @param type     买涨买跌
     * @param p        选中的产品类型
     * @param initList 外部已经查询出的产品list
     */
    public TradeCreateUtil4GG(BaseActivity c, int type, TradeProduct p, List<List<TradeProduct>> initList) {
        if (dialog != null && dialog.isShowing())
            return;

        try {
            this.context = c;
            this.typeBuy = type;
            this.initProduct = p;
            this.initList = initList;

            if (!new UserInfoDao(context).isLogin()) {
                context.showCusToast("请登录");
                return;
            }
            dialog = new Dialog(context, R.style.dialog_trade);
            View rootView = View.inflate(context, R.layout.dialog_create_order, null);
            dialog.setContentView(rootView);
            findViews(dialog);
            if (initList != null && initList.size() > 0)
                updatePrice(initList);//如果传入了就 初始化第一次的数据
            countUtil = new TradeCountUtil(context, rootView);
            countUtil.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setBuyTotalMoneyFee();
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, textView.getText().toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void showDialog(int aniStyle) {
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(aniStyle);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (refreshUtil != null)
                    refreshUtil.stop();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (UNavConfig.isShowStep03(context)) {
                    StepNavAct.start(context, StepNavAct.TYPE_STP_03);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 淡出淡入动画效果
                }
            }
        });

        initRefresh();

        new InitTask().execute();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_submit) {
                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "btn_submit_click");
                submit(context);

            } else if (id == R.id.btnZhisunLess) {
                seekBarZhisun.setProgress(seekBarZhisun.getProgress() - 1);
            } else if (id == R.id.btnZhisunAdd) {
                seekBarZhisun.setProgress(seekBarZhisun.getProgress() + 1);
            } else if (id == R.id.btnZhiyingLess) {
                seekBarZhiying.setProgress(seekBarZhiying.getProgress() - 1);
            } else if (id == R.id.btnZhiyingAdd) {
                seekBarZhiying.setProgress(seekBarZhiying.getProgress() + 1);
            } else if (id == R.id.tv_cashin) {
                context.startActivity(new Intent(context, CashInAct.class));
                dialog.dismiss();
            } else if (id == R.id.imgQuanSwitch) {
//                if (isQuan) {
//                    imgQuanSwitch.setImageResource(R.drawable.icon_switch_off);
//                } else {
//                    imgQuanSwitch.setImageResource(R.drawable.icon_switch_on);
//                }
//                isQuan = !isQuan;
//                setBuyTotalMoneyFee();
            } else if (id == R.id.upView) {
                selectUpOrDownV.setSelected(false);
                selectUpOrDownV = v;
                selectUpOrDownV.setSelected(true);
                typeBuy = TradeProduct.TYPE_BUY_UP;
                setViewBG(true);
                countUtil.setUp(typeBuy == TradeProduct.TYPE_BUY_UP);
                setBuyTotalMoneyFee();

                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "buyUp");

            } else if (id == R.id.tv_sizeOther) {
                countUtil.setUp(typeBuy == TradeProduct.TYPE_BUY_UP);
                valueCountSize();
                quickSizeView.setVisibility(View.GONE);
                //如果需要显示外面已经选中的
                countUtil.setCount(countSize);
                countUtil.initView();
                countUtil.show();

            } else if (id == R.id.downView) {
                selectUpOrDownV.setSelected(false);
                selectUpOrDownV = v;
                selectUpOrDownV.setSelected(true);

                typeBuy = TradeProduct.TYPE_BUY_DOWN;
                setViewBG(false);
                countUtil.setUp(typeBuy == TradeProduct.TYPE_BUY_UP);
                setBuyTotalMoneyFee();

                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "buyDown");
            } else if (id == R.id.tv_size01 || id == R.id.tv_size02 || id == R.id.tv_size03) {
                selectCountV.setSelected(false);
                selectCountV = v;
                selectCountV.setSelected(true);
                setBuyTotalMoneyFee();

                //直接取textView的文字
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, textView.getText().toString());
                }


            } else if (id == R.id.tv_mongey01 || id == R.id.tv_mongey02 || id == R.id.tv_mongey03) {
                selectMoneyV.setSelected(false);
                selectMoneyV = v;
                selectMoneyV.setSelected(true);

                setTvMoney();
                initSeekBar();
                setBuyTotalMoneyFee();

                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, textView.getText().toString());
                }
            } else if (id == R.id.gobackView) {
                dialog.dismiss();
            }
        }
    };

    /**
     * 改变背景色
     *
     * @param isUp
     */
    void setViewBG(boolean isUp) {
        if (isUp) {
            btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit);
            tv_mongey01.setBackgroundResource(R.drawable.bg_item_quick_trade);
            tv_mongey02.setBackgroundResource(R.drawable.bg_item_quick_trade);
            tv_mongey03.setBackgroundResource(R.drawable.bg_item_quick_trade);
            tv_size01.setBackgroundResource(R.drawable.bg_item_quick_trade);
            tv_size02.setBackgroundResource(R.drawable.bg_item_quick_trade);
            tv_size03.setBackgroundResource(R.drawable.bg_item_quick_trade);

            tv_mongey01.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
            tv_mongey02.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
            tv_mongey03.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
            tv_size01.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
            tv_size02.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));
            tv_size03.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv));


        } else {
            btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit_green);
            tv_mongey01.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
            tv_mongey02.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
            tv_mongey03.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
            tv_size01.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
            tv_size02.setBackgroundResource(R.drawable.bg_item_quick_trade_down);
            tv_size03.setBackgroundResource(R.drawable.bg_item_quick_trade_down);

            tv_mongey01.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
            tv_mongey02.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
            tv_mongey03.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
            tv_size01.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
            tv_size02.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));
            tv_size03.setTextColor(context.getResources().getColorStateList(R.color.item_quick_trade_color_tv_down));

        }

    }

    void findViews(Dialog dialog) {
        gobackView = (ImageView) dialog.findViewById(R.id.gobackView);
        tv_yin = (TextView) dialog.findViewById(R.id.tv_yin);
        tv_price01 = (TextView) dialog.findViewById(R.id.tv_price01);
        tv_rate = (TextView) dialog.findViewById(R.id.tv_rate);
        tv_rateChange = (TextView) dialog.findViewById(R.id.tv_rateChange);
        tv_balance = (TextView) dialog.findViewById(R.id.tv_balance);
        tv_quanCount = (TextView) dialog.findViewById(R.id.tv_quanCount);
        tv_unit = (TextView) dialog.findViewById(R.id.tv_unit);

        check_usecasher = (ImageView) dialog.findViewById(R.id.check_usecasher);
        line_check_usecasher = dialog.findViewById(R.id.line_check_usecasher);
        line_check_usecasher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_usecasher.setImageResource(R.drawable.img_choosequan_checked);
                check_usequan.setImageResource(R.drawable.img_choosequan_normal);
                isQuan = false;
                setBuyTotalMoneyFee();
            }
        });
        check_usequan = (ImageView) dialog.findViewById(R.id.check_usequan);
        line_check_usequan = dialog.findViewById(R.id.line_check_usequan);
        line_check_usequan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_usequan.setImageResource(R.drawable.img_choosequan_checked);
                check_usecasher.setImageResource(R.drawable.img_choosequan_normal);
                isQuan = true;
                setBuyTotalMoneyFee();
            }
        });

        upView = dialog.findViewById(R.id.upView);
        downView = dialog.findViewById(R.id.downView);
        text_byuprate = (TextView) dialog.findViewById(R.id.text_byuprate);
        text_bydownrate = (TextView) dialog.findViewById(R.id.text_bydownrate);
        text_ratetips = (TextView) dialog.findViewById(R.id.text_ratetips);

        quickSizeView = dialog.findViewById(R.id.quickSizeView);
        tv_size01 = (TextView) dialog.findViewById(R.id.tv_size01);
        tv_size02 = (TextView) dialog.findViewById(R.id.tv_size02);
        tv_size03 = (TextView) dialog.findViewById(R.id.tv_size03);
        tv_sizeOther = (TextView) dialog.findViewById(R.id.tv_sizeOther);

        tv_mongey01 = (TextView) dialog.findViewById(R.id.tv_mongey01);
        tv_mongey02 = (TextView) dialog.findViewById(R.id.tv_mongey02);
        tv_mongey03 = (TextView) dialog.findViewById(R.id.tv_mongey03);

        text_giftname = (TextView) dialog.findViewById(R.id.text_giftname);
        text_giftremark = (TextView) dialog.findViewById(R.id.text_giftremark);

        tv_totalMoney = (TextView) dialog.findViewById(R.id.tv_totalMoney);
        tv_fee = (TextView) dialog.findViewById(R.id.tv_fee);

        gobackView.setOnClickListener(onClickListener);

        upView.setOnClickListener(onClickListener);
        downView.setOnClickListener(onClickListener);

        tv_size01.setOnClickListener(onClickListener);
        tv_size02.setOnClickListener(onClickListener);
        tv_size03.setOnClickListener(onClickListener);
        tv_sizeOther.setOnClickListener(onClickListener);


        tv_size01.setText(String.format(context.getString(R.string.qucik_create_count01), COUNT_SIZE01 + ""));
        tv_size02.setText(String.format(context.getString(R.string.qucik_create_count02), COUNT_SIZE02 + ""));
        tv_size03.setText(String.format(context.getString(R.string.qucik_create_count03), COUNT_SIZE03 + ""));

        tv_mongey01.setOnClickListener(onClickListener);
        tv_mongey02.setOnClickListener(onClickListener);
        tv_mongey03.setOnClickListener(onClickListener);

        String strUp = context.getResources().getString(R.string.str_buy_up_1, initProduct.getBuyRateFromList(initList) + "%");
        text_byuprate.setText(strUp);
        String strDown = context.getResources().getString(R.string.str_buy_down_1, (100 - initProduct.getBuyRateFromList(initList)) + "%");
        text_bydownrate.setText(strDown);

        changPointTips(initProduct);

        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(onClickListener);

        setViewBG(typeBuy == TradeProduct.TYPE_BUY_UP);
        if (typeBuy == TradeProduct.TYPE_BUY_UP) {
            selectUpOrDownV = upView;
        } else {
            selectUpOrDownV = downView;
        }
        selectCountV = tv_size01;
        if (initProduct == null) {
            selectMoneyV = tv_mongey01;
            selectMoneyV.setSelected(true);
        }


        selectCountV.setSelected(true);

        selectUpOrDownV.setSelected(true);


        tv_zhisunValue = (TextView) dialog.findViewById(R.id.tv_zhisunValue);
        tv_zhiyingValue = (TextView) dialog.findViewById(R.id.tv_zhiyingValue);
        tv_maxZhisun = (TextView) dialog.findViewById(R.id.tv_maxZhisun);
        tv_maxZhiying = (TextView) dialog.findViewById(R.id.tv_maxZhiying);

        tv_zhisunMoney = (TextView) dialog.findViewById(R.id.tv_zhisunMoney);
        tv_zhiyingMoney = (TextView) dialog.findViewById(R.id.tv_zhiyingMoney);


        btnZhisunLess = (Button) dialog.findViewById(R.id.btnZhisunLess);
        btnZhisunAdd = (Button) dialog.findViewById(R.id.btnZhisunAdd);
        btnZhiyingLess = (Button) dialog.findViewById(R.id.btnZhiyingLess);
        btnZhiyingAdd = (Button) dialog.findViewById(R.id.btnZhiyingAdd);

        btnZhisunLess.setOnClickListener(onClickListener);
        btnZhisunAdd.setOnClickListener(onClickListener);
        btnZhiyingLess.setOnClickListener(onClickListener);
        btnZhiyingAdd.setOnClickListener(onClickListener);

        seekBarZhisun = (SeekBar) dialog.findViewById(R.id.seekBarZhisun);
        seekBarZhiying = (SeekBar) dialog.findViewById(R.id.seekBarZhiying);

        View tv_cashin = dialog.findViewById(R.id.tv_cashin);
        tv_cashin.setOnClickListener(onClickListener);

        seekBarUtil = new SeekBarUtil(seekBarZhisun, seekBarZhiying,
                tv_zhisunValue, tv_zhiyingValue,
                tv_zhisunMoney, tv_zhiyingMoney,
                tv_maxZhisun, tv_maxZhiying);
        seekBarUtil.setQuan(isQuan);
        seekBarUtil.setBuyCount(countSize);
    }


    void initSeekBar() {
        final TradeProduct product = getTradeProduct();
        if (product == null)
            return;
        seekBarUtil.initSeekBarZhiying(product);
        seekBarUtil.initSeekBarZhisun(product);
    }

    List<List<TradeProduct>> cashlist;
    boolean initedSeekBar = false;

    //第一次更新价格
    boolean isInitFee = false;


    /**
     * 仅仅刷新当前品种的价格
     * 必须先初始化过交易类型 这些信息之后
     *
     * @param o
     */
    public void updatePrice(Optional o) {
        if (o == null)
            return;
        //不是第一次传入的list初始化
        if (dialog == null || !dialog.isShowing() && isInitFee) {
            if (refreshUtil != null)
                refreshUtil.stop();
            return;
        }
        tv_price01.setText(o.getSellone());
    }

    /**
     * 更新价格
     */
    public void updatePrice(List<List<TradeProduct>> list) {
        try {
            if (context.isFinishing()) {
                if (refreshUtil != null)
                    refreshUtil.stop();
                return;
            }
            //筛选出数据
            list = filterData(list);

            if (list == null || list.size() == 0)
                return;
            if (list.get(0) == null || list.get(0).size() == 0)
                return;
            //不是第一次传入的list初始化
            if (dialog == null || !dialog.isShowing() && isInitFee) {
                if (refreshUtil != null)
                    refreshUtil.stop();
                return;
            }

            cashlist = list;
            //第一次更新价格
            if (!isInitFee) {
                setTvMoney();
                setBuyTotalMoneyFee();
                isInitFee = true;
            }

            if (tv_price01 == null)
                return;

            if (!initedSeekBar) {
                initSeekBar();
                initedSeekBar = true;
            }
            tv_yin.setText(list.get(0).get(0).getNameByCode(context));
            tv_price01.setText(list.get(0).get(0).getSell());

            TradeProduct optional = list.get(0).get(0);
            if (!StringUtil.isEmpty(optional.getMargin())) {
                double diff = 0;
                try {
                    diff = Double.parseDouble(optional.getMargin());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //格式化小数点
                String diffStr = NumberUtil.moveLast0(diff);
                String rate02 = ConvertUtil.NVL(optional.getMp(), "");

                int color = context.getResources().getColor(R.color.trade_down);
                if (diff > 0) {
                    color = context.getResources().getColor(R.color.trade_up);
                    diffStr = "+" + diffStr;
                    rate02 = "+" + rate02;
                }

                if (diff == 0)
                    color = context.getResources().getColor(R.color.color_opt_eq);
                tv_rate.setText(diffStr);
                tv_rate.setTextColor(color);

                tv_rateChange.setText(rate02);
                tv_rateChange.setTextColor(color);

                tv_price01.setTextColor(color);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用cashlist  设置textiew 多少元一手
     */
    void setTvMoney() {
        try {
            if (cashlist == null || cashlist.size() == 0)
                return;


            if (cashlist.get(0).size() > 0) {
                tv_mongey01.setVisibility(View.VISIBLE);
                tv_mongey01.setText(cashlist.get(0).get(0).getPrice() + "元/手");

                //初始化选中的类型
                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(cashlist.get(0).get(0).getProductId())) {
                        selectMoneyV = tv_mongey01;
                    }
                }
            } else {
                tv_mongey01.setVisibility(View.GONE);
            }
            if (cashlist.get(0).size() > 1) {
                tv_mongey02.setVisibility(View.VISIBLE);
                tv_mongey02.setText(cashlist.get(0).get(1).getPrice() + "元/手");

                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(cashlist.get(0).get(1).getProductId())) {
                        selectMoneyV = tv_mongey02;
                    }
                }
            } else {
                tv_mongey02.setVisibility(View.GONE);
            }
            if (cashlist.get(0).size() > 2) {
                tv_mongey03.setVisibility(View.VISIBLE);
                tv_mongey03.setText(cashlist.get(0).get(2).getPrice() + "元/手");

                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(cashlist.get(0).get(2).getProductId())) {
                        selectMoneyV = tv_mongey03;
                    }
                }
            } else {
                tv_mongey03.setVisibility(View.GONE);
            }

            if (selectMoneyV == null)
                selectMoneyV = tv_mongey01;
            selectMoneyV.setSelected(true);
//            setBuyTotalMoneyFee(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void valueCountSize() {
        if (quickSizeView.getVisibility() == View.VISIBLE) {
            if (selectCountV.equals(tv_size01)) {
                countSize = COUNT_SIZE01;
            } else if (selectCountV.equals(tv_size02)) {
                countSize = COUNT_SIZE02;
            } else if (selectCountV.equals(tv_size03)) {
                countSize = COUNT_SIZE03;
            }
        } else {
            countSize = countUtil.getCount();
        }
    }

    /**
     * 算出对应的品种 多少手
     *
     * @return
     */
    TradeProduct getTradeProduct() {
        try {
            if (cashlist == null || cashlist.size() == 0)
                return null;
            //多少手
            valueCountSize();

            //确定品种
            if (selectMoneyV.equals(tv_mongey01)) {
                return cashlist.get(0).get(0);
            } else if (selectMoneyV.equals(tv_mongey02)) {
                return cashlist.get(0).get(1);
            } else if (selectMoneyV.equals(tv_mongey03)) {
                return cashlist.get(0).get(2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void submit(final BaseActivity activity) {
        //先判读是否现金或者代金券是否够用
        if (isQuan) {
            int quan = getPVoucher(getTradeProduct());
            if (quan == 0) {
                DialogUtil.showMsgDialog(activity, "当前品种暂无代金券", "确定");
                return;
            }
            if (countSize > quan) {
                DialogUtil.showMsgDialog(activity, "可用代金券共" + quan + "张", "确定");
                return;
            }
        } else {
            try {
                //可用金额
                double totalMoney = Double.parseDouble(blance.replaceAll(",", ""));
                if (totalMoney < getBuyTotalMoney()) {
                    DialogUtil.showConfirmDlg(context, "余额不足", "取消", "去充值", true,
                            new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message message) {
                                    return false;
                                }
                            }, new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message message) {
                                    CashInAct.start(context);
                                    //直接关闭dlg简单处理，如果不关闭dlg 充值回来还得刷新金额和代金券
                                    dialog.dismiss();
                                    return false;
                                }
                            });
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final UserInfoDao dao = new UserInfoDao(activity);
        if (!dao.isLogin()) {
            context.startActivity(new Intent(activity, LoginActivity.class));
            return;
        }
        TradeProduct product = getTradeProduct();
        if (product == null)
            return;
        submit(context, isQuan);
    }

    /**
     * 建仓
     */
    Dialog tokenDialog;

    public void submit(final BaseActivity activity, final boolean isQuan) {
        if (activity == null)
            return;
        final UserInfoDao dao = new UserInfoDao(activity);
        TradeProduct product = getTradeProduct();
        if (product == null)
            return;

        Map<String, String> map = ApiConfig.getCommonMap(activity);
        map.put(TradeProduct.PARAM_UID, dao.queryUserInfo().getUserId());
        map.put(TradeProduct.PARAM_PID, product.getProductId() + "");
        map.put(TradeProduct.PARAM_ORDER_NB, countSize + "");
        map.put(TradeProduct.PARAM_TYPE, typeBuy + "");
        map.put(TradeProduct.PARAM_STOPPROFIT, SeekBarUtil.getZhiying(seekBarZhiying, product));
        map.put(TradeProduct.PARAM_STOPLOSS, SeekBarUtil.getZhisun(seekBarZhisun, product));


        if (TradeConfig.isCurrentJN(activity)||TradeConfig.isCurrentHN(activity)) {
            //农交所是否使过夜
            map.put(TradeProduct.PARAM_IS_DEFERRED, TradeProduct.IS_DEFERRED_YES);
        }
        //首先得判断是否使用券
        String juan = TradeProduct.JUAN_DISENABLE;
        if (isQuan)
            juan = TradeProduct.JUAN_ENABLE;
        map.put(TradeProduct.PARAM_ISJUAN, juan + "");

        map.put(TradeProduct.PARAM_COUPONID, product.getCouponId() + "");

        map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(activity, map));

        final Map<String, String> mapFinal = map;
        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showNetLoadingProgressDialog(activity.getResources().getString(R.string.str_trade_deal));
            }

            @Override
            protected CommonResponse<TempObject> doInBackground(Void... params) {
                try {
                    String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CREATE);
                    String res = TradeHelp.post(activity, url, mapFinal);
                    CommonResponse<TempObject> response = CommonResponse.fromJson(res, TempObject.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                if (activity == null)
                    return;
                super.onPostExecute(response);
                if (activity.isFinishing())
                    return;

                activity.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        //有下单记录了，就不显示红包引导了
                        UNavConfig.setShowSmallDlg(context, false);
                        UNavConfig.setShowStep01(context, false);

                        if (isQuan) {
                            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "useQuan_Success");
                        } else {
                            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "useCash_Sueecss");
                        }
                        String msg = activity.getResources().getString(R.string.trade_create_success);
                        if (!isGotoTradeOrder) {
                            DialogUtil.showSuccessSmallDialog(context, msg, 0, null);
                        } else {

                            DialogUtil.showTradeSuccessDlg(activity, msg, new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message message) {

                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                                    intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 1);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    activity.startActivity(intent);
                                    return false;
                                }
                            });
                        }
                        //成功的逻辑处理
                        if (dialog != null)
                            dialog.dismiss();

                        createOrderSuccess();
                    } else {
                        MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "closeError_code_" + ConvertUtil.NVL(response.getErrorCode(), ""));

                        //token过期
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
//                            if (!StringUtil.isEmpty(response.getErrorInfo())) {
//                                activity.showCusToast(response.getErrorInfo());
//                            }
                            //重新登录
                            if (tokenDialog != null)
                                tokenDialog.dismiss();//避免多个dialog 重复出现
                            DialogUtil.showTokenDialog(activity, TradeConfig.getCurrentTradeCode(context),
                                    new DialogUtil.AuthTokenDlgShow() {
                                        @Override
                                        public void onDlgShow(Dialog dlg) {
                                            tokenDialog = dlg;
                                        }
                                    }, new DialogUtil.AuthTokenCallBack() {
                                        @Override
                                        public void onPostClick(Object obj) {

                                        }

                                        @Override
                                        public void onNegClick() {

                                        }
                                    });

                            return;
                        }
                        //错误提示信息
                        String failMsg = activity.getResources().getString(R.string.trade_create_fail);
                        DialogUtil.showMsgDialog(activity, ConvertUtil.NVL(response.getErrorInfo(), failMsg), "确定");

//                        if (!StringUtil.isEmpty(response.getErrorInfo())) {
//                            activity.showCusToast(response.getErrorInfo());
//
//                        } else
//                            activity.showCusToast(activity.getResources().getString(R.string.trade_create_fail));
                    }
                } else {
                    activity.showCusToast(activity.getResources().getString(R.string.trade_create_fail));
                }


            }
        }.execute();
    }

    /**
     * 一个点收益多少钱
     *
     * @param product
     */
    void changPointTips(TradeProduct product) {
//        String tips = context.getResources().getString(R.string.quicktrade_pointtips, product.getMoneyByPoint(1, countSize) + "");
//        text_ratetips.setText(tips);
    }

    //tv设置需要支付的金额，手续费
    void setBuyTotalMoneyFee() {
        String feeReg = context.getResources().getString(R.string.quick_fee_str);

        //先设置代金券张数
        TradeProduct product = getTradeProduct();
        changPointTips(product);
        tv_quanCount.setText(getPVoucher(product) + "");

        seekBarUtil.updateSeekMoney(getTradeProduct(), isQuan, countSize);

        if (isQuan) {
            tv_fee.setText(String.format(feeReg, "0"));
            tv_totalMoney.setText(countSize + "");
            tv_unit.setText("张代金券");
            return;
        }
        tv_unit.setText("元");
        double feePrice = 0;

        if (product == null)
            return;
        try {
            feePrice = Double.parseDouble(product.getSxf().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //手续费
        double moneyFee = 0D;
        try {
            moneyFee = NumberUtil.multiply(feePrice, (double) countSize);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double total = getBuyTotalMoney();
        tv_fee.setText(String.format(feeReg, NumberUtil.moveLast0(moneyFee)));
        tv_totalMoney.setText(NumberUtil.moveLast0(total));
    }

    /**
     * 购买所需总金额
     *
     * @return
     */
    double getBuyTotalMoney() {
        //先设置代金券张数
        TradeProduct product = getTradeProduct();

        double everyPrice = 0, feePrice = 0;

        if (product == null)
            return 0;
        try {
            everyPrice = Double.parseDouble(product.getPrice().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            feePrice = Double.parseDouble(product.getSxf().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //总价
        double money = NumberUtil.multiply(everyPrice, (double) countSize);
        //手续费
        double moneyFee = 0D;
        try {
            moneyFee = NumberUtil.multiply(feePrice, (double) countSize);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double total = NumberUtil.add(money, moneyFee);
        return total;
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                getData();

                return optionalList;
            }

            @Override
            public void onUpdate(Object result) {
                updatePrice(optionalList);
            }
        });
    }


    //筛选出数据 用来确认油还是银
    List<List<TradeProduct>> filterData(List<List<TradeProduct>> getlist) {
        List<List<TradeProduct>> list = new ArrayList<>();
        if (initProduct != null) {
            if (getlist != null) {
                //获取到数据之后只留下当前传入的品种，用来确认油还是银
                for (List<TradeProduct> item : getlist) {
                    if (initProduct.getContract() != null &&
                            initProduct.getContract().equals(item.get(0).getContract())) {
                        list.add(item);
                        break;
                    }
                }
            }
            return list;
        }
        return getlist;
    }

    List<List<TradeProduct>> optionalList = null;

    /**
     * 获取数据
     */
    void getData() {
        try {
            CommonResponse4List<List<TradeProduct>> response4List = TradeHelp.getTypeProductList(context);
            if (response4List != null && response4List.isSuccess()) {
                optionalList = response4List.getData();
                optionalList = filterData(optionalList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前产品的持有代金券
     *
     * @return
     */
    int getPVoucher(TradeProduct p) {
        if (voucherRes == null)
            return 0;
        if (p == null)
            return 0;
        if (voucherRes != null && voucherRes.size() > 0) {
            for (TradeVoucher v : voucherRes) {
                if (v == null)
                    continue;
                if (v.getCouponId() == p.getCouponId()) {
                    return v.getNumber();
                }
            }
        }
        return 0;
    }

    String blance;
    //代金券的返回res
    List<TradeVoucher> voucherRes;

    /**
     * get total money and get total ticks
     */
    class InitTask extends AsyncTask<String, Void, CommonResponse<TradeInfoData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!context.isProgressDialogShowing())
                context.showNetLoadingProgressDialog(null);
        }

        @Override
        protected CommonResponse<TradeInfoData> doInBackground(String... params) {

            return TradeHelp.getTradeInfo(context);
        }

        @Override
        protected void onPostExecute(CommonResponse<TradeInfoData> result) {
            super.onPostExecute(result);
            if (context == null)
                return;
            if (context.isFinishing())
                return;
            context.hideNetLoadingProgressDialog();
            if (dialog == null)
                return;

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    //成功了才显示
                    if (!dialog.isShowing())
                        dialog.show();

                    //成功之后才开始刷新
                    //是否使用外面的 list 一起更新
                    if (isRefreshSelf) {
                        if (refreshUtil != null)
                            refreshUtil.start();
                    }

                    if (result.getData() != null) {
                        blance = result.getData().getBalance();
                        if (result.getData().getVoucherList() != null) {
                            voucherRes = result.getData().getVoucherList();
                        }
                        if(result.getData().getSpecialCard()!=null){
                            text_giftname.setVisibility(View.VISIBLE);
                            text_giftremark.setVisibility(View.VISIBLE);
                            text_giftname.setText(result.getData().getSpecialCard().getGiftName());
                            text_giftremark.setText(result.getData().getSpecialCard().getGiftOrderRemark());
                        }else{
                            text_giftname.setVisibility(View.GONE);
                            text_giftremark.setVisibility(View.GONE);
                        }
                    }
                    if (tv_balance != null)
                        tv_balance.setText(blance + "元");

                    tv_quanCount.setText(getPVoucher(initProduct) + "");
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        //重新登录
                        showTokenDialog();
                        return;
                    }
                    context.showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

            } else {
                context.showCusToast("网络连接失败！");
            }
        }
    }

    void showTokenDialog() {
        if (dialog == null)
            return;
        if (context.isFinishing())
            return;
        //重复显示
        if (tokenDialog != null) {
            if (tokenDialog.isShowing())
                return;
        }

        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context),
                new DialogUtil.AuthTokenDlgShow() {
                    @Override
                    public void onDlgShow(Dialog dlg) {
                        tokenDialog = dlg;
                    }
                }, new DialogUtil.AuthTokenCallBack() {
                    @Override
                    public void onPostClick(Object obj) {

                    }

                    @Override
                    public void onNegClick() {

                    }
                });

        return;
    }

    @Override
    public void setGobackViewVisible(boolean isVisible) {
        if (isVisible) {
            gobackView.setVisibility(View.VISIBLE);
        }
    }

    boolean isGotoTradeOrder = true;

    @Override
    public void isGotoTradeOrder(boolean isGotoTradeOrder) {
        this.isGotoTradeOrder = isGotoTradeOrder;
    }
}
