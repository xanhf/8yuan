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
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.trade.TradeVoucher;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.refresh.RefreshUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu 大部分代码来自TradeCreateUtil做了修改
 * 快速建仓 直播室里头
 */
public class QuickTradeUtilGG extends QuickTradeUtil{
    public static final String TAG = "QuickTradeUtilGG";
    public static final String MOBCLICK_TAG = "QuickTradeUtil";
    int typeBuy = TradeProduct.TYPE_BUY_UP;

    TradeProduct initProduct;//传入进来的选中的类型，确定是8，80，200
    public static final int COUNT_SIZE01 = 1;
    public static final int COUNT_SIZE02 = 5;
    public static final int COUNT_SIZE03 = 10;
    int countSize = COUNT_SIZE01;
    View selectCountV, selectMoneyV, selectUpOrDownV, selectTabV;
    Button btn_submit;
    TextView tv_size01, tv_balance, tv_quanCount, tv_unit,
            tv_size02, tv_size03, tv_mongey01, tv_mongey02, tv_mongey03, tv_sizeOther;
    TextView tv_title01, tv_title02,tv_title03;
    SeekBar seekBarZhisun, seekBarZhiying;
    View upView, downView;
    Dialog dialog;
    BaseActivity context;
    TextView tv_totalMoney, tv_fee;
    RefreshUtil refreshUtil;
    View quickSizeView;
    TradeCountUtil countUtil;//暂时用不到；使用外面的 list 一起更新
    ImageView imgQuanSwitch;
    boolean isQuan = false;//是否使用代金券
    private boolean isRefreshSelf = false;//自己内部刷新
    Button btnZhisunLess, btnZhisunAdd, btnZhiyingLess, btnZhiyingAdd;
    TextView tv_zhisunValue, tv_zhiyingValue, tv_maxZhisun, tv_maxZhiying,
            tv_zhisunMoney, tv_zhiyingMoney;
    SeekBarUtil seekBarUtil;

    /**
     * 建仓页面dlg
     *
     * @param c
     * @param type     买涨买跌
     * @param p        选中的产品类型
     * @param initList 外部已经查询出的产品list
     */
    public QuickTradeUtilGG(BaseActivity c, int type, TradeProduct p, List<List<TradeProduct>> initList) {
        if (dialog != null && dialog.isShowing())
            return;

        try {
            this.context = c;
            this.typeBuy = type;
            this.initProduct = p;

            if (!new UserInfoDao(context).isLogin()) {
                context.showCusToast("请登录");
                return;
            }
            dialog = new Dialog(context, R.style.dialog_trade);
            View rootView = View.inflate(context, R.layout.dialog_qucik_trade_create, null);
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
    public void dismissDlg() {
        if (dialog != null)
           dialog.dismiss();
    }

    public void showDialog() {
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(R.style.dialog_trade_ani);
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
            } else if (id == R.id.tv_title01
                    || id == R.id.tv_title02
                    || id == R.id.tv_title03) {
                selectTabV.setSelected(false);
                selectTabV = v;
                selectTabV.setSelected(true);

                setTvMoney();
                initSeekBar();
                setBuyTotalMoneyFee();
            } else if (id == R.id.tv_cashin) {
                context.startActivity(new Intent(context, CashInAct.class));
                dialog.dismiss();
            } else if (id == R.id.imgQuanSwitch) {
                if (isQuan) {
                    imgQuanSwitch.setImageResource(R.drawable.icon_switch_off);
                } else {
                    imgQuanSwitch.setImageResource(R.drawable.icon_switch_on);
                }
                isQuan = !isQuan;
                setBuyTotalMoneyFee();
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
//                countUtil.setCount(countSize);
//                countUtil.initView();
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
                setTvMoney();
//                initSeekBar();
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
        //如果是农交所 需要去掉代金券的方法
//        if (TradeConfig.isCurrentJN(context)) {
//            dialog.findViewById(R.id.quanLayout).setVisibility(View.GONE);
//        }
        tv_title01 = (TextView) dialog.findViewById(R.id.tv_title01);
        selectTabV = tv_title01;
        selectTabV.setSelected(true);
//        tv_price01 = (TextView) dialog.findViewById(R.id.tv_price01);
        tv_title02 = (TextView) dialog.findViewById(R.id.tv_title02);
        tv_title03 = (TextView) dialog.findViewById(R.id.tv_title03);

//        tv_price02 = (TextView) dialog.findViewById(R.id.tv_price02);
        tv_balance = (TextView) dialog.findViewById(R.id.tv_balance);
        tv_quanCount = (TextView) dialog.findViewById(R.id.tv_quanCount);
        tv_unit = (TextView) dialog.findViewById(R.id.tv_unit);


        imgQuanSwitch = (ImageView) dialog.findViewById(R.id.imgQuanSwitch);
        if (!isQuan) {
            imgQuanSwitch.setImageResource(R.drawable.icon_switch_off);
        } else {
            imgQuanSwitch.setImageResource(R.drawable.icon_switch_on);
        }

        upView = dialog.findViewById(R.id.upView);
        downView = dialog.findViewById(R.id.downView);

        quickSizeView = dialog.findViewById(R.id.quickSizeView);
        tv_size01 = (TextView) dialog.findViewById(R.id.tv_size01);
        tv_size02 = (TextView) dialog.findViewById(R.id.tv_size02);
        tv_size03 = (TextView) dialog.findViewById(R.id.tv_size03);
        tv_sizeOther = (TextView) dialog.findViewById(R.id.tv_sizeOther);

        tv_mongey01 = (TextView) dialog.findViewById(R.id.tv_mongey01);
        tv_mongey02 = (TextView) dialog.findViewById(R.id.tv_mongey02);
        tv_mongey03 = (TextView) dialog.findViewById(R.id.tv_mongey03);


        tv_totalMoney = (TextView) dialog.findViewById(R.id.tv_totalMoney);
        tv_fee = (TextView) dialog.findViewById(R.id.tv_fee);

        tv_maxZhisun = (TextView) dialog.findViewById(R.id.tv_maxZhisun);
        tv_maxZhiying = (TextView) dialog.findViewById(R.id.tv_maxZhiying);

        btnZhisunLess = (Button) dialog.findViewById(R.id.btnZhisunLess);
        btnZhisunAdd = (Button) dialog.findViewById(R.id.btnZhisunAdd);
        btnZhiyingLess = (Button) dialog.findViewById(R.id.btnZhiyingLess);
        btnZhiyingAdd = (Button) dialog.findViewById(R.id.btnZhiyingAdd);

        btnZhisunLess.setOnClickListener(onClickListener);
        btnZhisunAdd.setOnClickListener(onClickListener);
        btnZhiyingLess.setOnClickListener(onClickListener);
        btnZhiyingAdd.setOnClickListener(onClickListener);

        tv_title01.setOnClickListener(onClickListener);
        tv_title02.setOnClickListener(onClickListener);
        tv_title03.setOnClickListener(onClickListener);

        upView.setOnClickListener(onClickListener);
        downView.setOnClickListener(onClickListener);

        tv_size01.setOnClickListener(onClickListener);
        tv_size02.setOnClickListener(onClickListener);
        tv_size03.setOnClickListener(onClickListener);
        tv_sizeOther.setOnClickListener(onClickListener);


        tv_size01.setText(String.format(context.getString(R.string.qucik_create_count01), COUNT_SIZE01 + ""));
        tv_size02.setText(String.format(context.getString(R.string.qucik_create_count02), COUNT_SIZE02 + ""));
        tv_size03.setText(String.format(context.getString(R.string.qucik_create_count03), COUNT_SIZE03 + ""));

        imgQuanSwitch.setOnClickListener(onClickListener);
        tv_mongey01.setOnClickListener(onClickListener);
        tv_mongey02.setOnClickListener(onClickListener);
        tv_mongey03.setOnClickListener(onClickListener);

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

        tv_zhisunMoney = (TextView) dialog.findViewById(R.id.tv_zhisunMoney);
        tv_zhiyingMoney = (TextView) dialog.findViewById(R.id.tv_zhiyingMoney);

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
//    public void updatePrice(Optional o) {
//        if (o == null)
//            return;
//        //不是第一次传入的list初始化
//        if (dialog == null || !dialog.isShowing() && isInitFee) {
//            if (refreshUtil != null)
//                refreshUtil.stop();
//            return;
//        }
//        tv_price01.setText(o.getSellone());
//    }

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
//            list = filterData(list);

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

            if (!initedSeekBar) {
                initSeekBar();
                initedSeekBar = true;
            }
            if (list.size() > 0) {
                tv_title01.setText(list.get(0).get(0).getNameByCode(context) + " " + ConvertUtil.NVL(list.get(0).get(0).getSell(), ""));
//            tv_price01.setText(list.get(0).get(0).getSell());
            }
            if (list.size() > 1) {
                tv_title02.setText(list.get(1).get(0).getNameByCode(context) + " " + ConvertUtil.NVL(list.get(1).get(0).getSell(), ""));
//            tv_price02.setText(list.get(1).get(0).getSell());
            } else {
                tv_title02.setVisibility(View.GONE);
            }
            if (list.size() > 2) {
                tv_title03.setText(list.get(2).get(0).getNameByCode(context) + " " + ConvertUtil.NVL(list.get(2).get(0).getSell(), ""));
//            tv_price02.setText(list.get(1).get(0).getSell());
            } else {
                tv_title03.setVisibility(View.GONE);
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

            List<TradeProduct> groupList = cashlist.get(0);
            if (selectTabV.equals(tv_title02)) {
                groupList = cashlist.get(1);
            } else if (selectTabV.equals(tv_title03)) {
                groupList = cashlist.get(2);
            }
            if (groupList.size() > 0) {
                tv_mongey01.setVisibility(View.VISIBLE);
                tv_mongey01.setText(groupList.get(0).getPrice() + "元/手");

                //初始化选中的类型
                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(groupList.get(0).getProductId())) {
                        selectMoneyV = tv_mongey01;
                    }
                }
            } else {
                tv_mongey01.setVisibility(View.GONE);
            }
            if (groupList.size() > 1) {
                tv_mongey02.setVisibility(View.VISIBLE);
                tv_mongey02.setText(groupList.get(1).getPrice() + "元/手");

                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(groupList.get(1).getProductId())) {
                        selectMoneyV = tv_mongey02;
                    }
                }
            } else {
                tv_mongey02.setVisibility(View.GONE);
            }
            if (groupList.size() > 2) {
                tv_mongey03.setVisibility(View.VISIBLE);
                tv_mongey03.setText(groupList.get(2).getPrice() + "元/手");

                if (selectMoneyV == null && initProduct != null) {
                    if (initProduct.getProductId() != null && initProduct.getProductId().equals(groupList.get(2).getProductId())) {
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
            List<TradeProduct> indexList = cashlist.get(0);
            if (selectTabV.equals(tv_title02)) {
                indexList = cashlist.get(1);
            } else if (selectTabV.equals(tv_title03)) {
                indexList = cashlist.get(2);
            }
            //确定品种
            if (selectMoneyV.equals(tv_mongey01)) {
                return indexList.get(0);
            } else if (selectMoneyV.equals(tv_mongey02)) {
                return indexList.get(1);
            } else if (selectMoneyV.equals(tv_mongey03)) {
                return indexList.get(2);
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

//        //弹出确认框
//        if (isQuan) {
//            DialogUtil.tradeCreateConfigDlg(context, typeBuy,
//                    product.getMyCustomName() + " "+countSize + "手",
//                    countSize+"", "张代金券", new Handler.Callback() {
//                @Override
//                public boolean handleMessage(Message message) {
//                    submit(context, isQuan);
//                    return false;
//                }
//            });
//        } else {
//            DialogUtil.tradeCreateConfigDlg(context, typeBuy,
//                    product.getMyCustomName() + " "+countSize + "手",
//                    ""+getBuyTotalMoney(), "元", new Handler.Callback() {
//                        @Override
//                        public boolean handleMessage(Message message) {
//                            submit(context, isQuan);
//                            return false;
//                        }
//                    });
//        }
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
        map.put(TradeProduct.PARAM_PID, product.getProductId());
        map.put(TradeProduct.PARAM_ORDER_NB, countSize + "");
        map.put(TradeProduct.PARAM_TYPE, typeBuy + "");
        map.put(TradeProduct.PARAM_STOPPROFIT, SeekBarUtil.getZhiying(seekBarZhiying, product));
        map.put(TradeProduct.PARAM_STOPLOSS, SeekBarUtil.getZhisun(seekBarZhisun, product));

        if (TradeConfig.isCurrentJN(context)||TradeConfig.isCurrentHN(context)) {
            //农交所是否使过夜
            map.put(TradeProduct.PARAM_IS_DEFERRED, TradeProduct.IS_DEFERRED_YES);
        } else {
            //首先得判断是否使用券
            String juan = TradeProduct.JUAN_DISENABLE;
            if (isQuan)
                juan = TradeProduct.JUAN_ENABLE;
            map.put(TradeProduct.PARAM_ISJUAN, juan + "");
        }
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
                        if (isQuan) {
                            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "useQuan_Success");
                        } else {
                            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "useCash_Sueecss");
                        }
                        String msg = activity.getResources().getString(R.string.trade_create_success);
//                        activity.showCusToast(msg);
                        DialogUtil.showSuccessSmallDialog(context, msg, 0, null);
//                        DialogUtil.showTradeSuccessDlg(activity, msg, new Handler.Callback() {
//                            @Override
//                            public boolean handleMessage(Message message) {
//                                Intent intent = new Intent(activity, MainActivity.class);
//                                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
//                                intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 1);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                activity.startActivity(intent);
//                                return false;
//                            }
//                        });
                        //成功的逻辑处理
                        if (dialog != null)
                            dialog.dismiss();


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


    //tv设置需要支付的金额，手续费
    void setBuyTotalMoneyFee() {
        String feeReg = context.getResources().getString(R.string.quick_fee_str);

        //先设置代金券张数
        TradeProduct product = getTradeProduct();
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
        if (voucherRes.isSuccess()) {
            List<TradeVoucher> list = voucherRes.getData();
            if (list != null && list.size() > 0) {
                for (TradeVoucher v : list) {
                    if (v == null)
                        continue;
                    if (v.getCouponId() == p.getCouponId()) {
                        return v.getNumber();
                    }
                }
            }
        }
        return 0;
    }

    String blance;
    //代金券的返回res
    CommonResponse4List<TradeVoucher> voucherRes;

    /**
     * get total money and get total ticks
     */
    class InitTask extends AsyncTask<String, Void, CommonResponse<UserInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!context.isProgressDialogShowing())
                context.showNetLoadingProgressDialog(null);
        }

        @Override
        protected CommonResponse<UserInfo> doInBackground(String... params) {
            //取所有代金券
            try {
                if (TradeConfig.isCurrentGG(context)
                        || TradeConfig.isCurrentHG(context)) {
                    voucherRes = TradeHelp.getTradeVoucher(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return TradeHelp.getUserBalance(context);
        }

        @Override
        protected void onPostExecute(CommonResponse<UserInfo> result) {
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
                    }
                    if (tv_balance != null)
                        tv_balance.setText(blance + "元");

                    tv_quanCount.setText(getPVoucher(initProduct) + "");
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
//                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
//                            ((BaseActivity) context).showCusToast(result.getErrorInfo());
//                        }
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

    public boolean isRefreshSelf() {
        return isRefreshSelf;
    }

    public void setRefreshSelf(boolean refreshSelf) {
        isRefreshSelf = refreshSelf;
    }
}

