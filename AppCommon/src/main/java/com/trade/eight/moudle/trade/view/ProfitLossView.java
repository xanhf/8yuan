package com.trade.eight.moudle.trade.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.home.trade.ProfitLossObj;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;

/**
 * Created by dufangzhu on 2017/5/26.
 * 止盈止损空间   使用时需调用初始化方法 init(ProductObj obj)
 */

public class ProfitLossView extends LinearLayout implements View.OnClickListener {
    Context context;
    ProfitLossObj productObj;
    /*购买方向*/
    int typeBuy = ProductObj.TYPE_BUY_UP;
    /*建仓面板里的手数*/
    EditText ed_num;

    View lossViewLeft, lossViewRight, lessLossView, lessLossImg, addLossView, addLossImg,
            profitViewLeft, profitViewRight, lessProfitView, lessProfitImg, addProfitView, addProfitImg;
    EditText ed_loss, ed_profit;
    TextView tv_lossHint, tv_profitHint;

    public ProfitLossView(Context context) {
        super(context);
        initView(context);
    }

    public ProfitLossView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public ProfitLossView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    void initView(Context context) {
        this.context = context;
        View view = View.inflate(context, R.layout.layout_profit_loss, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(view);
        //止损
        lossViewLeft = view.findViewById(R.id.lossViewLeft);
        lossViewRight = view.findViewById(R.id.lossViewRight);
        lessLossView = view.findViewById(R.id.lessLossView);
        lessLossImg = view.findViewById(R.id.lessLossImg);
        addLossView = view.findViewById(R.id.addLossView);
        addLossImg = view.findViewById(R.id.addLossImg);
        ed_loss = (EditText) view.findViewById(R.id.ed_loss);

        //止盈
        profitViewLeft = view.findViewById(R.id.profitViewLeft);
        profitViewRight = view.findViewById(R.id.profitViewRight);
        lessProfitView = view.findViewById(R.id.lessProfitView);
        lessProfitImg = view.findViewById(R.id.lessProfitImg);
        addProfitView = view.findViewById(R.id.addProfitView);
        addProfitImg = view.findViewById(R.id.addProfitImg);
        ed_profit = (EditText) view.findViewById(R.id.ed_profit);

        tv_lossHint = (TextView) view.findViewById(R.id.tv_lossHint);
        tv_profitHint = (TextView) view.findViewById(R.id.tv_profitHint);


        lossViewLeft.setOnClickListener(this);
        profitViewLeft.setOnClickListener(this);
        lessLossView.setOnClickListener(this);
        addLossView.setOnClickListener(this);
        lessProfitView.setOnClickListener(this);
        addProfitView.setOnClickListener(this);

        ed_loss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLossHint();
                try {
                    if (StringUtil.isEmpty(s.toString())) {
                        return;
                    }

                    double d = Double.parseDouble(s.toString());
                    int point = ProFormatConfig.getProPoint(productObj.getExcode(), productObj.getCode());
                    if (point != -1
                            && NumberUtil.getPointPow(s.toString()) > point) {
                        ed_loss.setText(ProFormatConfig.format(d, point, BigDecimal.ROUND_DOWN).toString());
                        ed_loss.setSelection(ed_loss.getText().length());
                        return;
                    }

                    double initLossP = getLossPrice(productObj.getMinStopLoss(), false);
                    if (productObj instanceof TradeOrder) {
                        initLossP = getLossPrice(productObj.getMinStopLoss(), true);
                    }
                    if (typeBuy == ProductObj.TYPE_BUY_UP) {
                        //buy d <= initLossP
                        if (d < initLossP) {
                            lessLossImg.setEnabled(true);
                            lessLossView.setEnabled(true);

                            addLossImg.setEnabled(true);
                            addLossView.setEnabled(true);
                        } else if (d >= initLossP) {
                            lessLossImg.setEnabled(true);
                            lessLossView.setEnabled(true);

                            addLossImg.setEnabled(false);
                            addLossView.setEnabled(false);
                        }
                    } else {
                        //sell d >= initLossP
                        if (d <= initLossP) {
                            lessLossImg.setEnabled(false);
                            lessLossView.setEnabled(false);

                            addLossImg.setEnabled(true);
                            addLossView.setEnabled(true);
                        } else if (d > initLossP) {
                            lessLossImg.setEnabled(true);
                            lessLossView.setEnabled(true);

                            addLossImg.setEnabled(true);
                            addLossView.setEnabled(true);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_profit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setProfitHint();
                try {
                    if (StringUtil.isEmpty(s.toString())) {
                        return;
                    }
                    //小数点控制
                    double d = Double.parseDouble(s.toString());
                    int point = ProFormatConfig.getProPoint(productObj.getExcode(), productObj.getCode());
                    if (point != -1
                        && NumberUtil.getPointPow(s.toString()) > point) {
                        ed_profit.setText(ProFormatConfig.format(d, point, BigDecimal.ROUND_DOWN).toString());
                        ed_profit.setSelection(ed_profit.getText().length());
                        return;
                    }

                    double initP = getProfitPrice(productObj.getMinStopProfile(), false);
                    if (productObj instanceof TradeOrder) {
                        initP = getProfitPrice(productObj.getMinStopProfile(), true);
                    }
                    if (typeBuy == ProductObj.TYPE_BUY_UP) {
                        //buy d >= initP
                        if (d <= initP) {
                            lessProfitImg.setEnabled(false);
                            lessProfitView.setEnabled(false);

                            addProfitImg.setEnabled(true);
                            addProfitView.setEnabled(true);
                        } else if (d > initP) {
                            lessProfitImg.setEnabled(true);
                            lessProfitView.setEnabled(true);

                            addProfitImg.setEnabled(true);
                            addProfitView.setEnabled(true);
                        }
                    } else {
                        //sell d <= initP
                        if (d < initP) {
                            lessProfitImg.setEnabled(true);
                            lessProfitView.setEnabled(true);

                            addProfitImg.setEnabled(true);
                            addProfitView.setEnabled(true);
                        } else if (d >= initP) {
                            lessProfitImg.setEnabled(true);
                            lessProfitView.setEnabled(true);

                            addProfitImg.setEnabled(false);
                            addProfitView.setEnabled(false);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void init(ProfitLossObj obj, int typeBuy, EditText ed_num) {
        this.productObj = obj;
        this.typeBuy = typeBuy;
        this.ed_num = ed_num;
        if (obj instanceof TradeOrder) {
            //持仓界面的止盈止损,持仓界面 判断是否有已经设置的止盈止损
            if (obj.getStopLossPoint() <= obj.getMinStopLoss()) {
                lossEnable(false);
            } else {
                lossEnable(true);
            }
            if (obj.getStopProfitPoint() <= obj.getMinStopProfile()) {
                profitEnable(false);
            } else {
                profitEnable(true);
            }
            setLossProfitHint();
        } else {
            lossEnable(false);
            profitEnable(false);
            setLossProfitHint();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lossViewLeft) {
            //选中check box
            //当前是可编辑状态，再点击变成不可编辑
            boolean enable = v.isSelected();
            lossEnable(!enable);


        } else if (id == R.id.lessLossView) {
            lessAddLossNum(false);

        } else if (id == R.id.addLossView) {
            lessAddLossNum(true);
        }
        if (id == R.id.profitViewLeft) {
            //选中check box
            //当前是可编辑状态，再点击变成不可编辑
            boolean enable = v.isSelected();
            profitEnable(!enable);

        } else if (id == R.id.lessProfitView) {
            lessAddProfitNum(false);

        } else if (id == R.id.addProfitView) {
            lessAddProfitNum(true);
        }
    }

    /**
     * @param enable 可以使用
     */
    public void lossEnable(boolean enable) {
        if (productObj == null)
            return;
        if (!enable) {
            //设置未不能编辑
            lossViewLeft.setSelected(false);
            lessLossView.setEnabled(false);
            lessLossImg.setEnabled(false);
            addLossView.setEnabled(false);
            addLossImg.setEnabled(false);
            ed_loss.setEnabled(false);

            //输入框
            ed_loss.setText("");
            tv_lossHint.setText("");
        } else {
            double p = 0;
            if (typeBuy == ProductObj.TYPE_BUY_UP) {
                //买涨的止损价格<= buy-minStopLoss   初始值不能再加
                lossViewLeft.setSelected(true);
//                lessLossView.setEnabled(true);
//                lessLossImg.setEnabled(true);
//                addLossView.setEnabled(false);
//                addLossImg.setEnabled(false);
                ed_loss.setEnabled(true);

                p = getLossPrice(productObj.getMinStopLoss(), true);

                if (productObj instanceof TradeOrder) {
                    //设置止盈止损
                    if (productObj.getStopLossPoint() != 0) {
                        //如果有设置的有值，使用已经设置的值
                        double stopLossPoint = productObj.getStopLossPoint();
                        if (stopLossPoint < p) {
//                            addLossView.setEnabled(true);
//                            addLossImg.setEnabled(true);
                        }
                        p = stopLossPoint;
                    }
                }

            } else {
                //买跌的止损价格>= sell+minStopLoss   初始值不能再减少
                lossViewLeft.setSelected(true);
//                lessLossView.setEnabled(false);
//                lessLossImg.setEnabled(false);
//                addLossView.setEnabled(true);
//                addLossImg.setEnabled(true);
                ed_loss.setEnabled(true);

                p = getLossPrice(productObj.getMinStopLoss(), true);

                if (productObj instanceof TradeOrder) {
                    //设置止盈止损
                    if (productObj.getStopLossPoint() != 0) {
                        //如果有设置的有值，使用已经设置的值
                        double stopLossPoint = productObj.getStopLossPoint();
                        if (stopLossPoint > p) {
//                            lessLossView.setEnabled(true);
//                            lessLossImg.setEnabled(true);
                        }
                        p = stopLossPoint;
                    }
                }
            }

            ed_loss.setText(NumberUtil.moveLast0(p));
            ed_loss.setSelection(ed_loss.getText().length());
        }
    }

    /**
     * @param enable 可以使用
     */
    public void profitEnable(boolean enable) {
        if (productObj == null)
            return;
        if (!enable) {
            //设置未不能编辑
            profitViewLeft.setSelected(false);
            lessProfitView.setEnabled(false);
            lessProfitImg.setEnabled(false);
            addProfitView.setEnabled(false);
            addProfitImg.setEnabled(false);
            ed_profit.setEnabled(false);

            ed_profit.setText("");
            tv_profitHint.setText("");
        } else {
            double p = 0;
            if (typeBuy == ProductObj.TYPE_BUY_UP) {
                profitViewLeft.setSelected(true);
//                lessProfitView.setEnabled(false);
//                lessProfitImg.setEnabled(false);
//                addProfitView.setEnabled(true);
//                addProfitImg.setEnabled(true);
                ed_profit.setEnabled(true);

                //买涨的止盈价格>=buy + getMinStopProfile  初始值不能再减
                p = getProfitPrice(productObj.getMinStopProfile(), true);

                if (productObj instanceof TradeOrder) {
                    //设置止盈止损
                    if (productObj.getStopProfitPoint() != 0) {
                        //如果有设置的有值，使用已经设置的值
                        double stopProfitPoint = productObj.getStopProfitPoint();
                        if (stopProfitPoint > p) {
//                            lessProfitView.setEnabled(true);
//                            lessProfitImg.setEnabled(true);
                        }
                        p = stopProfitPoint;
                    }
                }
            } else {
                //买跌的止盈价格<=sell - getMinStopProfile  初始值不能再加号
                profitViewLeft.setSelected(true);
//                lessProfitView.setEnabled(true);
//                lessProfitImg.setEnabled(true);
//                addProfitView.setEnabled(false);
//                addProfitImg.setEnabled(false);
                ed_profit.setEnabled(true);

                p = getProfitPrice(productObj.getMinStopProfile(), true);

                if (productObj instanceof TradeOrder) {
                    //设置止盈止损
                    if (productObj.getStopProfitPoint() != 0) {
                        //如果有设置的有值，使用已经设置的值
                        double stopProfitPoint = productObj.getStopProfitPoint();
                        if (stopProfitPoint < p) {
//                            addProfitView.setEnabled(true);
//                            addProfitImg.setEnabled(true);
                        }
                        p = stopProfitPoint;
                    }
                }

            }


            ed_profit.setText(NumberUtil.moveLast0(p));
            ed_profit.setSelection(ed_profit.getText().length());

        }
    }

    /**
     * 止损加减号的点击
     *
     * @param isAdd true表示加号
     */
    void lessAddLossNum(boolean isAdd) {
        try {
            if (StringUtil.isEmpty(ed_loss.getText().toString()))
                return;
            double d = Double.parseDouble(ed_loss.getText().toString());
            //对不同品种的最小移动单位处理
//            int scal = NumberUtil.getPointPow(productObj.getMinMovePoint());
//            d = NumberUtil.roundUpDouble(d, scal);
            if (isAdd) {
                d = NumberUtil.add(d, productObj.getMinMovePoint());
            } else {
                d = NumberUtil.subtract(d, productObj.getMinMovePoint());
            }
//            if (typeBuy == ProductObj.TYPE_BUY_UP) {
//                //买涨的止损价格<= buy-getMinStopLoss
//                double price = getLossPrice(productObj.getMinStopLoss(), false);
//                if (d >= price) {
////                    d = price;
//                    //价格可以减不能再加
//                    lessLossView.setEnabled(true);
//                    lessLossImg.setEnabled(true);
//
//                    addLossView.setEnabled(false);
//                    addLossImg.setEnabled(false);
//                } else {
//                    //价格可以减  也可以加
//                    lessLossImg.setEnabled(true);
//                    lessLossView.setEnabled(true);
//                    addLossView.setEnabled(true);
//                    addLossImg.setEnabled(true);
//                }
//            } else {
//                //买跌的止损价格>= sell+getMinStopLoss
//                double price = getLossPrice(productObj.getMinStopLoss(), false);
//                if (d <= price) {
////                    d = price;
//                    //价格可以减不能再加
//                    lessLossView.setEnabled(false);
//                    lessLossImg.setEnabled(false);
//
//                    addLossView.setEnabled(true);
//                    addLossImg.setEnabled(true);
//                } else {
//                    //价格可以减  也可以加
//                    lessLossImg.setEnabled(true);
//                    lessLossView.setEnabled(true);
//                    addLossView.setEnabled(true);
//                    addLossImg.setEnabled(true);
//                }
//            }

            ed_loss.setText(NumberUtil.moveLast0(d + ""));
            ed_loss.setSelection(ed_loss.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 止赢加减号的点击
     *
     * @param isAdd true表示加号
     */
    void lessAddProfitNum(boolean isAdd) {
        try {
            if (StringUtil.isEmpty(ed_profit.getText().toString()))
                return;
            double d = Double.parseDouble(ed_profit.getText().toString());
            //对不同品种的最小移动单位处理
//            int scal = NumberUtil.getPointPow(productObj.getMinMovePoint());
//            d = NumberUtil.roundUpDouble(d, scal);
            if (isAdd) {
                d = NumberUtil.add(d, productObj.getMinMovePoint());
            } else {
                d = NumberUtil.subtract(d, productObj.getMinMovePoint());
            }

//            if (typeBuy == ProductObj.TYPE_BUY_UP) {
//                //买涨的时候止盈价格>=buy+getMinStopProfile
//                double price = getProfitPrice(productObj.getMinStopProfile(), false);
//                if (d <= price) {
////                    d = price;
//                    lessProfitImg.setEnabled(false);
//                    lessProfitView.setEnabled(false);
//                    addProfitView.setEnabled(true);
//                    addProfitImg.setEnabled(true);
//                } else {
//                    lessProfitImg.setEnabled(true);
//                    lessProfitView.setEnabled(true);
//                    addProfitView.setEnabled(true);
//                    addProfitImg.setEnabled(true);
//                }
//            } else {
//                //买跌的时候止盈价格<=sell-getMinStopProfile
//                double price = getProfitPrice(productObj.getMinStopProfile(), false);
//                if (d >= price) {
////                    d = price;
//                    lessProfitImg.setEnabled(true);
//                    lessProfitView.setEnabled(true);
//                    addProfitView.setEnabled(false);
//                    addProfitImg.setEnabled(false);
//                } else {
//                    lessProfitImg.setEnabled(true);
//                    lessProfitView.setEnabled(true);
//                    addProfitView.setEnabled(true);
//                    addProfitImg.setEnabled(true);
//                }
//            }

            ed_profit.setText(NumberUtil.moveLast0(d + ""));
            ed_profit.setSelection(ed_profit.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前的止损
     *
     * @return 0表示不设置
     */
    public double getLoss() {
        try {
            //没有开启
            if (!lossViewLeft.isSelected())
                return 0;
            String s = ed_loss.getText().toString();
            if (StringUtil.isEmpty(s))
                return 0;
            return new BigDecimal(s).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前的止盈
     *
     * @return 0表示不设置
     */
    public double getProfit() {
        try {
            //没有开启
            if (!profitViewLeft.isSelected())
                return 0;
            String s = ed_profit.getText().toString();
            if (StringUtil.isEmpty(s))
                return 0;
            return new BigDecimal(s).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setTypeBuy(int typeBuy) {
        this.typeBuy = typeBuy;
    }

    public void setProductObj(ProductObj productObj) {
        this.productObj = productObj;
    }

    public void setLossProfitHint() {
        setProfitHint();
        setLossHint();
    }

    /**
     * 设置止盈提示价
     */
    public void setProfitHint() {
        try {
            if (StringUtil.isEmpty(ed_profit.getText().toString())) {
                tv_profitHint.setText("");
                return;
            }
            if (ed_num != null
                    && StringUtil.isEmpty(ed_num.getText().toString())) {
                tv_profitHint.setText("");
                return;
            }
            double num = 0;
            if (ed_num != null) {
                num = new BigDecimal(ed_num.getText().toString()).doubleValue();
                if (num == 0) {
                    tv_profitHint.setText("");
                    return;
                }
            }
            if (productObj instanceof TradeOrder) {
                num = new BigDecimal(productObj.getOrderNumber()).doubleValue();
            }

            //止盈
            if (StringUtil.isEmpty(ed_profit.getText().toString())) {
                tv_profitHint.setText("");
            }
            double profitPrice = Double.parseDouble(ed_profit.getText().toString());
            double profitPoint = 0, price = 0;
            double subPoint = 0;
            //止盈价格
            if (typeBuy == ProductObj.TYPE_BUY_UP) {
                if (productObj instanceof TradeOrder) {
                    //持仓界面算止盈止损 用建仓价格
                    price = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                } else {
                    price = new BigDecimal(productObj.getBuy()).doubleValue();
                }
                //买涨的时候 止盈点数=止赢价格-建仓的buy价格
                subPoint = NumberUtil.subtract(profitPrice, price);
            } else {
                if (productObj instanceof TradeOrder) {
                    //持仓界面算止盈止损 用建仓价格
                    price = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                } else {
                    price = new BigDecimal(productObj.getSell()).doubleValue();
                }
                //买跌的时候 止盈点数=建仓的sell价格-止赢价格
                subPoint = NumberUtil.subtract(price, profitPrice);
            }
            String profitReg = context.getString(R.string.profit_point_hint);
            //止盈金额
            double profitMoney = productObj.getMoneyByPoint(subPoint, num);
            //会出现负数 显示为0
            if (profitMoney < 0)
                profitMoney = 0;
            profitReg = String.format(profitReg, NumberUtil.moveLast0(NumberUtil.beautifulDouble(profitMoney, 2)));
            tv_profitHint.setText(Html.fromHtml(profitReg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取止盈价格
     * @param step 止盈点数
     * @param is4HoldInit true表示持仓的止赢初始化
     * @return
     */
    double getProfitPrice(double step, boolean is4HoldInit) {
        double profitPrice = 0D;
        //止盈价格
        if (typeBuy == ProductObj.TYPE_BUY_UP) {
            if (productObj instanceof TradeOrder) {
                //持仓界面算止盈止损 用建仓价格
                profitPrice = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                if (is4HoldInit)
                    profitPrice = new BigDecimal(((TradeOrder) productObj).getCurrentPrice()).doubleValue();
            } else {
                profitPrice = new BigDecimal(productObj.getSell()).doubleValue();
            }
            //买涨的时候 止盈价格=sell价格+止赢点数
            profitPrice = NumberUtil.add(profitPrice, step);
        } else {
            if (productObj instanceof TradeOrder) {
                //持仓界面算止盈止损 用建仓价格
                profitPrice = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                if (is4HoldInit)
                    profitPrice = new BigDecimal(((TradeOrder) productObj).getCurrentPrice()).doubleValue();
            } else {
                profitPrice = new BigDecimal(productObj.getBuy()).doubleValue();
            }
            //买跌的时候 止盈价格=buy价格-止赢点数
            profitPrice = NumberUtil.subtract(profitPrice, step);
        }
        return profitPrice;
    }

    /**
     * 获取止损价格
     * @param step 止损点数
     * @param is4HoldInit 持仓的止损初始化
     * @return
     */
    double getLossPrice(double step, boolean is4HoldInit) {
        double lossPrice = 0D;
        //止损价格
        if (typeBuy == ProductObj.TYPE_BUY_UP) {
            if (productObj instanceof TradeOrder) {
                //持仓界面算止盈止损 用建仓价格
                lossPrice = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                if (is4HoldInit)
                    lossPrice = new BigDecimal(((TradeOrder) productObj).getCurrentPrice()).doubleValue();
            } else {
                lossPrice = new BigDecimal(productObj.getSell()).doubleValue();
            }
            //买涨的时候 止损价格=sell价格-止损点数
            lossPrice = NumberUtil.subtract(lossPrice, step);
        } else {
            if (productObj instanceof TradeOrder) {
                //持仓界面算止盈止损 用建仓价格
                lossPrice = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                if (is4HoldInit)
                    lossPrice = new BigDecimal(((TradeOrder) productObj).getCurrentPrice()).doubleValue();
            } else {
                lossPrice = new BigDecimal(productObj.getBuy()).doubleValue();
            }
            //买跌的时候 止损价格=buy价格+止损点数
            lossPrice = NumberUtil.add(lossPrice, step);
        }
        return lossPrice;
    }

    /**
     * 设置止损提示价
     */
    public void setLossHint() {
        //止损
        try {
            if (StringUtil.isEmpty(ed_loss.getText().toString())) {
                tv_lossHint.setText("");
                return;
            }

            if (ed_num != null
                    && StringUtil.isEmpty(ed_num.getText().toString())) {
                tv_lossHint.setText("");
                return;
            }
            double num = 0;
            if (ed_num != null) {
                num = new BigDecimal(ed_num.getText().toString()).doubleValue();
                if (num == 0) {
                    tv_lossHint.setText("");
                    return;
                }
            }
            if (productObj instanceof TradeOrder) {
                num = new BigDecimal(productObj.getOrderNumber()).doubleValue();
            }
            double lossPrice = Double.parseDouble(ed_loss.getText().toString());
            double lossPoint = 0, price = 0;
            double subPoint =0;
            //止损价格
            if (typeBuy == ProductObj.TYPE_BUY_UP) {
                if (productObj instanceof TradeOrder) {
                    //持仓界面算止盈止损 用建仓价格
                    price = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                } else {
                    price = new BigDecimal(productObj.getBuy()).doubleValue();
                }
                //止损点数 = 止损价格-建仓buy价格
                subPoint = NumberUtil.subtract(lossPrice, price);
                //这里显示正的
//                subPoint = Math.abs(subPoint);
            } else {
                if (productObj instanceof TradeOrder) {
                    //持仓界面算止盈止损 用建仓价格
                    price = new BigDecimal(productObj.getCreatePrice()).doubleValue();
                } else {
                    price = new BigDecimal(productObj.getSell()).doubleValue();
                }
                //止损点数 = 建仓sell价格-止损价格
                subPoint = NumberUtil.subtract(price, lossPrice);
                //这里显示正的
//                subPoint = Math.abs(subPoint);
            }
            String lossReg = context.getString(R.string.loss_point_hint);
            //止损金额
            double lossMoney = productObj.getMoneyByPoint(subPoint, num);
            //止损输入的  其实是赚钱了
            if (lossMoney > 0)
                lossMoney = 0;
            lossMoney = Math.abs(lossMoney);
            lossReg = String.format(lossReg, NumberUtil.moveLast0(NumberUtil.beautifulDouble(lossMoney, 2)));
            tv_lossHint.setText(Html.fromHtml(lossReg));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
