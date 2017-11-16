package com.trade.eight.kchart.chart.minute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.trade.eight.kchart.chart.KBaseGraphView;
import com.trade.eight.kchart.chart.cross.KCrossLineView;
import com.trade.eight.kchart.entity.KCandleObj;
import com.trade.eight.kchart.listener.OnKCrossLineMoveListener;
import com.trade.eight.kchart.util.KLogUtil;
import com.trade.eight.kchart.util.KNumberUtil;

import java.util.List;


/**
 * Created by fangzhu
 * k线的分时图的touch处理逻辑
 *
 * 发现问题
 * mark：
 * 关联的act使用了不重复加载的方式
 * android:configChanges="orientation|keyboard|keyboardHidden"
 * 发现ontouch 使用  SimpleOnGestureListener的时候 ondoubTap双击之后还会调用onlongPress
 * 造成切换屏幕方向的时候 onlongPress 多调用一次
 */
public class KMinuteTouchView extends KBaseGraphView implements KCrossLineView.IDrawCrossLine {
    String TAG = "KMinuteTouchView";
    //X轴的位置是否根据时间定位
    protected boolean asixXByTime = true;
    //true表示X轴的文字位置在线条的内部
    protected boolean isAsixTitlein = false;
    //十字线控件
    protected KCrossLineView crossLineView;
    //开盘最大值 最小值 不做别的使用 仅仅作为保留 最大值最小值
    protected double maxValue, minValue;
    //成交量最大值，最小值为0
    protected double maxVol;
    //昨收 虚线位置 0.00% 用于显示
    protected double zuoClosed;
    //应该是画出线条数242－1，index 从0开始的
    protected int drawCount = 241;
    //每条线条占有的宽度
    protected float dataSpace = 0;
    //时间轴的开始时间结束时间long类型
    protected long startTimeLong, stopTimeLong;
    //休盘时间 开始 结束  持续时长，如股指中间休盘的时间 11:30/13:00
    protected long middleStart, middleStop, middleTakeTime;
    //Y轴开始结束的值 最大和最小 实际使用的最大最小收盘价
    protected double startYdouble, stopYdouble;
    //成交量矩形宽度
    protected float volW = 2;
    //解析中间休盘时间的
    public static final String SPLIT_SCHME = "/";
    //股票的开始 休盘 结束时间
    public String startTimeStr = "", middleTimeStr = "", stopTimeStr = "";
    //默认交易时间段
    public static final String START_TIME_STR = "06:00", MIDDLE_TIME_STR = "", STOP_TIME_STR = "04:00";
    //价格大于收盘价的颜色
    public int textColorGt = candlePostColor;
    //价格小于收盘价的颜色
    public int textColorLt = candleNegaColor;
    //价格等于收盘价的颜色
    public int textColorEq = Color.WHITE;
    //昨收位置虚线效果
    public PathEffect pathEffect = new DashPathEffect(new float[]{10, 10, 10, 10}, 1);
    //昨收位置虚线颜色
    public int effectColor = Color.parseColor("#848999");
    //path的填充颜色
    public int areaColor = Color.parseColor("#334877e6");//20%
    //path的填充颜色透明度 如果颜色使用了透明度就不用了
    public int areaAlph = 20;
    //set date 按照时间升序排列的
    protected List<KCandleObj> kCandleObjList;

    //线条的填充颜色
    protected int lineColor = Color.parseColor("#4877e6");
    //线均线的填充颜色
    protected int averageLineColor = Color.parseColor("#F5A245");//Color.YELLOW
    //分时线条的宽度 1dip setDefaultValue 中重新设置
    protected float lineStrokeWidth = 3F;
    //分时图是否显示均线
    protected boolean showAverageLine = true;

    //手指按下的位置
    protected float touchX = -1;
    //黄色均线的线条大小
    protected float aveStrokeWidth = 2F;
    //是否双击了view，解决重复调用onlongPress
    protected boolean isDoubClick = false;
    // false 表示只显示 开始 休盘 结束时间
    protected boolean showMoreTime = true;

    public KMinuteTouchView(Context context) {
        super(context);
    }

    public KMinuteTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KMinuteTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 十字线
     *
     * @param canvas
     */
    public void drawCrossLine(Canvas canvas) {
        if (!showCross)
            return;
        if (!isCrossEnable)
            return;
        try {
            int index = -1;
            float x = touchX;//x的坐标位置
            if (asixXByTime) {
                if (kCandleObjList == null || kCandleObjList.size() == 0)
                    return;
//                float divX = (x - axisXleftWidth);
                for (int i = 0; i < kCandleObjList.size(); i++) {
                    float indexX = getXByTime(kCandleObjList.get(i));
                    if (i == 0 && x <= indexX) {
//                        x = index;
                        index = i;
                    }
                    if (i == kCandleObjList.size() - 1 && x >= indexX) {
//                        x = index;
                        index = i;
                    }
                    if (index >= 0) {
                        break;
                    }
                    if (i + 1 < kCandleObjList.size() - 1) {
                        float nextX = getXByTime(kCandleObjList.get(i + 1));
                        if (x >= indexX && x < nextX) {
                            index = i;
                        }
                    }
                    if (index >= 0) {
                        break;
                    }
                }
                x = getXByTime(kCandleObjList.get(index));
            } else {
                index = (int) ((touchX - axisXleftWidth) / (dataSpace));
                if (index >= kCandleObjList.size())
                    index = kCandleObjList.size() - 1;
                if (index < 0)
                    index = 0;
                x = index * (dataSpace) + axisXleftWidth;
                if (index == 0)
                    x = axisXleftWidth;
            }
            KCandleObj fenshiData = kCandleObjList.get(index);
            if (fenshiData == null) {
                return;
            } else {
                if (onKCrossLineMoveListener != null)
                    onKCrossLineMoveListener.onCrossLineMove(fenshiData);

                // 绘制点击十字线
                Paint crossPaint = getPaint();
                crossPaint.clearShadowLayer();
                crossPaint.reset();
                crossPaint.setStrokeWidth(crossStrokeWidth);
                crossPaint.setColor(crossLineColor);
                crossPaint.setStyle(Paint.Style.FILL);
                //价格paint
                Paint textPaint = getPaint();
                textPaint.setColor(rectFillColor);
                textPaint.setTextSize(crossLatitudeFontSize);
                textPaint.setStyle(Paint.Style.FILL);

                //时间paint
                Paint textPaintT = getPaint();
                textPaintT.setColor(rectFillColor);
                textPaintT.setTextSize(crossLongitudeFontSize);
                textPaintT.setStyle(Paint.Style.FILL);

                //竖线
                if (showSubChart)
                    canvas.drawLine(x, axisYtopHeight, x, getHeight() - axisYbottomHeight, crossPaint);
                else
                    canvas.drawLine(x, axisYtopHeight, x, getHeight() - axisYbottomHeight - axisYmiddleHeight, crossPaint);

                //左边价格width
                float w = textPaint.measureText(fenshiData.getClose() + "");

                //横线x开始位置坐标
                float horX = axisXleftWidth;
                if (isAsixTitlein) {
                    horX = axisXleftWidth + w;
                }
                //Y轴方向 横线
                float y = getYByPrice(fenshiData.getClose());

                //right百分比width
                float wR = textPaint.measureText(axisYrightTitles.get(0) + "");
                canvas.drawLine(horX, y, getWidth() - axisXrightWidth - wR, y, crossPaint);

                //时间文字width
                float wT = textPaintT.measureText(kCandleObjList.get(0).getTime() + "");
                //价格
                RectF rectF = new RectF(axisXleftWidth - w - commonPadding, y - latitudeFontSize / 2,
                        axisXleftWidth, y + latitudeFontSize / 2);

                //百分比
                RectF rectFR = new RectF(getWidth() - axisXrightWidth, y - latitudeFontSize / 2,
                        getWidth() - axisXrightWidth + wR, y + latitudeFontSize / 2);

                if (isAsixTitlein) {
                    rectF = new RectF(axisXleftWidth, y - latitudeFontSize / 2,
                            axisXleftWidth + w, y + latitudeFontSize / 2);
                    rectFR = new RectF(getWidth() - axisXrightWidth - wR + 2, y - latitudeFontSize / 2,
                            getWidth() - axisXrightWidth, y + latitudeFontSize / 2);
                }
                //时间
                float left = x - wT / 2;
                float right = x + wT / 2;
                if (left < axisXleftWidth) {
                    left = axisXleftWidth;
                    right = left + 1 * wT;
                }
                if (right > getWidth() - axisXrightWidth) {
                    right = getWidth() - axisXrightWidth;
                    left = right - 1 * wT;
                }
                //时间矩形框
                RectF rectFT = new RectF(left, axisYtopHeight - longitudeFontSize,
                        right, axisYtopHeight);
                //左边价格矩形
                canvas.drawRect(rectF, textPaint);
                //右边百分比矩形
                canvas.drawRect(rectFR, textPaint);
                //时间矩形
                canvas.drawRect(rectFT, textPaintT);
                //设置文字颜色
                textPaint.setColor(crossFontColor);
                textPaintT.setColor(crossFontColor);

                double s = ((fenshiData.getClose() - zuoClosed) / zuoClosed * 100);
                String persent = KNumberUtil.beautifulDouble(s) + "%";
                //画出文字 价格
                drawText(canvas, fenshiData.getClose() + "", rectF, textPaint);
                //画出文字 百分比
                drawText(canvas, persent, rectFR, textPaint);
                //画出文字 时间
                drawText(canvas, fenshiData.getTime(), rectFT, textPaintT);

//                canvas.drawText(fenshiData.getClose() + "", 0, y + latitudeFontSize / 2, textPaint);
//                canvas.drawText(persent, getWidth() - wR + 2, y + latitudeFontSize / 2, textPaint);
//                canvas.drawText(fenshiData.getTime(), rectFT.left + (rectFT.right - rectFT.left)/2 - wT / 2, axisYtopHeight / 2 + latitudeFontSize / 2, textPaintT);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置十字线的逻辑提供回调
     */
    private OnKCrossLineMoveListener onKCrossLineMoveListener;

    public OnKCrossLineMoveListener getOnKCrossLineMoveListener() {
        return onKCrossLineMoveListener;
    }

    public void setOnKCrossLineMoveListener(OnKCrossLineMoveListener onKCrossLineMoveListener) {
        this.onKCrossLineMoveListener = onKCrossLineMoveListener;
    }

    /**
     * touch事件处理逻辑
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //是否需要禁用touch事件
        if (!touchEnable) {
            if (onKLineTouchDisableListener != null)
                onKLineTouchDisableListener.event();
            return true;
        }
        //第一种写法  直接使用getAction判断
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//                touchX = event.getRawX();
//                if (touchX < 2 || touchX > getWidth() - 2) {
//                    return false;
//                }
//                showCross = true;
//
//                if (crossLineView != null)
//                    crossLineView.postInvalidate();
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_OUTSIDE:
//                showCross = false;
//                if (onKCrossLineMoveListener != null)
//                    onKCrossLineMoveListener.onCrossLineHide();
//                if (crossLineView != null)
//                    crossLineView.postInvalidate();
//                break;
//
//            default:
//                break;
//        }

        //使用SimpleOnGestureListener 处理双击单击 滑动
        boolean stopDisPatchTouch = gestureDetector.onTouchEvent(event);
        KLogUtil.v(TAG, "stopDisPatchTouch="+stopDisPatchTouch);
        if (stopDisPatchTouch) {
            //停止继续分发touch事件
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                KLogUtil.v(TAG, "onTouchEvent ACTION_MOVE");
                //这里处理长按事件
                if (isLongPress) {
                    touchX = event.getRawX();
                    if (touchX < 2 || touchX > getWidth() - 2) {
                        return false;
                    }
                    showCross = true;

                    if (crossLineView != null)
                        crossLineView.postInvalidate();
                }

                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isLongPress = false;
                isDoubClick = false;
                break;
        }
        return true;
    }
    //是否是长按的逻辑
    boolean isLongPress = false;
    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            KLogUtil.v(TAG, "onDoubleTap");
            isDoubClick = true;
            //双击事件
            if (onKChartClickListener != null)
                onKChartClickListener.onDoubleClick();
            return true;
        }

//        @Override
//        public boolean onDoubleTapEvent(MotionEvent e) {
//            KLogUtil.v(TAG, "onDoubleTapEvent");
//            //不用这个  这个是双击中间产生的down up 这个action
//            return super.onDoubleTapEvent(e);
//        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            KLogUtil.v(TAG, "onSingleTapConfirmed");
            //单击事件

            if (onKChartClickListener != null)
                onKChartClickListener.onSingleClick();

            //点击  如果十字线已经出现，隐藏十字线；如果十字线没出现，显示十字线
            if (showCross) {
                //十字线已经出现，隐藏十字线
                showCross = false;
                if (onKCrossLineMoveListener != null)
                    onKCrossLineMoveListener.onCrossLineHide();
            } else {
                //十字线没出现，显示十字线
                showCross = true;
                touchX = e.getRawX();
                if (touchX < 2 || touchX > getWidth() - 2) {
                    return false;
                }
            }
            if (crossLineView != null)
                crossLineView.postInvalidate();

            return true;
        }


//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            KLogUtil.v(TAG, "onSingleTapUp");
//            //这个不是确定的单击事件，这个是down之后就会触发
//            return super.onSingleTapUp(e);
//        }

        @Override
        public void onLongPress(MotionEvent e) {
            KLogUtil.v(TAG, "onLongPress");
            //如果是双击之后多调用的  不用处理
            if (isDoubClick)
                return;

            //长按
            if (onKChartClickListener != null)
                onKChartClickListener.onLongPress();

            isLongPress = true;

//            //长按 和单击事件是一样的处理方式
            if (showCross) {
                //十字线已经出现，隐藏十字线
                showCross = false;
                if (onKCrossLineMoveListener != null)
                    onKCrossLineMoveListener.onCrossLineHide();
            } else {
                //十字线没出现，显示十字线
                showCross = true;
                touchX = e.getRawX();
                if (touchX < 2 || touchX > getWidth() - 2) {
                    return;
                }
            }
            if (crossLineView != null)
                crossLineView.postInvalidate();
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            KLogUtil.v(TAG, "onFling");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            KLogUtil.v(TAG, "onScroll");
            //拖动
            if (!showCross)
                return false;
            touchX = e2.getRawX();
            if (touchX < 2 || touchX > getWidth() - 2) {
                return false;
            }
            if (crossLineView != null)
                crossLineView.postInvalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            KLogUtil.v(TAG, "onDown");
            //down
            return true;
        }
    };
    GestureDetector gestureDetector = new GestureDetector(getContext(), gestureListener);

    /**
     * 填充完数据之后
     * 默认显示十字线调用
     * <p/>
     * 在setkCandleObjList之后使用
     */
    public void perShowCross() {
        //认为事第一次画
//        if (touchX < 0)
//            return;

        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        if (kCandleObjList.size() < 2)
            return;
        //画在现有数据的中间
//        int index = kCandleObjList.size()/2;
        int index = kCandleObjList.size() - 1;
        KCandleObj obj = kCandleObjList.get(index);
        showCross = true;
        touchX = getXByTime(obj);
        if (crossLineView != null)
            crossLineView.postInvalidate();

    }

    /**
     * 根据价格获取Y坐标
     *
     * @param price
     * @return
     */
    float getYByPrice(double price) {
        //Y轴的真实坐标位置 (maxValue - minValue):dataheight = (zuoshou- minValue):y
        double d = price - startYdouble;
        float y = getHeight() * mainF - (float) axisYmiddleHeight - (float) ((d * (double) getDataHeightMian()) / (stopYdouble - startYdouble));
        return y;
    }

    /**
     * 根据时间获取当前X坐标
     *
     * @param obj
     * @return
     */
    float getXByTime(KCandleObj obj) {
        long divTime = stopTimeLong - startTimeLong - middleTakeTime;
        long t = obj.getTimeLong();
        if (t > middleStart && t < middleStop)
            return -1;
        if (t >= middleStop) {
            t = t - middleTakeTime;
        }
        //按照x轴的时间定位;关键是按下的时候怎么按照x坐标算出list的位置
        return (float) axisXleftWidth + getDataWidth() * (t - startTimeLong) / divTime;
    }


    public List<KCandleObj> getkCandleObjList() {
        return kCandleObjList;
    }

    public void setkCandleObjList(List<KCandleObj> kCandleObjList) {
        this.kCandleObjList = kCandleObjList;
        postInvalidate();
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getZuoClosed() {
        return zuoClosed;
    }

    public void setZuoClosed(double zuoClosed) {
        this.zuoClosed = zuoClosed;
    }

    public int getAreaColor() {
        return areaColor;
    }

    public void setAreaColor(int areaColor) {
        this.areaColor = areaColor;
    }

    public int getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(int effectColor) {
        this.effectColor = effectColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getAreaAlph() {
        return areaAlph;
    }

    public void setAreaAlph(int areaAlph) {
        this.areaAlph = areaAlph;
    }

    public PathEffect getPathEffect() {
        return pathEffect;
    }

    public void setPathEffect(PathEffect pathEffect) {
        this.pathEffect = pathEffect;
    }


    public int getTextColorGt() {
        return textColorGt;
    }

    public void setTextColorGt(int textColorGt) {
        this.textColorGt = textColorGt;
    }

    public int getTextColorEq() {
        return textColorEq;
    }

    public void setTextColorEq(int textColorEq) {
        this.textColorEq = textColorEq;
    }

    public int getTextColorLt() {
        return textColorLt;
    }

    public void setTextColorLt(int textColorLt) {
        this.textColorLt = textColorLt;
    }

    public float getLineStrokeWidth() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(float lineStrokeWidth) {
        this.lineStrokeWidth = lineStrokeWidth;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        if (startTimeStr != null && startTimeStr.trim().length() > 0)
            this.startTimeStr = startTimeStr;
    }

    public String getStopTimeStr() {
        return stopTimeStr;
    }

    public void setStopTimeStr(String stopTimeStr) {
        if (stopTimeStr != null && stopTimeStr.trim().length() > 0)
            this.stopTimeStr = stopTimeStr;
    }

    public String getMiddleTimeStr() {
        return middleTimeStr;
    }

    public void setMiddleTimeStr(String middleTimeStr) {
        this.middleTimeStr = middleTimeStr;
    }

    public boolean isAsixTitlein() {
        return isAsixTitlein;
    }

    public void setAsixTitlein(boolean asixTitlein) {
        isAsixTitlein = asixTitlein;
    }

    public int getAverageLineColor() {
        return averageLineColor;
    }

    public void setAverageLineColor(int averageLineColor) {
        this.averageLineColor = averageLineColor;
    }

    public boolean isAsixXByTime() {
        return asixXByTime;
    }

    public void setAsixXByTime(boolean asixXByTime) {
        this.asixXByTime = asixXByTime;
    }

    public boolean isShowMoreTime() {
        return showMoreTime;
    }

    public void setShowMoreTime(boolean showMoreTime) {
        this.showMoreTime = showMoreTime;
    }

    public boolean isShowAverageLine() {
        return showAverageLine;
    }

    public void setShowAverageLine(boolean showAverageLine) {
        this.showAverageLine = showAverageLine;
    }
}
