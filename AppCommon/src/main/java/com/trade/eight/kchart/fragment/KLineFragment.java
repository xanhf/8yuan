package com.trade.eight.kchart.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.kchart.chart.candle.KLineView;
import com.trade.eight.kchart.chart.cross.KCrossLineView;
import com.trade.eight.kchart.data.KLineHelper;
import com.trade.eight.kchart.entity.KCandleObj;
import com.trade.eight.kchart.entity.KLineNormal;
import com.trade.eight.kchart.listener.OnKChartClickListener;
import com.trade.eight.kchart.listener.OnKCrossLineMoveListener;
import com.trade.eight.kchart.listener.OnKLineTouchDisableListener;
import com.trade.eight.kchart.util.KDisplayUtil;
import com.trade.eight.kchart.util.KNumberUtil;
import com.trade.eight.kchart.util.KParamConfig;
import com.trade.eight.kchart.util.KParseUtils;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.Date;
import java.util.List;


/**
 * android:hardwareAccelerated="true"
 * 硬件加速问题  导致多层次的view重新绘制
 * 设置hardwareAccelerated true
 */
public class KLineFragment extends BaseFragment implements OnKCrossLineMoveListener, OnKLineTouchDisableListener, OnKChartClickListener {
    public static final String TAG = "KLineFragment";
    KLineView kLineView;
    KCrossLineView crossLineView;
    LinearLayout crossInfoView;
    TextView tv_time, tv_open, tv_close, tv_high, tv_low, tv_rate, tv_rateChange;

    private Activity mActivity;

    View layoutLoding = null, layoutContent = null;
    View mainNormal, subNormal;
    //默认值 也就是NormUnionCandleStickChart的默认值  modify by fangzhu
    private int lastBottomNorm = KLineNormal.NORMAL_MACD;
    private int lastTopNorm = KLineNormal.NORMAL_SMA;

    View mainNormalView, subNormalView, mainNormalViewLand, subNormalViewLand;
    View landTypeView;

    View rootView;
    float mainF = 4 / 5F;//竖屏时候主图占整体的高度
    float subF = 1 / 5F;//竖屏时候附图占整体的高度
    float lanMainF = 2 / 3F;//横屏时候主图占整体的高度
    float lanSubF = 1 / 3F;//横屏时候附图占整体的高度
    //传入的code
    String type, code;
    String cycle;//周期

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = activity;

    }


    public static KLineFragment newInstance(Bundle bundle) {
        KLineFragment fragment = new KLineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //处理回收 如home键后长时间
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("toptype", lastTopNorm);
        outState.putInt("bottomtype", lastBottomNorm);
    }


    private GetKlineDataTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView--");
//        lastBottomNorm = AppSetting.getInstance(getActivity()).getKlineNormalSubChart();
        if (savedInstanceState != null) {
            lastTopNorm = savedInstanceState.getInt("toptype", KLineNormal.NORMAL_SMA);
            lastBottomNorm = savedInstanceState.getInt("bottomtype", lastBottomNorm);
        }
        type = getArguments().getString("type");
        code = getArguments().getString("code");
        cycle = getArguments().getString("interval");
        rootView = inflater.inflate(R.layout.frag_kline, null);
        initView(rootView);
        initListener();
        task = new GetKlineDataTask();
        task.execute();


        layoutContent = rootView.findViewById(R.id.layoutContent);
        layoutLoding = rootView.findViewById(R.id.layoutLoding);

        return rootView;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (task != null && task.getStatus() == Status.RUNNING) {
            task.cancel(true);
        }
    }


    public void initView(View view) {
        kLineView = (KLineView) view.findViewById(R.id.klineView);
        landTypeView = view.findViewById(R.id.landTypeView);

        crossLineView = (KCrossLineView) view.findViewById(R.id.crossLineView);

//        crossLineView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //十字线出现的时候 详细信息。这里是activity 绑定的
        crossInfoView = (LinearLayout) view.findViewById(R.id.crossInfoView);
        tv_time = (TextView) crossInfoView.findViewById(R.id.tv_time);
        tv_open = (TextView) crossInfoView.findViewById(R.id.tv_open);
        tv_close = (TextView) crossInfoView.findViewById(R.id.tv_close);
        tv_high = (TextView) crossInfoView.findViewById(R.id.tv_high);
        tv_low = (TextView) crossInfoView.findViewById(R.id.tv_low);
        tv_rate = (TextView) crossInfoView.findViewById(R.id.tv_rate);
        tv_rateChange = (TextView) crossInfoView.findViewById(R.id.tv_rateChange);

        int numberScale = ProFormatConfig.getProFormatMap(type + "|" + code);
        kLineView.setNumberScal(numberScale != -1 ? numberScale : 2);
        if (TradeProduct.CODE_XAG.equals(code)
                || TradeConfig.code_jn.equals(type)) {
            //如果品种是银，直接使用整数
            kLineView.setNumberScal(0);
        }
        //设置配置的k线颜色
        kLineView.setCandlePostColor(getResources().getColor(R.color.k_post));
        kLineView.setCandleNegaColor(getResources().getColor(R.color.k_neg));

        kLineView.setCrossLineView(crossLineView);
        kLineView.setShowSubChart(true);
        kLineView.setAxisYtopHeight(0);//最顶部不留白
        kLineView.setAxisYmiddleHeight(KDisplayUtil.dip2px(getActivity(), 47));
        //Y轴价格坐标 在边框内部
        kLineView.setAxisTitlein(true);
        //阻断touch事件的分发 并且setOnKLineTouchDisableListener中处理逻辑
        kLineView.setTouchEnable(true);
        //不显示显示指标线的值 SMA10:100 RSI:这些tips不显示
        kLineView.setShowTips(true);


        //十字线出现的滑动逻辑
        kLineView.setOnKCrossLineMoveListener(this);
        //阻断touch事件的分发逻辑  listView headerView,这里还是用listview的onitemClick，touch太容易触发
//        kLineView.setOnKLineTouchDisableListener(this);
        kLineView.setOnKChartClickListener(this);

        mainNormal = view.findViewById(R.id.mainNormal);
        subNormal = view.findViewById(R.id.subNormal);
        mainNormalView = view.findViewById(R.id.tab_SMA);
        subNormalView = view.findViewById(R.id.tab_MACD);
        mainNormalView.setSelected(true);
        subNormalView.setSelected(true);

        mainNormalViewLand = view.findViewById(R.id.tab_SMA_land);
        subNormalViewLand = view.findViewById(R.id.tab_MACD_land);
        mainNormalViewLand.setSelected(true);
        subNormalViewLand.setSelected(true);

        view.findViewById(R.id.tab_SMA).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_EMA).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_BOLL).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_MACD).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_RSI).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_KDJ).setOnClickListener(normalLinstener);

        view.findViewById(R.id.tab_SMA_land).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_EMA_land).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_BOLL_land).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_MACD_land).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_RSI_land).setOnClickListener(normalLinstener);
        view.findViewById(R.id.tab_KDJ_land).setOnClickListener(normalLinstener);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
            handLandView(true);

            kLineView.setMainF(lanMainF);
            kLineView.setSubF(lanSubF);

        } else {
            //切换到竖屏
            handLandView(false);

            kLineView.setMainF(mainF);
            kLineView.setSubF(subF);
        }
    }

    public void initListener() {


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
            handLandView(true);

            kLineView.setMainF(lanMainF);
            kLineView.setSubF(lanSubF);
        } else {
            //切换到竖屏
            handLandView(false);

            kLineView.setMainF(mainF);
            kLineView.setSubF(subF);
        }
    }

    void handLandView(boolean isLand) {
        if (isLand) {
            kLineView.setAxisYmiddleHeight(KDisplayUtil.dip2px(getActivity(), 15));

            //隐藏竖屏的指标
            mainNormal.setVisibility(View.GONE);
            subNormal.setVisibility(View.GONE);

            //显示横屏指标
            if (landTypeView != null)
                landTypeView.setVisibility(View.VISIBLE);
        } else {
            kLineView.setAxisYmiddleHeight(KDisplayUtil.dip2px(getActivity(), 47));
            //显示竖屏的指标
            mainNormal.setVisibility(View.VISIBLE);
            subNormal.setVisibility(View.VISIBLE);

            //隐藏横屏指标
            if (landTypeView != null)
                landTypeView.setVisibility(View.GONE);
        }
    }


    public boolean isLandScape() {
        return Configuration.ORIENTATION_LANDSCAPE == getActivity().getResources()
                .getConfiguration().orientation;
    }

    void event4MACD() {
        kLineView.setSubLineData(KParseUtils.getMacdData(list,
                KParamConfig.getMacdTParam1(getActivity()),
                KParamConfig.getMacdTParam2(getActivity()),
                KParamConfig.getMacdKParam(getActivity())));
        kLineView.setSubList(KParseUtils.getMacdStickDatas(list,
                KParamConfig.getMacdTParam1(getActivity()),
                KParamConfig.getMacdTParam2(getActivity()),
                KParamConfig.getMacdKParam(getActivity())));
        kLineView.setSubNormal(KLineNormal.NORMAL_MACD);

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "MACD");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "MACD");
        }
    }

    void event4KDJ() {
        kLineView.setSubLineData(KParseUtils.getKDJLinesDatas(list,
                KParamConfig.getKdjKParam(getActivity())));
        kLineView.setSubNormal(KLineNormal.NORMAL_KDJ);

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "KDJ");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "KDJ");
        }
    }

    void event4VOL() {
        kLineView.setSubNormal(KLineNormal.NORMAL_VOL);

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "VOL");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "VOL");
        }
    }

    void event4RSI() {
        kLineView.setSubLineData(KParseUtils.getRsiLineDatas(list,
                KParamConfig.getRsiParam1(getActivity()),
                KParamConfig.getRsiParam2(getActivity()),
                KParamConfig.getRsiParam3(getActivity())));
        kLineView.setSubNormal(KLineNormal.NORMAL_RSI);

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "RSI");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "RSI");
        }
    }

    void event4SMA() {
        kLineView.setMainNormal(KLineNormal.NORMAL_SMA);
        if (BakSourceInterface.PARAM_KLINE_5M_WEIPAN.equals(cycle)
                || BakSourceInterface.PARAM_KLINE_5M.equals(cycle)
                || BakSourceInterface.PARAM_KLINE_1M_WEIPAN.equals(cycle)) {
            kLineView.setMainLineData(KParseUtils.getSMAData(list,
                    KParamConfig.getSMAcfg(getActivity(), false)));
        } else {
            kLineView.setMainLineData(KParseUtils.getSMAData(list,
                    KParamConfig.getSMAcfg(getActivity(), true)));
        }

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "SMA");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "SMA");
        }
    }

    void event4BOLL() {
        kLineView.setMainNormal(KLineNormal.NORMAL_BOLL);
        kLineView.setMainLineData(KParseUtils.getBollData(list,
                KParamConfig.getBoolTParam(getActivity()),
                KParamConfig.getBoolKParam(getActivity())));

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "BOLL");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "BOLL");
        }
    }

    void event4EMA() {
        kLineView.setMainNormal(KLineNormal.NORMAL_EMA);
        kLineView.setMainLineData(KParseUtils.getEMAData(list,
                KParamConfig.getEmaParam(getActivity())));

        if (isLandScape()) {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_horizontal", "EMA");
        } else {
            MyAppMobclickAgent.onEvent(getActivity(), "v3_kline_type_vertical", "EMA");
        }
    }

    View.OnClickListener normalLinstener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.tab_SMA) {
                event4SMA();

                mainNormalView.setSelected(false);
                mainNormalView = view;
                mainNormalView.setSelected(true);


                mainNormalViewLand.setSelected(false);
                mainNormalViewLand = rootView.findViewById(R.id.tab_SMA_land);
                mainNormalViewLand.setSelected(true);
            }
            if (id == R.id.tab_EMA) {
                event4EMA();

                mainNormalView.setSelected(false);
                mainNormalView = view;
                mainNormalView.setSelected(true);


                mainNormalViewLand.setSelected(false);
                mainNormalViewLand = rootView.findViewById(R.id.tab_EMA_land);
                mainNormalViewLand.setSelected(true);
            }
            if (id == R.id.tab_BOLL) {
                event4BOLL();

                mainNormalView.setSelected(false);
                mainNormalView = view;
                mainNormalView.setSelected(true);


                mainNormalViewLand.setSelected(false);
                mainNormalViewLand = rootView.findViewById(R.id.tab_BOLL_land);
                mainNormalViewLand.setSelected(true);
            }

            //附图
            if (id == R.id.tab_MACD) {
                event4MACD();

                subNormalView.setSelected(false);
                subNormalView = view;
                subNormalView.setSelected(true);


                subNormalViewLand.setSelected(false);
                subNormalViewLand = rootView.findViewById(R.id.tab_MACD_land);
                subNormalViewLand.setSelected(true);
            }
            if (id == R.id.tab_RSI) {
                event4RSI();

                subNormalView.setSelected(false);
                subNormalView = view;
                subNormalView.setSelected(true);


                subNormalViewLand.setSelected(false);
                subNormalViewLand = rootView.findViewById(R.id.tab_RSI_land);
                subNormalViewLand.setSelected(true);
            }
            if (id == R.id.tab_KDJ) {
                event4KDJ();

                subNormalView.setSelected(false);
                subNormalView = view;
                subNormalView.setSelected(true);


                subNormalViewLand.setSelected(false);
                subNormalViewLand = rootView.findViewById(R.id.tab_KDJ_land);
                subNormalViewLand.setSelected(true);
            }

            if (id == R.id.tab_SMA_land) {
                rootView.findViewById(R.id.tab_SMA).performClick();
            }
            if (id == R.id.tab_EMA_land) {
                rootView.findViewById(R.id.tab_EMA).performClick();
            }
            if (id == R.id.tab_BOLL_land) {
                rootView.findViewById(R.id.tab_BOLL).performClick();
            }

            if (id == R.id.tab_MACD_land) {
                rootView.findViewById(R.id.tab_MACD).performClick();
            }
            if (id == R.id.tab_RSI_land) {
                rootView.findViewById(R.id.tab_RSI).performClick();
            }
            if (id == R.id.tab_KDJ_land) {
                rootView.findViewById(R.id.tab_KDJ).performClick();
            }
        }
    };

    /**
     * 十字线 滑动显示对应的日期K线信息
     *
     * @param object
     */
    @Override
    public void onCrossLineMove(KCandleObj object) {
        try {
            if (crossInfoView == null)
                return;
            if (object == null)
                return;
            if (kLineView.getkCandleObjList() == null)
                return;
            //昨收价格为上一个k线的收盘价,如果是第一个k线就是open价
            double zuoClose = object.getClose();
            //获取到当前的位置
            int index = kLineView.getTouchIndex();
            //上一个k线的位置
            index = index - 1;
            if (index >= 0 && index < kLineView.getkCandleObjList().size()) {
                zuoClose = kLineView.getkCandleObjList().get(index).getClose();
            }

            tv_time.setText(object.getTime());

            //收盘价用白色标示，其他大于close 红色，小于绿色
            tv_close.setText(ProFormatConfig.formatByCodes(type + "|" + code, object.getClose()));//KNumberUtil
            tv_open.setText(ProFormatConfig.formatByCodes(type + "|" + code, object.getOpen() + ""));
            tv_high.setText(ProFormatConfig.formatByCodes(type + "|" + code, object.getHigh() + ""));
            tv_low.setText(ProFormatConfig.formatByCodes(type + "|" + code, object.getLow() + ""));
            //开盘价大于上一个k线的收盘价 红色
            if (object.getOpen() >= zuoClose) {
                tv_open.setTextColor(getResources().getColor(R.color.color_opt_gt));
            } else {
                tv_open.setTextColor(getResources().getColor(R.color.color_opt_lt));
            }
            //最高价大于上一个k线的收盘价 红色
            if (object.getHigh() >= object.getClose()) {
                tv_high.setTextColor(getResources().getColor(R.color.color_opt_gt));
            } else {
                tv_high.setTextColor(getResources().getColor(R.color.color_opt_lt));
            }
            //最低价大于上一个k线的收盘价 红色
            if (object.getLow() >= object.getClose()) {
                tv_low.setTextColor(getResources().getColor(R.color.color_opt_gt));
            } else {
                tv_low.setTextColor(getResources().getColor(R.color.color_opt_lt));
            }
            double rate = object.getClose() - zuoClose;
            //涨幅的计算  (当前k线的收盘价-上一个k线的收盘价)/上一个k线的收盘价*100%
            tv_rate.setText(KNumberUtil.beautifulDouble(rate));
            String percent = KNumberUtil.beautifulDouble(NumberUtil.divide(NumberUtil.multiply(rate, 100), zuoClose));
            tv_rateChange.setText("" + percent + "%");
            if (rate >= 0) {
                tv_rate.setTextColor(getResources().getColor(R.color.color_opt_gt));
                tv_rateChange.setTextColor(getResources().getColor(R.color.color_opt_gt));
            } else {
                tv_rate.setTextColor(getResources().getColor(R.color.color_opt_lt));
                tv_rateChange.setTextColor(getResources().getColor(R.color.color_opt_lt));
            }

            if (!kLineView.isToucInLeftChart()) {
                //touch在右边，将信息放在左边
                crossInfoView.setGravity(Gravity.LEFT);
            } else {
                crossInfoView.setGravity(Gravity.RIGHT);
            }

            if (crossInfoView.getVisibility() != View.VISIBLE)
                crossInfoView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCrossLineHide() {
        if (crossInfoView == null)
            return;
        crossInfoView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void event() {

    }


    private List<KCandleObj> list = null;

    @Override
    public boolean onSingleClick() {
        return false;
    }

    @Override
    public boolean onDoubleClick() {
        //双击横屏竖屏切换
        if (Configuration.ORIENTATION_LANDSCAPE == getResources()
                .getConfiguration().orientation) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        return false;
    }

    @Override
    public boolean onLongPress() {
        return false;
    }

    class GetKlineDataTask extends AsyncTask<String, Void, List<KCandleObj>> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (layoutContent != null)
                layoutContent.setVisibility(View.INVISIBLE);
            if (layoutLoding != null)
                layoutLoding.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<KCandleObj> doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String source = getArguments().getString("source");

                if (list == null && BaseInterface.isBakSource) {
                    String code = getArguments().getString("code");

                    if (BakSourceInterface.specialSource.contains(source)) {
                        String id = getArguments().getInt("goodsid") + "";
                        list = KLineHelper.getKlineAsc4Weipan(source, code, cycle);
                    } else {
                        list = KLineHelper.getKlineAsc(source, code, cycle);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<KCandleObj> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (!isAdded())
                return;

            if (layoutContent != null)
                layoutContent.setVisibility(View.VISIBLE);
            if (layoutLoding != null)
                layoutLoding.setVisibility(View.GONE);

            if (result == null || result.size() == 0) {
                showCusToast("暂无数据");
                if (layoutContent != null)
                    layoutContent.setVisibility(View.INVISIBLE);
                return;
            }
            setData(list);
        }
    }

    void setData(List<KCandleObj> list) {
        kLineView.setkCandleObjList(list);
        //主图指标
        if (lastTopNorm == KLineNormal.NORMAL_SMA) {
            event4SMA();
        } else if (lastTopNorm == KLineNormal.NORMAL_EMA) {
            event4EMA();
        } else if (lastTopNorm == KLineNormal.NORMAL_BOLL) {
            event4BOLL();
        }
        //附图指标，根据历史记录设置
        if (lastBottomNorm == KLineNormal.NORMAL_VOL) {
            event4VOL();
        } else if (lastBottomNorm == KLineNormal.NORMAL_KDJ) {
            event4KDJ();
        } else if (lastBottomNorm == KLineNormal.NORMAL_RSI) {
            event4RSI();
        } else if (lastBottomNorm == KLineNormal.NORMAL_MACD) {
            event4MACD();
        }
        kLineView.postInvalidate();
    }

    /**
     * 刷新最后一个K线 update by  haiyang 08-06
     * 由于期货的间隔休市时间过多  所以服务器返回周期线的时候带上最新的周期
     * 如果最新的行情时间在尾巴里面  不添加新的周期  不再新的尾巴之内  就添加一个新的周期
     * 有夜市的产品,日线在夜市开盘之后 当天需要画两根
     *
     * @param optional
     */
    public void setLastKData(Optional optional) {
        //周线不好处理，直接不管
        if (BakSourceInterface.PARAM_KLINE_WEEK_WEIPAN.equals(cycle)) {
            return;
        }

        // 待添加的对象
        KCandleObj toAddendK = optional.obj2KCandleObj();
        if (toAddendK != null) {
            List<KCandleObj> list = kLineView.getkCandleObjList();
            if (list == null || list.size() == 0)
                return;
            //如果少于两条k线值
            if (list.size() < 2)
                return;
            // 最后一根蜡烛线
            KCandleObj lastK = list.get(list.size() - 1);
            //格式化显示的时间
            String formartT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "MM-dd HH:mm");
            if (BakSourceInterface.PARAM_KLINE_1D_WEIPAN.equals(cycle)) {
                //日线
                formartT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "yyyy-MM-dd");
            }
            toAddendK.setTime(formartT);
            String toAddendKT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "yyyy-MM-dd HH:mm:ss");
            String lastKT = DateUtil.formatDate(new Date(lastK.getTimeLong()), "yyyy-MM-dd HH:mm:ss");
            Log.v(TAG, "toAddendKT=" + toAddendKT + "  lastKT=" + lastKT + "  formartT=" + formartT);

            /**
             * 按照5分钟来算
             *
             * 出现的情况
             * 添加的时间是  17:03
             * 1、17:00  17:01  替换最后一个 -－>  17:00 17:03
             *
             * 添加的时间是  17:06
             * 2、17:00 17:04
             *
             * 第一个k线刚好是停盘之后的k线
             * 添加时间是 08:00
             */
            //刷新最后一个k线；1、替换最后一根k线 2、时间停留久了，添加一根新的k线 默认改为true
            boolean isReplaceLast = true;
            //k线传过来的最后一个k线就是当前没结束的周期k线，
            // 比如5分钟周期的，上一个是17:00，当前时间是17:01，那么传过来的是17:05的k线；传过来的时间就是 17:05的
            // 需要更新的和最后一个是同一个
            String strlastKT = DateUtil.formatDate(new Date(lastK.getTimeLong()), "yyyy-MM-dd HH:mm");
            String strtoAddendKT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "yyyy-MM-dd HH:mm");
            //  以下为(原)替换或者添加的核心代码
//            if (BakSourceInterface.PARAM_KLINE_1M_WEIPAN.equals(cycle)) {
//                //分时图 一分钟
//                if (strlastKT.equals(strtoAddendKT)) {
//                    Log.v(TAG, "remove");
//                    isReplaceLast = true;
//                    list.remove(lastK);
//                }
//            } else if (BakSourceInterface.PARAM_KLINE_1D_WEIPAN.equals(cycle)) {
//                //如果是日线直接替换
//                isReplaceLast = true;
//                list.remove(lastK);
//            } else {
//                if (BakSourceInterface.cycleTMap.containsKey(cycle)) {
//                    if (lastK.getTimeLong() - list.get(list.size() - 2).getTimeLong() >=
//                            BakSourceInterface.cycleTMap.get(cycle).longValue()
//                            && toAddendK.getTimeLong() < lastK.getTimeLong()) {
//                        isReplaceLast = true;
//                        list.remove(lastK);
//                    }
//                }
//            }
            // 新的替换代码 因为有尾巴 直接比较是否在时间之内(去除比较最后两根线的周期跨度)
            if (BakSourceInterface.cycleTMap.containsKey(cycle)) {
                if (BakSourceInterface.PARAM_KLINE_1M_WEIPAN.equals(cycle)) {
//                //分时图 一分钟
                    if (strlastKT.equals(strtoAddendKT)) {
                        Log.v(TAG, "remove");
                        isReplaceLast = true;
                        list.remove(lastK);
                    }else {
                        isReplaceLast = false;
                    }
                } else {
                    if (toAddendK.getTimeLong() > lastK.getTimeLong()) {
                        isReplaceLast = false;
                    } else {
                        isReplaceLast = true;
                        list.remove(lastK);
                    }
                }
            }

            long addT = 0;// 跨度周期
            if (BakSourceInterface.cycleTMap.containsKey(cycle)) {
                addT = BakSourceInterface.cycleTMap.get(cycle).longValue();
            }

            //设置K线的开高低收
            if (isReplaceLast) {
                //如果是替换的话,
                // 开盘价就是 最后k线的开盘价，
                toAddendK.setOpen(lastK.getOpen());
                // 最高价就是最后k线和当前的价比较的较高价，字段close 接收的最新价
                toAddendK.setHigh(Math.max(lastK.getHigh(), toAddendK.getClose()));
                // 最低价就是最后k线和当前的价比较的较低价,字段close 接收的最新价
                toAddendK.setLow(Math.min(lastK.getLow(), toAddendK.getClose()));
                //收盘价就是最新价格

                //因为最后一个k线就是下一个k线周期的结束时间，如果是替换最后一个K线的话，时间设置成下一个k线的结束结时间
                toAddendK.setTime(lastK.getTime());
                toAddendK.setTimeLong(lastK.getTimeLong());
            } else {
//                //新加的k线 都是最新价格
                toAddendK.setOpen(lastK.getClose());
                toAddendK.setHigh(lastK.getClose());
                toAddendK.setLow(lastK.getClose());

                //update at 2017-04-19
                //因为最后一个k线就是下一个k线周期的结束时间 新加的k线就要设置成 下一个周期的结束时间
                if (BakSourceInterface.PARAM_KLINE_1M_WEIPAN.equals(cycle)) {
                    //直接使用刷新获取到的时间
                    String strT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "MM-dd HH:mm");
                    toAddendK.setTime(strT);
                    toAddendK.setTimeLong(toAddendK.getTimeLong());
                } else {
                    //大于一分钟的  直接使用替换的时间对的上
                    String strT = DateUtil.formatDate(new Date(lastK.getTimeLong() + addT), "MM-dd HH:mm");
                    // 日线不显示分钟
                    if (BakSourceInterface.PARAM_KLINE_1D_WEIPAN.equals(cycle)){
                         strT = DateUtil.formatDate(new Date(lastK.getTimeLong() + addT), "MM-dd");
                    }
                    toAddendK.setTime(strT);
                    toAddendK.setTimeLong(lastK.getTimeLong() + addT);
                }
            }
            Log.v(TAG, "add");
            list.add(toAddendK);
            //上一次在最后的位置，或者是新加了一根k线还在最后位置，手动移动位置显示最新的k线
            if (kLineView.getDrawIndexEnd() == list.size() - 1 - 1
                    || kLineView.getDrawIndexEnd() == list.size() - 1) {
                Log.v(TAG, "getDrawIndexEnd() " + kLineView.getDrawIndexEnd() + " list.size()=" + list.size());
                //是最后一个
                kLineView.setDrawIndexEnd(list.size());
            }
            lastTopNorm = kLineView.getMainNormal();
            lastBottomNorm = kLineView.getSubNormal();

            setData(list);
        }
    }

}
