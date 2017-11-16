package com.trade.eight.kchart.chart.minute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.kchart.chart.cross.KCrossLineView;
import com.trade.eight.kchart.entity.KCandleObj;
import com.trade.eight.kchart.util.KDateUtil;
import com.trade.eight.kchart.util.KDisplayUtil;
import com.trade.eight.kchart.util.KLogUtil;
import com.trade.eight.kchart.util.KNumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by fangzhu
 * k线的分时图
 */
public class KMinuteView extends KMinuteTouchView {
    String TAG = "KMinuteView";

    public KMinuteView(Context context) {
        super(context);
    }

    public KMinuteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KMinuteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KCrossLineView getCrossLineView() {
        return crossLineView;
    }

    public void setCrossLineView(KCrossLineView crossLineView) {
        this.crossLineView = crossLineView;
        crossLineView.setiDrawCrossLine(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        KLogUtil.v(TAG, "onDraw");
        super.onDraw(canvas);

        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        initTradeTime();
        KLogUtil.v(TAG, "startTimeStr=" + startTimeStr);
        KLogUtil.v(TAG, "stopTimeStr=" + stopTimeStr);
        KLogUtil.v(TAG, "middleTimeStr=" + middleTimeStr);
        try {
            initMaxMinValue();
            initTime();
            initDrawCount();
            drawLatitudeLine(canvas);
            drawLongitudeLineTitle(canvas);
            drawArea(canvas);
            drawHelpLineForTradeOrder(canvas);//持仓辅助线
            drawHelpLineForProductNotice(canvas);//行情复制线
            //文字最后画 避免被覆盖
            drawLatitudeTitle(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 交易时间段
     * 做兼容数据处理
     * 1、优先取k线带有的时间
     * 2、默认时间补齐
     */
    protected void initTradeTime() {
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        //优先取k线带有的时间
        if (isEmpty(startTimeStr)) {
            startTimeStr = kCandleObjList.get(0).getStartTime();
        }
        if (isEmpty(middleTimeStr)) {
            middleTimeStr = kCandleObjList.get(0).getMiddleTime();
        }
        if (isEmpty(stopTimeStr)) {
            stopTimeStr = kCandleObjList.get(0).getEndTime();
        }
        //默认时间补齐
        if (isEmpty(startTimeStr)) {
            startTimeStr = START_TIME_STR;
        }
        if (isEmpty(middleTimeStr)) {
            middleTimeStr = MIDDLE_TIME_STR;
        }
        if (isEmpty(stopTimeStr)) {
            stopTimeStr = STOP_TIME_STR;
        }
    }

    @Override
    protected void initDefaultValue(Context context) {
        super.initDefaultValue(context);
        lineStrokeWidth = KDisplayUtil.dip2px(context, 1);

    }

    /**
     * 计算需要画的线条总数
     */
    protected void initDrawCount() {
        long tempL = 60 * 1000;//默认间隔是一分钟
        if (kCandleObjList != null && kCandleObjList.size() > 1) {
            //不标准的有的是5秒
            tempL = kCandleObjList.get(1).getTimeLong() - kCandleObjList.get(0).getTimeLong();
        }
        if (tempL <= 0)
            tempL = 60 * 1000;
        KLogUtil.v(TAG, "tempL=" + tempL);
        drawCount = (int) ((stopTimeLong - startTimeLong - middleTakeTime) / (tempL));
        drawCount = drawCount + 1;
//      此段代码计算丢失数据点
//        int emptyDataCount = 0;
//        if (muiltMiddleTime) {
//            // 剔除掉没数据的点
//            for (int i = 0; i < kCandleObjList.size(); i++) {
//                long recentlyTime = getRecentlyTimeLong(i, kCandleObjList);
//                if (kCandleObjList.get(i).getTimeLong() - recentlyTime > tempL*2) {
//                    emptyDataCount += (kCandleObjList.get(i).getTimeLong() - recentlyTime) / tempL;
//                }
//            }
//        }
//        drawCount -= emptyDataCount;
//        KLogUtil.v(TAG, "emptyDataCount=" + emptyDataCount);

        KLogUtil.v(TAG, "drawCount=" + drawCount);
    }

    /**
     * 02:00-09:00,10:15/10:30,11:30/13:30
     *
     * @param index 获取最近的一个时间  好计算
     */
    private long getRecentlyTimeLong(int index, List<KCandleObj> kCandleObjList) {
        if (index == 0) {
            return startTimeLong;
        }
        String[] middleTimeArray = middleTimeStr.split(",");
        long[] startTime = new long[middleTimeArray.length];
        for (int i = 0; i < middleTimeArray.length; i++) {
            //毫秒数
            String str[] = middleTimeArray[i].split("\\" + SPLIT_SCHME);

            //接口数据中获取当前年月日，本地时间可能不正确
            Date date = new Date(kCandleObjList.get(index).getTimeLong());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDay = simpleDateFormat.format(date);

            long tempMiddleEnd = KDateUtil.getDate(currentDay + " " + str[1], "yyyy-MM-dd HH:mm").getTime();
//            if (tempMiddleEnd - startTimeLong <= 0) {
//                tempMiddleEnd = 1 * 24 * 60 * 60 * 1000;
//            }
            startTime[i] = tempMiddleEnd;
        }
        long recentlyTime = kCandleObjList.get(index - 1).getTimeLong();
        long offest = kCandleObjList.get(index).getTimeLong() - kCandleObjList.get(index - 1).getTimeLong();

        for (int i = 0; i < startTime.length; i++) {
            long tempOffest = kCandleObjList.get(index).getTimeLong() - startTime[i];
            if (tempOffest > 0) {
                if (tempOffest < offest) {
                    offest = tempOffest;
                    recentlyTime = startTime[i];
                }
            }
        }
        return recentlyTime;
    }

    protected void initMaxMinValue() {
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
//        //已经计算过了
//        if (maxValue > 0)
//            return;
        double sum = 0;
        for (int i = 0; i < kCandleObjList.size(); i++) {
            KCandleObj obj = kCandleObjList.get(i);
            if (i == 0) {
                maxValue = obj.getClose();
                minValue = obj.getClose();
                maxVol = obj.getVol();
            } else {
                if (maxValue < obj.getClose()) {
                    maxValue = obj.getClose();
                }
                if (minValue > obj.getClose()) {
                    minValue = obj.getClose();
                }
                if (maxVol < obj.getVol()) {
                    maxVol = obj.getVol();
                }
            }
            //绘制均线使用
            sum += obj.getClose();
            obj.setNormValue(sum / (i + 1));
        }
        //最大值与昨收的差距
        double divT = Math.abs(maxValue - zuoClosed);
        //最小值与昨收的差距
        double divB = Math.abs(minValue - zuoClosed);
        //取最大差距
        double absMax = Math.max(divT, divB);
        //每个格子的距离    最大差距就是一半latitudeLineNumber的宽度，一半的高度距离
        int halfCiel = latitudeLineNumber / 2;
        //算出每个格子的距离
        double offset = absMax / (halfCiel);

        //从昨收价向上加和向下减
        //最顶部和底部留白距离
        //最高点 最低点 加半个格子()
        startYdouble = zuoClosed - halfCiel * offset - (double) (offset / 2d);
        stopYdouble = zuoClosed + halfCiel * offset + (double) (offset / 2d);

//        KLogUtil.v(TAG, "maxValue=" + maxValue + " minValue=" + minValue + " maxVol=" + maxVol);
    }

    // 是否有多个休市时间  添加此字段作为期货中间多个休市时间计算标志
    private boolean muiltMiddleTime = false;
    protected void initTime() {
//        //init axisXtitles
        axisXtitles.clear();
        try {
            //接口数据中获取当前年月日，本地时间可能不正确
            Date date = new Date(kCandleObjList.get(0).getTimeLong());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDay = simpleDateFormat.format(date);
            //股票交易时间  09:30 11:30/13:00 15:00
            //默认当天的
            String startTime = currentDay + " " + startTimeStr;
            String endTiem = currentDay + " " + stopTimeStr;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            startTimeLong = sdf.parse(startTime).getTime();
            stopTimeLong = sdf.parse(endTiem).getTime();

            middleTakeTime = 0;
            //计算停盘时间
            if (middleTimeStr != null && middleTimeStr.trim().length() > 0) {
                /*如果有多个时间间隔使用,分割的那么暂时不处理*/
                if (middleTimeStr.contains(",")) {
//                    middleTimeStr = MIDDLE_TIME_STR;
                    muiltMiddleTime = true;
                }

            }
            // 有多个休市时间
            if (muiltMiddleTime) {
                String[] middleTimeArray = middleTimeStr.split(",");
                for (int i = 0; i < middleTimeArray.length; i++) {
                    //毫秒数
                    String str[] = middleTimeArray[i].split("\\" + SPLIT_SCHME);
                    long tempMiddleStart = KDateUtil.getDate(currentDay + " " + str[0], "yyyy-MM-dd HH:mm").getTime();
                    long tempMiddleStop = KDateUtil.getDate(currentDay + " " + str[1], "yyyy-MM-dd HH:mm").getTime();
                    if (tempMiddleStop - tempMiddleStart <= 0) {
                        tempMiddleStop += 1 * 24 * 60 * 60 * 1000;
                    }
                    middleTakeTime += tempMiddleStop - tempMiddleStart;
                }

                // 判断是否为同一天,结束时间小于开始时间 已经跨天
                if (stopTimeLong - startTimeLong <= 0) {
                    //默认同一天的结束时间 小于开始时间；津贵所 06:00 04:00
                    //减一天的时间
                    stopTimeLong += 1 * 24 * 60 * 60 * 1000;
                }

                //处理没有休盘时间的品种
//                if (middleTakeTime == 0) {
//                SimpleDateFormat sdfX = new SimpleDateFormat("HH:mm");
//                long offset = (stopTimeLong - startTimeLong) / (longitudeLineNumber - 1);
//                for (int i = 0; i < longitudeLineNumber; i++) {
//                    Date d = new Date(startTimeLong + offset * i);
//                    String s = sdfX.format(d);
//                    if (i > 0 && s.equals("00:00"))
//                        s = "24:00";
//                    axisXtitles.add(s);
//                }


                // 去掉中间对不准的x轴时间
                // 剩下多少经度
                int count = longitudeLineNumber - 2;
                //开始
                axisXtitles.add(startTimeStr);
                for (int i = 0; i < count; i++) {
                    axisXtitles.add("");
                }
                axisXtitles.add(stopTimeStr);

            } else {
                //11:30/13:00
                if (middleTimeStr.contains(SPLIT_SCHME)) {
                    //毫秒数
                    String str[] = middleTimeStr.split("\\" + SPLIT_SCHME);
                    try {
                        middleStart = KDateUtil.getDate(currentDay + " " + str[0], "yyyy-MM-dd HH:mm").getTime();
                        middleStop = KDateUtil.getDate(currentDay + " " + str[1], "yyyy-MM-dd HH:mm").getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    middleTakeTime = end - start;
                    KLogUtil.v(TAG, "middleStart=" + middleStart);
                    KLogUtil.v(TAG, "middleStop=" + middleStop);

                } else {
                    middleStart = 0;
                    middleStop = 0;
                    middleTakeTime = 0;
                }

                // 判断是否为同一天,结束时间小于开始时间 已经跨天
                if (stopTimeLong - startTimeLong <= 0) {
                    //默认同一天的结束时间 小于开始时间；津贵所 06:00 04:00
                    //加一天的时间
                    stopTimeLong += 1 * 24 * 60 * 60 * 1000;
                    //确定跨天的地方是否在停盘时间之前 之内 还是之后
                    if (middleStart > 0 && middleStop > 0) {
                        //跨天的地方在停盘之前
                        if (middleStart - startTimeLong < 0) {
                            //默认休盘的开始时间middleStart就已经跨天了
                            middleStart += 1 * 24 * 60 * 60 * 1000;
                            middleStop += 1 * 24 * 60 * 60 * 1000;
                        } else {
                            //跨天的地方在停盘时间内
                            if (middleStop - startTimeLong < 0) {
                                //middleStart没跨天；middleStop跨天
                                middleStop += 1 * 24 * 60 * 60 * 1000;
                            } else {
                                //middleStart没跨天；middleStop没跨天
                                //跨天时间在 middleStop之后
                            }
                        }
                    }
                }
                //算出停盘的毫秒时间
                middleTakeTime = (middleStop - middleStart);
                //如果遇见传入的时间是错误的 13:30/13:15  恒生指数这种,直接去掉休盘时间
                if (middleTakeTime < 0) {
                    middleStart = 0;
                    middleStop = 0;
                    middleTakeTime = 0;
                }
                KLogUtil.v(TAG, "middleTakeTime=" + middleTakeTime);

                //处理没有休盘时间的品种
                if (middleTakeTime == 0) {
                    SimpleDateFormat sdfX = new SimpleDateFormat("HH:mm");
                    long offset = (stopTimeLong - startTimeLong) / (longitudeLineNumber - 1);
                    for (int i = 0; i < longitudeLineNumber; i++) {
                        Date d = new Date(startTimeLong + offset * i);
                        String s = sdfX.format(d);
                        if (i > 0 && s.equals("00:00"))
                            s = "24:00";
                        axisXtitles.add(s);
                    }
                    return;
                }


                //前后各剩下多少
                int count = (longitudeLineNumber - 1 - 2) / 2;
                //开始
                axisXtitles.add(startTimeStr);
                //开始到 休盘时间
                if (count > 0 && middleTakeTime > 0) {
                    SimpleDateFormat sdfX = new SimpleDateFormat("HH:mm");
                    long offset = (middleStart - startTimeLong) / (count + 1);
                    for (int i = 0; i < count; i++) {
                        Date d = new Date(startTimeLong + offset * (i + 1));
                        String s = sdfX.format(d);
                        if (i > 0 && s.equals("00:00"))
                            s = "24:00";
                        if (!showMoreTime)
                            s = "";
                        axisXtitles.add(s);
                    }
                }


                axisXtitles.add(middleTimeStr);

                //休盘时间到结束
                if (count > 0 && middleTakeTime > 0) {
                    SimpleDateFormat sdfX = new SimpleDateFormat("HH:mm");
                    long offset = (stopTimeLong - middleStop) / (count + 1);
                    for (int i = 0; i < count; i++) {
                        Date d = new Date(middleStop + offset * (i + 1));
                        String s = sdfX.format(d);
                        if (i > 0 && s.equals("00:00"))
                            s = "24:00";
                        if (!showMoreTime)
                            s = "";
                        axisXtitles.add(s);
                    }
                }
                //结束
                axisXtitles.add(stopTimeStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 画区域  分时线 成交量
     *
     * @param canvas
     */
    protected void drawArea(Canvas canvas) {
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        Path path = new Path();
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(areaColor);
//        paint.setAlpha(areaAlph);

        //线条画笔
        Paint linePaint = getPaint();
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineStrokeWidth);

        //均线画笔
        Paint avePaint = getPaint();
        avePaint.setStyle(Paint.Style.FILL);
        avePaint.setColor(averageLineColor);
        avePaint.setStrokeWidth(aveStrokeWidth);

        //成交量画笔
        Paint volPaint = getPaint();
        volPaint.setStyle(Paint.Style.FILL);
        volPaint.setStrokeWidth(volW);

        //最底部的交易量启始位置
        float subBottom = getHeight() - axisYbottomHeight;
        float startX = 0, startY = 0, lx = 0, ly = 0, avgStartX = 0, avgStartY = 0;
        for (int i = 0; i < kCandleObjList.size(); i++) {
            KCandleObj kLineEntity = kCandleObjList.get(i);
            float x = 0;
            // 因为周一涉及到跨周 所以去掉此判断
            if (KDateUtil.getDayOfWeek(new Date(kLineEntity.getTimeLong())) != 1) {
                if (kLineEntity.getTimeLong() < startTimeLong)
                    continue;
                if (kLineEntity.getTimeLong() > stopTimeLong)
                    continue;
            }
            if (asixXByTime) {
                //X轴的真实位置 (stopTimeLong - startTimeLong):datawidth = (longTime- startTimeLong):x
                x = getXByTime(kLineEntity);
                if (x < 0)
                    continue;
            } else {
                //按照画的线条多少定位
                x = (float) axisXleftWidth + dataSpace * i;
            }

            //Y轴的真实坐标位置 (maxValue - minValue):dataheight = (zuoshou- minValue):y
            float y = getYByPrice(kLineEntity.getClose());
            if (startX == 0) {
                path.moveTo(x, getHeight() * mainF - axisYmiddleHeight);
                //如果只有一条数据
                if (kCandleObjList.size() == 1) {
                    //增加几个单位 看见效果
                    path.lineTo(x + 2, getHeight() * mainF - axisYmiddleHeight);
                }
                path.lineTo(x, y);
                startX = x;
                startY = y;

                if (showAverageLine) {
                    double div = kLineEntity.getNormValue() - startYdouble;
                    avgStartX = x;
                    avgStartY = getHeight() * mainF - (float) axisYmiddleHeight - (float) ((div * (double) getDataHeightMian()) / (stopYdouble - startYdouble));
                }

                lx = x;
                ly = y;
            } else {
                lx = x;
                ly = y;
                path.lineTo(x, y);
                //分时线
                canvas.drawLine(startX, startY, x, y, linePaint);

                if (showAverageLine) {
                    //均线
                    double div = kLineEntity.getNormValue() - startYdouble;
                    float newAvgStartY = getHeight() * mainF - (float) axisYmiddleHeight - (float) ((div * (double) getDataHeightMian()) / (stopYdouble - startYdouble));

                    canvas.drawLine(avgStartX, avgStartY, x, newAvgStartY, avePaint);
                    avgStartX = x;
                    avgStartY = newAvgStartY;
                }

                startX = x;
                startY = y;
            }

            if (showSubChart) {
                //成交量
                //这里可以设置成交量的宽度
                volW = dataSpace * 3 / 4;//取dataSpace的百分比
                if (volW <= 0)
                    volW = 1;
                //限制宽度
                if (volW > 20)
                    volW = 20;
                //getDataHeightSub :maxVol = y:vol
                float volY = getDataHeightSub() * (float) kLineEntity.getVol() / (float) maxVol;
                RectF rectF = new RectF(x - volW / 2, subBottom - volY, x + volW / 2, subBottom);
                //没有设置过颜色值，这里算出来
                if (kLineEntity.getColor() == KCandleObj.COLOR_VOL_ENABLE) {
                    //该分钟最后一笔（即时股价）的股价与前一分钟的即时股价（那一分钟的最后一笔）
                    if (i == 0) {
                        volPaint.setColor(candlePostColor);
                    } else {
                        //后一分钟的值大于前一分钟 就是红色
                        if (kLineEntity.getClose() > kCandleObjList.get(i - 1).getClose()) {
                            volPaint.setColor(candlePostColor);
                        } else {
                            volPaint.setColor(candleNegaColor);
                        }
                    }
                } else {
//                //后台接口用 1，0标示颜色，1标示红色，0标示绿色；如果是 1和0,代码则补齐处理
                    if (kLineEntity.getColor() == 1) {
                        volPaint.setColor(candlePostColor);
                    } else if (kLineEntity.getColor() == 0) {
                        volPaint.setColor(candleNegaColor);
                    } else {
                        volPaint.setColor(kLineEntity.getColor());
                    }
                }
                canvas.drawRect(rectF, volPaint);
            }
        }
        //最后封闭区域
        path.lineTo(lx, getDataHeightMian() + (float) axisYtopHeight);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 画纬线
     *
     * @param canvas
     */
    protected void drawLatitudeLine(Canvas canvas) {
        //这里按照数学上的XY轴画线 （0,0）在左下方， 不是按照view的XY轴，（0,0）在左上方
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        if (axisXtitles == null || axisXtitles.size() == 0)
            return;

        double offset = (stopYdouble - startYdouble) / (latitudeLineNumber - 1);
        int index = -1;
        for (int i = 0; i < latitudeLineNumber; i++) {
            double currentY = startYdouble + offset * i;
            axisYleftTitles.add(KNumberUtil.beautifulDouble(currentY, numberScal) + "");

            if (i == 0)
                startYdouble = currentY;
            if (i == latitudeLineNumber - 1)
                stopYdouble = currentY;

            if (currentY == zuoClosed)
                index = i;

            //百分比 (当前-昨收)/昨收
            double s = ((currentY - zuoClosed) / zuoClosed * 100);
            axisYrightTitles.add(KNumberUtil.beautifulDouble(s) + "%");
        }


        int linesCounts = latitudeLineNumber;
        float dateHeight = getDataHeightMian();//高度---div

        Paint paint = getPaint();
        paint.setColor(latitudeColor);
        paint.setStrokeWidth(borderStrokeWidth);

        Paint textPaint = getPaint();
        textPaint.setColor(latitudeFontColor);
        textPaint.setTextSize(latitudeFontSize);

        if (!isAsixTitlein) {
            //如果x轴的文字不在线条内 重新计算一下 左右两边的距离
            float lw = textPaint.measureText(axisYleftTitles.get(0));
            if (axisXleftWidth < lw) {
                //留下的宽度不够文字显示
                axisXleftWidth = lw + commonPadding * 5;
            }
            float lr = textPaint.measureText(axisYrightTitles.get(0));
            if (axisXrightWidth < lr) {
                //留下的宽度不够文字显示
                axisXrightWidth = lr + commonPadding * 5;

            }
//            KLogUtil.v(TAG, "axisXleftWidth="+axisXleftWidth + " axisXrightWidth="+axisXrightWidth);
        } else {
            axisXleftWidth = commonPadding;
            axisXrightWidth = commonPadding;
        }
        dataSpace = getDataWidth() / drawCount;
        float startX = axisXleftWidth;
        float stopX = getWidth() - axisXrightWidth;
        float startY = axisYtopHeight + getDataHeightMian();

        if (showSubChart) {
            //成交量的纬线
//        float subY = getHeight() - axisYbottomHeight;
            float subY = 0;
            float subOffect = getDataHeightSub() / (subLatitudeLineNumber - 1);
            for (int i = 0; i < subLatitudeLineNumber; i++) {
//            subY = subY - i * subOffect;
                subY = mainF * getHeight() + i * subOffect;
                //画全部线条
                canvas.drawLine(startX, subY, stopX, subY, paint);
                //成交量
                String maxv = KNumberUtil.formartBigNumber(maxVol) + "";//手
                float w = textPaint.measureText(maxv);
                textPaint.setColor(textColorLt);
                //这里暂时只显示最大
//                if (i == 0) {
//                    if (isAsixTitlein)
//                        canvas.drawText(maxv, axisXleftWidth, mainF * getHeight() + latitudeFontSize, textPaint);
//                    else
//                        canvas.drawText(maxv, axisXleftWidth - w, mainF * getHeight() + latitudeFontSize, textPaint);
//                }
            }
        }
        float offsetY = 0;
        if (linesCounts > 1) {
            offsetY = dateHeight / (linesCounts - 1);//Y增加的幅度
        }

        for (int i = 0; i < linesCounts; i++) {
            float y = startY - i * offsetY;
            if (index == i) {
                Paint paintEffect = getPaint();
                paintEffect.setStyle(Paint.Style.STROKE);//STROKE 属性必须加上  不加画不出虚线
                paintEffect.setColor(effectColor);
                paintEffect.setStrokeWidth(borderStrokeWidth);//height
                paintEffect.setPathEffect(pathEffect);


                Path p = new Path();
                p.moveTo(startX, y);
                p.lineTo(stopX, y);
//                如果虚线画不出来 请关掉硬件加速 4.4 默认是开启的 (samsung note4)
//                android:hardwareAccelerated="false"
                canvas.drawPath(p, paintEffect);
////                canvas.drawLine(startX, y, stopX, y, paintEffect);//drawLine得不到虚线

                //上面代码画虚线，暂时去掉
//                canvas.drawLine(startX, y, stopX, y, paint);
            } else {
                canvas.drawLine(startX, y, stopX, y, paint);
            }

            //Y轴字体画笔颜色
            if (i > index) {
                textPaint.setColor(textColorGt);
            } else if (i == index) {
                textPaint.setColor(textColorEq);
            } else if (i < index) {
                textPaint.setColor(textColorLt);
            }

//            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;

            String textLeft = axisYleftTitles.get(i);
            float textW = textPaint.measureText(textLeft);

            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            //Rect.left  对于左边X轴的距离， top 对于顶部 Y轴的距离， right - left = 宽度 对于左边X轴的距离， bottom - top = 高度 Y轴
            Rect targetRect = new Rect((int) (startX - textW - commonPadding), (int) (y - (fontMetrics.bottom - fontMetrics.top) / 2), (int) startX, (int) (y + (fontMetrics.bottom - fontMetrics.top) / 2));//the size is copy from canvas.drawRect
            //不需要在线条的内部
            if (isAsixTitlein)
                targetRect = new Rect((int) (startX + commonPadding), (int) (y - (fontMetrics.bottom - fontMetrics.top) / 2), (int) (startX + textW + commonPadding), (int) (y + (fontMetrics.bottom - fontMetrics.top) / 2));//the size is copy from canvas.drawRect


            int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
//            if (i == 0) {
//                canvas.drawText(textLeft, targetRect.centerX(), (y), textPaint);
//            } else if (i == linesCounts - 1) {
//                canvas.drawText(textLeft, targetRect.centerX(), (y + latitudeFontSize), textPaint);
//            } else {
//                canvas.drawText(textLeft, targetRect.centerX(), baseline, textPaint);
//            }


            //
            String textRight = axisYrightTitles.get(i);
            float textWr = textPaint.measureText(textRight);

            Paint.FontMetricsInt fontMetrics2 = textPaint.getFontMetricsInt();
            Rect targetRect2 = new Rect((int) (stopX + commonPadding), (int) (y - (fontMetrics2.bottom - fontMetrics2.top) / 2), (int) (stopX + commonPadding + textWr), (int) (y + (fontMetrics2.bottom - fontMetrics2.top) / 2));//the size is copy from canvas.drawRect
            //不需要在线条的内部
            if (isAsixTitlein)
                targetRect2 = new Rect((int) (stopX - commonPadding - textWr), (int) (y - (fontMetrics2.bottom - fontMetrics2.top) / 2), (int) (stopX - commonPadding), (int) (y + (fontMetrics2.bottom - fontMetrics2.top) / 2));//the size is copy from canvas.drawRect

//            canvas.drawRect(targetRect2, textPaint);
            int baseline2 = targetRect2.top + (targetRect2.bottom - targetRect2.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
//            if (i == 0)
//                canvas.drawText(textRight, targetRect2.centerX(), (y - (fontMetrics2.bottom) / 2), textPaint);
//            else if (i == linesCounts - 1)
//                canvas.drawText(textRight, targetRect2.centerX(), (y + latitudeFontSize), textPaint);
//            else
//                canvas.drawText(textRight, targetRect2.centerX(), baseline2, textPaint);
        }

    }

    /**
     * 画纬线方向的文字
     *
     * @param canvas
     */
    protected void drawLatitudeTitle(Canvas canvas) {
        //这里按照数学上的XY轴画线 （0,0）在左下方， 不是按照view的XY轴，（0,0）在左上方
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        if (axisXtitles == null || axisXtitles.size() == 0)
            return;


        double offset = (stopYdouble - startYdouble) / (latitudeLineNumber - 1);
        int index = axisXtitles.size() / 2;
        for (int i = 0; i < latitudeLineNumber; i++) {
            double currentY = startYdouble + offset * i;
            axisYleftTitles.add(KNumberUtil.beautifulDouble(currentY) + "");

            if (i == 0)
                startYdouble = currentY;
            if (i == latitudeLineNumber - 1)
                stopYdouble = currentY;

            if (currentY == zuoClosed)
                index = i;

            //百分比 (当前-昨收)/昨收
            double s = ((currentY - zuoClosed) / zuoClosed * 100);
            axisYrightTitles.add(KNumberUtil.beautifulDouble(s) + "%");
        }


        int linesCounts = latitudeLineNumber;
        float dateHeight = getDataHeightMian();//高度---div

        Paint paint = getPaint();
        paint.setColor(latitudeColor);
        paint.setStrokeWidth(borderStrokeWidth);

        Paint textPaint = getPaint();
        textPaint.setColor(latitudeFontColor);
        textPaint.setTextSize(latitudeFontSize);

        if (!isAsixTitlein) {
            //如果x轴的文字不在线条内 重新计算一下 左右两边的距离
            float lw = textPaint.measureText(axisYleftTitles.get(0));
            if (axisXleftWidth < lw) {
                //留下的宽度不够文字显示
                axisXleftWidth = lw + commonPadding * 5;
            }
            float lr = textPaint.measureText(axisYrightTitles.get(0));
            if (axisXrightWidth < lr) {
                //留下的宽度不够文字显示
                axisXrightWidth = lr + commonPadding * 5;

            }
//            KLogUtil.v(TAG, "axisXleftWidth="+axisXleftWidth + " axisXrightWidth="+axisXrightWidth);
        } else {
            axisXleftWidth = commonPadding;
            axisXrightWidth = commonPadding;
        }
        dataSpace = getDataWidth() / drawCount;
        float startX = axisXleftWidth;
        float stopX = getWidth() - axisXrightWidth;
        float startY = axisYtopHeight + getDataHeightMian();

        if (showSubChart) {
            //成交量的纬线
//        float subY = getHeight() - axisYbottomHeight;
            float subY = 0;
            float subOffect = getDataHeightSub() / (subLatitudeLineNumber - 1);
            for (int i = 0; i < subLatitudeLineNumber; i++) {
//            subY = subY - i * subOffect;
                subY = mainF * getHeight() + i * subOffect;
                //画全部线条
//                canvas.drawLine(startX, subY, stopX, subY, paint);
                //成交量
                String maxv = KNumberUtil.formartBigNumber(maxVol) + "";//手
                float w = textPaint.measureText(maxv);
                textPaint.setColor(textColorLt);
                //这里暂时只显示最大
                if (i == 0) {
                    if (isAsixTitlein)
                        canvas.drawText(maxv, axisXleftWidth, mainF * getHeight() + latitudeFontSize, textPaint);
                    else
                        canvas.drawText(maxv, axisXleftWidth - w, mainF * getHeight() + latitudeFontSize, textPaint);
                }
            }
        }
        float offsetY = 0;
        if (linesCounts > 1) {
            offsetY = dateHeight / (linesCounts - 1);//Y增加的幅度
        }

        for (int i = 0; i < linesCounts; i++) {
            float y = startY - i * offsetY;
            if (index == i) {
                Paint paintEffect = getPaint();
                paintEffect.setStyle(Paint.Style.STROKE);//STROKE 属性必须加上  不加画不出虚线
                paintEffect.setColor(effectColor);
                paintEffect.setStrokeWidth(borderStrokeWidth);//height
                paintEffect.setPathEffect(pathEffect);


                Path p = new Path();
                p.moveTo(startX, y);
                p.lineTo(stopX, y);
//                如果虚线画不出来 请关掉硬件加速 4.4 默认是开启的 (samsung note4)
//                android:hardwareAccelerated="false"
//                canvas.drawPath(p, paintEffect);
////                canvas.drawLine(startX, y, stopX, y, paintEffect);//drawLine得不到虚线

                //上面代码画虚线，暂时去掉
//                canvas.drawLine(startX, y, stopX, y, paint);
            } else {
//                canvas.drawLine(startX, y, stopX, y, paint);
            }

            //Y轴字体画笔颜色
            if (i > index) {
                textPaint.setColor(textColorGt);
            } else if (i == index) {
                textPaint.setColor(textColorEq);
            } else if (i < index) {
                textPaint.setColor(textColorLt);
            }

//            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;

            String textLeft = axisYleftTitles.get(i);
            float textW = textPaint.measureText(textLeft);


            //Rect.left  对于左边X轴的距离， top 对于顶部 Y轴的距离， right - left = 宽度 对于左边X轴的距离， bottom - top = 高度 Y轴
            Rect targetRect = new Rect((int) (startX - textW - commonPadding), (int) (y - (latitudeFontSize) / 2), (int) startX, (int) (y + (latitudeFontSize) / 2));//the size is copy from canvas.drawRect
            //不需要在线条的内部
            if (isAsixTitlein)
                targetRect = new Rect((int) (startX + commonPadding), (int) (y - (latitudeFontSize) / 2), (int) (startX + textW + commonPadding), (int) (y + (latitudeFontSize) / 2));//the size is copy from canvas.drawRect

            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
            if (i == 0) {
                canvas.drawText(textLeft, targetRect.centerX(), (y), textPaint);
            } else if (i == linesCounts - 1) {
                canvas.drawText(textLeft, targetRect.centerX(), (y + latitudeFontSize), textPaint);
            } else {
                drawText(canvas, textLeft, targetRect, textPaint);
            }

            String textRight = axisYrightTitles.get(i);
            float textWr = textPaint.measureText(textRight);

            Paint.FontMetricsInt fontMetrics2 = textPaint.getFontMetricsInt();
            Rect targetRect2 = new Rect((int) (stopX + commonPadding), (int) (y - (fontMetrics2.bottom - fontMetrics2.top) / 2), (int) (stopX + commonPadding + textWr), (int) (y + (fontMetrics2.bottom - fontMetrics2.top) / 2));//the size is copy from canvas.drawRect
            //不需要在线条的内部
            if (isAsixTitlein)
                targetRect2 = new Rect((int) (stopX - commonPadding - textWr), (int) (y - (fontMetrics2.bottom - fontMetrics2.top) / 2), (int) (stopX - commonPadding), (int) (y + (fontMetrics2.bottom - fontMetrics2.top) / 2));//the size is copy from canvas.drawRect

//            canvas.drawRect(targetRect2, textPaint);
            int baseline2 = targetRect2.top + (targetRect2.bottom - targetRect2.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
            if (i == 0)
                canvas.drawText(textRight, targetRect2.centerX(), (y - (fontMetrics2.bottom) / 2), textPaint);
            else if (i == linesCounts - 1)
                canvas.drawText(textRight, targetRect2.centerX(), (y + latitudeFontSize), textPaint);
            else
                drawText(canvas, textRight, targetRect2, textPaint);
        }

    }

    /**
     * 画经度线 和title
     *
     * @param canvas
     */
    protected void drawLongitudeLineTitle(Canvas canvas) {
        if (kCandleObjList == null || kCandleObjList.size() == 0)
            return;
        if (axisXtitles == null || axisXtitles.size() == 0)
            return;

        int linesCounts = longitudeLineNumber;
        float dateHeight = getDataHeightMian();//经线的高度

        Paint paint = getPaint();
        paint.setColor(longitudeColor);
        paint.setStrokeWidth(borderStrokeWidth);

        Paint textPaint = getPaint();
        textPaint.setColor(longitudeFontColor);
        textPaint.setTextSize(longitudeFontSize);
//        textPaint.setTextAlign(Paint.Align.CENTER);

        float startX = axisXleftWidth;
        float startY = axisYtopHeight;

        if (showSubChart) {
            float suboffset = getDataWidth() / (subLongitudeLineNumber - 1);
            float substartY = axisYtopHeight + getDataHeightMian() + axisYmiddleHeight;
            float substopY = getHeight() - axisYbottomHeight;
            for (int i = 0; i < subLongitudeLineNumber; i++) {
                float x = startX + i * suboffset;
                canvas.drawLine(x, substartY, x, substopY, paint);
            }
        }

        float offsetX = 0;
        if (linesCounts > 1) {
            offsetX = getDataWidth() / (linesCounts - 1);//x增加的幅度
        }
        for (int i = 0; i < linesCounts; i++) {
            float x = startX + i * offsetX;
            float stopY = startY + dateHeight;
            canvas.drawLine(x, startY, x, stopY, paint);

            if (i >= axisXtitles.size())
                return;
            String text = axisXtitles.get(i);
            float textW = textPaint.measureText(text);
            float tox = x - textW / 2;
            if (i == 0)
                tox = x;
            if (i == linesCounts - 1)
                tox = x - textW;
            canvas.drawText(text, tox, stopY + axisYmiddleHeight / 2 + longitudeFontSize / 2, textPaint);
        }
    }

    /**
     * 绘制持仓辅助线
     *
     * @param canvas
     */
    protected void drawHelpLineForTradeOrder(Canvas canvas) {
        if (listOrder == null || listOrder.size() == 0) {
            return;
        }

        float startX = axisXleftWidth;
        float stopX = getWidth() - axisXrightWidth;
        float startY = axisYtopHeight + getDataHeightMian();
        Paint paint = getPaint();
        paint.setStrokeWidth(borderStrokeWidth);
        for (TradeOrder tradeOrder : listOrder) {
            double orderYDouble = Double.parseDouble(tradeOrder.getCreatePrice());
            if (orderYDouble < startYdouble || orderYDouble > stopYdouble) {// 不在该区域不画线

            } else {
                double offest = orderYDouble - startYdouble;
                float y = startY - (float) (offest / (stopYdouble - startYdouble) * getDataHeightMian());
                if (tradeOrder.getType() == TradeOrder.BUY_UP) {
                    paint.setColor(textColorGt);
                } else {
                    paint.setColor(textColorLt);
                }
                canvas.drawLine(startX, y, stopX, y, paint);
            }
        }
    }

    /**
     * 绘制行情提醒辅助线
     *
     * @param canvas
     */
    protected void drawHelpLineForProductNotice(Canvas canvas) {
        if (listProductNotice == null || listProductNotice.size() == 0) {
            return;
        }
        float startX = axisXleftWidth;
        float stopX = getWidth() - axisXrightWidth;
        float startY = axisYtopHeight + getDataHeightMian();
        Paint paintEffect = getPaint();
        paintEffect.setStyle(Paint.Style.STROKE);//STROKE 属性必须加上  不加画不出虚线
        paintEffect.setColor(lineColor);
        paintEffect.setStrokeWidth(borderStrokeWidth);//height
        paintEffect.setPathEffect(pathEffect);

        for (ProductNotice productNotice : listProductNotice) {
            double orderYDouble = Double.parseDouble(productNotice.getCustomizedProfit());
            if (orderYDouble < startYdouble || orderYDouble > stopYdouble) {// 不在该区域不画线

            } else {
                double offest = orderYDouble - startYdouble;
                float y = startY - (float) (offest / (stopYdouble - startYdouble) * getDataHeightMian());
                Path p = new Path();
                p.moveTo(startX, y);
                p.lineTo(stopX, y);
                canvas.drawPath(p, paintEffect);
            }
        }
    }

    List<TradeOrder> listOrder;
    List<ProductNotice> listProductNotice;

    /**
     * 设置持仓辅助线数据
     *
     * @param listOrder
     */
    public void setHelpLineForTradeOrder(List<TradeOrder> listOrder) {
        this.listOrder = listOrder;
        postInvalidate();
    }

    public void setHelpLineForProductNotice(List<ProductNotice> listProductNotice) {
        this.listProductNotice = listProductNotice;
        postInvalidate();
    }

    private String codes;

    public void setCodes(String codes) {
        this.codes = codes;
    }
}
