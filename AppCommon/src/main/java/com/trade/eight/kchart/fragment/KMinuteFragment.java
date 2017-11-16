package com.trade.eight.kchart.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.kchart.chart.cross.KCrossLineView;
import com.trade.eight.kchart.chart.minute.KMinuteView;
import com.trade.eight.kchart.data.KLineHelper;
import com.trade.eight.kchart.entity.KCandleObj;
import com.trade.eight.kchart.listener.OnKChartClickListener;
import com.trade.eight.kchart.listener.OnKCrossLineMoveListener;
import com.trade.eight.kchart.listener.OnKLineTouchDisableListener;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.Date;
import java.util.List;

/**
 * Created by fangzhu v4.0.
 * <p/>
 * 改版后的分时图 fragment
 */
public class KMinuteFragment extends BaseFragment implements OnKLineTouchDisableListener, OnKChartClickListener {
    public static final String TAG = "KMinuteFragment";
    KMinuteView minuteView;
    KCrossLineView crossLineView;
    View layoutLoding;

    double closed;
    /*
        处理baksource 数据不完整的情况
        如 深圳成指  非WIFI下 获取到的数据不完整 服务端的接口问题
        在此做特殊处理
    */
    int TIME_STOP_MIN = 125;
    //产品code
    String type, code;
    GetKlineDataTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_kminute, null);
        closed = Double.parseDouble(ConvertUtil.NVL(getArguments().getString("closed"), "0"));
        minuteView = (KMinuteView) view.findViewById(R.id.minuteView);
        crossLineView = (KCrossLineView) view.findViewById(R.id.crossLineView);
        code = getArguments().getString("code");
        type = getArguments().getString("type");
        int numberScale = ProFormatConfig.getProFormatMap(type + "|" + code);
        minuteView.setNumberScal(numberScale != -1 ? numberScale : 2);
        if (TradeProduct.CODE_XAG.equals(code)
                || TradeConfig.code_jn.equals(type)) {
            //如果品种是银，直接使用整数
            minuteView.setNumberScal(0);
        }
        minuteView.setCrossLineView(crossLineView);
        //阻断touch事件的分发逻辑  listView headerView,这里还是用listview的onitemClick，touch太容易触发
//        minuteView.setOnKLineTouchDisableListener(this);
        minuteView.setOnKChartClickListener(this);
        minuteView.setAsixTitlein(true);
        minuteView.setShowSubChart(false);//不需要画成交量的指标
        minuteView.setZuoClosed(closed);
        minuteView.setStartTimeStr(getArguments().getString("startTimeStr"));
        minuteView.setStopTimeStr(getArguments().getString("stopTimeStr"));
        minuteView.setMiddleTimeStr(getArguments().getString("middleTimeStr"));

        minuteView.setAsixXByTime(false);

        task = new GetKlineDataTask();
        task.execute(new String[]{getArguments().getString("treaty"),
                getArguments().getString("type"),
                getArguments().getString("interval")});


//        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
//            minuteView.setTouchEnable(true);
//        } else {
//            minuteView.setTouchEnable(false);
//
//        }

        //点击出现十字线监听
        minuteView.setOnKCrossLineMoveListener(new OnKCrossLineMoveListener() {
            @Override
            public void onCrossLineMove(KCandleObj object) {
                if (object != null) {
                    initCrossView(object);
                }
            }

            @Override
            public void onCrossLineHide() {

            }
        });


        layoutLoding = view.findViewById(R.id.layoutLoding);

        return view;
    }

    @Override
    public void event() {

    }

    //touch 出现十字线的逻辑
    void initCrossView(KCandleObj entity) {

    }

    List<KCandleObj> kCandleObjs;

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
//            minuteView.setVisibility(View.INVISIBLE);
            if (layoutLoding != null)
                layoutLoding.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<KCandleObj> doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String source = getArguments().getString("source");
                if (BaseInterface.isBakSource) {
                    String code = getArguments().getString("code");
                    String id = getArguments().getInt("goodsid") + "";

                    if (BakSourceInterface.specialSource.contains(source)) {
                        kCandleObjs = KLineHelper.getKlineTime4Weipan(source, code);
                    } else {
                        kCandleObjs = KLineHelper.getKlineTime(source, code, BakSourceInterface.PARAM_KLINE_TIME);

                        //是否需要做接口数据不完整问题处理  全球沪指start
                        int stopPos = -1;
                        for (int i = 0; i < kCandleObjs.size(); i++) {
                            KCandleObj kLineEntity = kCandleObjs.get(i);
                            KCandleObj kLineEntityAfter = null;
                            if (i + 1 < kCandleObjs.size())
                                kLineEntityAfter = kCandleObjs.get(i + 1);

                            if (kLineEntityAfter != null && kLineEntityAfter.getTimeLong() - kLineEntity.getTimeLong() > TIME_STOP_MIN * 60 * 1000) {
                                Log.v(TAG, "get promble pos =" + i + " getTimelong=" + kLineEntity.getTimeLong() + " kLineEntityAfter.getTimelong()=" + kLineEntityAfter.getTimeLong());
                                stopPos = i;
                                break;
                            }
                        }
                        if (stopPos != -1)
                            kCandleObjs = kCandleObjs.subList(0, stopPos);
                        //是否需要做接口数据不完整问题处理  end
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return kCandleObjs;
        }

        @Override
        protected void onPostExecute(List<KCandleObj> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (!isAdded())
                return;

            doPostExecute(result, true);
        }

    }

    public void doPostExecute(List<KCandleObj> result, boolean isToast) {
        minuteView.setVisibility(View.VISIBLE);
        if (layoutLoding != null)
            layoutLoding.setVisibility(View.GONE);

        if (result == null) {
            if (isToast) {
                showCusToast("数据获取失败");
            }
            return;
        }

        if (result == null || result.size() == 0) {
            if (isToast) {
                showCusToast("暂无数据");
            }
            return;
        }
        if (result.size() > 0 && result.size() < 2) {
            //只有一条数据没法画出分时线的区域面积  这里当作没有数据
            result.add(result.get(result.size() - 1));
        }
        minuteView.setkCandleObjList(result);
        minuteView.postInvalidate();
//            minuteView.perShowCross();//默认显示十字线

//            //需要layout布局完成之后
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    minuteView.perShowCross();//默认显示十字线
//                }
//            }, 200);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    /**
     * 刷新最后一个K线
     *
     * @param optional
     */
    public void setLastKData(Optional optional) {
        KCandleObj toAddendK = optional.obj2KCandleObj();
        if (toAddendK != null) {
            List<KCandleObj> list = minuteView.getkCandleObjList();
            if (list == null || list.size() == 0)
                return;
            KCandleObj lastK = list.get(list.size() - 1);
            //都转换成分钟
            String lastKT = DateUtil.formatDate(new Date(lastK.getTimeLong()), "yyyy-MM-dd HH:mm");
            String toAddendKT = DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "yyyy-MM-dd HH:mm");

            //需要更新的和最后一个是同一个
            if (toAddendK.getTimeLong() >= lastK.getTimeLong()
                    && lastKT.equals(toAddendKT)) {
                Log.v(TAG, "remove");
                list.remove(lastK);
            }
            Log.v(TAG, "toAddendKT=" + toAddendKT);
            Log.v(TAG, "lastK=" + lastK);
            toAddendK.setTime(DateUtil.formatDate(new Date(toAddendK.getTimeLong()), "yyyy-MM-dd HH:mm"));
            if (toAddendK.getTimeLong() >= lastK.getTimeLong()) {
                Log.v(TAG, "add");
                toAddendK.setClose(toAddendK.getClose());
                list.add(toAddendK);
            }
            minuteView.setkCandleObjList(list);
        }
    }

    /**
     * 绘制持仓单辅助线
     *
     * @param listOrder
     */
    public void setHelpLineForTradeOrder(List<TradeOrder> listOrder) {
        minuteView.setHelpLineForTradeOrder(listOrder);
    }

    /**
     * 绘制行情提醒辅助线
     *
     * @param listProductNotice
     */
    public void setHelpLineForProductNotice(List<ProductNotice> listProductNotice) {
        minuteView.setHelpLineForProductNotice(listProductNotice);
    }
}
