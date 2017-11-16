package com.trade.eight.view.rise;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.service.NumberUtil;

import java.text.DecimalFormat;

public class RiseNumberTextView extends TextView {

    private int mType;
    private float mStartNum;
    private float mEndNum;
    private int mDecimalPlace;
    private int mDuration;
    private int mMaxDecimalPlace;
    private int mMinDecimalPlace;
    private DecimalFormat mDecimalFormat = new DecimalFormat("######0.000");
    private boolean mRunning;

    private Optional optional;// 旨在格式化数据

    public Optional getOptional() {
        return optional;
    }

    public void setOptional(Optional optional) {
        this.optional = optional;
        try {
            int format = ProFormatConfig.getProFormatMap(optional.getType()+"|"+optional.getTreaty());
            if (format <= 0) {
                //保留自己的小数点位数
                format = NumberUtil.getPointPow(optional.getLastPrice());
            }
            mDecimalFormat = ProFormatConfig.getDecimalFormat(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RiseNumberTextView(Context context) {
        this(context, null);
    }

    public RiseNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initFormat();
    }

    public void start() {
        if (!mRunning) {
            mRunning = true;

            switch (mType) {
                case 0:
                    runFloat();
                    break;
                case 1:
                    runInt();
                    break;
            }
        }
    }

    public void setType(NumberType type) {
        mType = type.ordinal();
    }

    public void setNum(float startNum, float endNum) {
        mStartNum = startNum;
        mEndNum = endNum;
    }

    public void setNum(int startNum, int endNum) {
        mStartNum = startNum;
        mEndNum = endNum;
    }

    public void setStartNum(float startNum) {
        mStartNum = startNum;
        reSetFormat();
    }

    public void setStartNum(int startNum) {
        mStartNum = startNum;
    }

    public void setEndNum(float endNum) {
        mEndNum = endNum;
        reSetFormat();
    }

    public void setEndNum(int endNum) {
        mEndNum = endNum;
    }

    public void setDecimal(int decimalPlace) {
        if (mDecimalPlace > mMinDecimalPlace && mDecimalPlace < mMaxDecimalPlace)
            mDecimalPlace = decimalPlace;
    }

    public void setDuration(int duraion) {
        mDuration = duraion;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Resources res = context.getResources();

        mType = res.getInteger(R.integer.default_type);
        mStartNum = res.getInteger(R.integer.default_start);
        mEndNum = res.getInteger(R.integer.default_end);
        mMaxDecimalPlace = res.getInteger(R.integer.max_decimal_place);
        mMinDecimalPlace = res.getInteger(R.integer.min_decimal_place);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RiseNumberTextView);
        mDecimalPlace = a.getInteger(R.styleable.RiseNumberTextView_decimalPlace,
                res.getInteger(R.integer.default_decimal_place));
        if (mDecimalPlace < mMinDecimalPlace || mDecimalPlace > mMaxDecimalPlace)
            mDecimalPlace = res.getInteger(R.integer.default_decimal_place);
        mType = a.getInteger(R.styleable.RiseNumberTextView_type, mType);
        mStartNum = a.getFloat(R.styleable.RiseNumberTextView_startNum, mStartNum);
        mEndNum = a.getFloat(R.styleable.RiseNumberTextView_endNum, mStartNum);
        mDuration = a.getInteger(R.styleable.RiseNumberTextView_duration,
                res.getInteger(R.integer.default_duration));
        a.recycle();
    }

    private void initFormat() {
//        StringBuilder pattern = new StringBuilder("##0.");
//        for (int i = 0; i < mDecimalPlace; i++)
//            pattern.append("0");

//        mDecimalFormat = new DecimalFormat(pattern.toString());
    }

    /**
     * 根据开始的值结束的值取最大位数的小数点
     */
    void reSetFormat() {
        try {
            int startDot = 0, endDot = 0;
            if (String.valueOf(mStartNum).contains(".")) {
                startDot = String.valueOf(mStartNum).split("\\.")[1].length();
            }
            if (String.valueOf(mEndNum).contains(".")) {
                endDot = String.valueOf(mEndNum).split("\\.")[1].length();
            }
            mDecimalPlace = Math.max(startDot, endDot);
            if (mDecimalPlace < mMinDecimalPlace || mDecimalPlace > mMaxDecimalPlace)
                mDecimalPlace = 1;
            initFormat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * int 类型的变化
     * @param value
     */
    public void setTextByAnimation (int value) {
        try {
            setType(NumberType.INTEGER);

            if (mStartNum == 0) {
                //first time
                setStartNum(value);
            } else {
                //使用上一次的value
                String s = getText().toString();
                if (s.trim().length() == 0) {
                    setText(value + "");
                    return;
                }
                int start= 0;
                try {
                    start = Integer.parseInt(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (start == 0)
//                    start = value;
                setStartNum(start);
            }

            setEndNum(value);
            start();
        } catch (Exception e) {
            e.printStackTrace();
            setText(value+"");
        }
    }


    /**
     * 设置方法调用这个
     * @param value
     */
    public void setTextByAnimation (float value) {
        try {
            if (mStartNum == 0) {
                //first time
                setStartNum(value);
            } else {
                //使用上一次的value
                String s = getText().toString();
                if (s.trim().length() == 0) {
                    setText(value + "");
                    return;
                }
                float start= 0;
                try {
                    start = Float.parseFloat(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (start == 0)
//                    start = value;
                setStartNum(start);
            }

            setEndNum(value);
            start();
        } catch (Exception e) {
            e.printStackTrace();
            setText(value+"");
        }
    }

    /**
     * string to float
     * @param value
     */
    public void setTextByAnimation (String value) {
        try {
            setTextByAnimation(Float.parseFloat(value));
        } catch (Exception e) {
            e.printStackTrace();
            setText(value);
        }
    }

    private void runFloat() {
        if (android.os.Build.VERSION.SDK_INT <= 11) {
            setText(mEndNum + "");
            return;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(mStartNum, mEndNum);
        animator.setDuration(mDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setText(mDecimalFormat.format(Float.parseFloat(animation.getAnimatedValue().toString())));
                if (animation.getAnimatedFraction() >= 1)
                    mRunning = false;
            }
        });
        animator.start();
    }

    private void runInt() {
        if (android.os.Build.VERSION.SDK_INT <= 11) {
            setText(mEndNum + "");
            return;
        }

        ValueAnimator animator = ValueAnimator.ofInt((int) mStartNum, (int) mEndNum);
        animator.setDuration(mDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setText(animation.getAnimatedValue().toString());
                if (animation.getAnimatedFraction() >= 1)
                    mRunning = false;
            }
        });
        animator.start();
    }
}
