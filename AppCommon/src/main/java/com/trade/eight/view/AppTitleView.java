package com.trade.eight.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;
import java.util.logging.Handler;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/5.
 * app的头部view
 */

public class AppTitleView extends LinearLayout {

    private Context context;
    View line_rootview;
    ImageView img_go_back;
    public TextView text_title;
    private TextView text_product_isclose;
    TextView text_rightbtn;
    ImageView img_rightbtn_1;
    ImageView img_rightbtn;
    View view_spilt;
    BaseActivity baseActivity;// 供返回事件销毁处理
    android.os.Handler.Callback rightBtnCallback;// 右边文字按钮点击事件
    android.os.Handler.Callback rightImgCallback_1;//右边图片按钮点击事件
    android.os.Handler.Callback rightImgCallback;//右边图片按钮点击事件


    public AppTitleView(Context context) {
        super(context);
        init(context);
    }

    public AppTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AppTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        this.context = context;
        View v = View.inflate(context, R.layout.view_title_view, null);
        addView(v);
        initView(v);
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        line_rootview = view.findViewById(R.id.line_rootview);
        img_go_back = (ImageView) view.findViewById(R.id.img_go_back);
        text_title = (TextView) view.findViewById(R.id.text_title);
        text_product_isclose = (TextView) view.findViewById(R.id.text_product_isclose);
        text_rightbtn = (TextView) view.findViewById(R.id.text_rightbtn);
        img_rightbtn_1 = (ImageView) view.findViewById(R.id.img_rightbtn_1);
        img_rightbtn = (ImageView) view.findViewById(R.id.img_rightbtn);
        view_spilt = view.findViewById(R.id.view_spilt);
        img_go_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseActivity != null) {
                    baseActivity.doMyfinish();
                }
            }
        });

        text_rightbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rightBtnCallback != null) {
                    rightBtnCallback.handleMessage(new Message());
                }
            }
        });

        img_rightbtn_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rightImgCallback_1 != null) {
                    rightImgCallback_1.handleMessage(new Message());
                }
            }
        });

        img_rightbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rightImgCallback != null) {
                    rightImgCallback.handleMessage(new Message());
                }
            }
        });
    }

    /**
     * 设置返回按钮资源
     *
     * @param drawable
     */
    public void setBackViewResource(int drawable) {
        img_go_back.setImageResource(drawable);
    }

    /**
     * 休市标志是否显示
     * @param isVisiable
     */
    public void setProCloseViewVisiable(boolean isVisiable) {
        if (isVisiable) {
            text_product_isclose.setVisibility(VISIBLE);
        } else {
            text_product_isclose.setVisibility(GONE);
        }
    }

    /**
     * 设置分割线是否显示
     *
     * @param show
     */
    public void setSpiltLineDisplay(boolean show) {
        if (show) {
            view_spilt.setVisibility(VISIBLE);
        } else {
            view_spilt.setVisibility(GONE);
        }
    }

    /**
     * 设置分割线颜色
     *
     * @param color
     */
    public void setSpiltLineColor(int color) {
        view_spilt.setBackgroundColor(context.getResources().getColor(color));

    }

    /**
     * 设置标题字体颜色
     *
     * @param color
     */
    public void setTitleTextColor(int color) {
        text_title.setTextColor(context.getResources().getColor(color));
    }


    /**
     * 设置头部的背景颜色
     *
     * @param color
     */
    public void setRootViewBG(int color) {
        line_rootview.setBackgroundColor(context.getResources().getColor(color));
    }

    /**
     * 设置标题
     *
     * @param id
     */
    public void setAppCommTitle(int id) {
        text_title.setText(id);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setAppCommTitle(String title) {
        text_title.setText(title);
    }

    /**
     * 设置右边按钮文字
     *
     * @param id
     */
    public void setRightBtnText(int id) {
        text_rightbtn.setText(id);
    }

    /**
     * 设置右边按钮文字
     *
     * @param title
     */
    public void setRightBtnText(String title) {
        text_rightbtn.setText(title);
    }

    /**
     * 最右边图片按钮是否显示 以及显示的icon
     *
     * @param isShow
     * @param drawale
     */
    public void setRightImgBtn(boolean isShow, int drawale) {
        if (isShow) {
            img_rightbtn.setVisibility(VISIBLE);
            img_rightbtn.setImageResource(drawale);
        } else {
            img_rightbtn.setVisibility(GONE);
        }
    }

    /**
     * 最右边图片(有变数第二个)按钮是否显示 以及显示的icon
     *
     * @param isShow
     * @param drawale
     */
    public void setRightImgBtn_1(boolean isShow, int drawale) {
        if (isShow) {
            img_rightbtn_1.setVisibility(VISIBLE);
            img_rightbtn_1.setImageResource(drawale);
        } else {
            img_rightbtn_1.setVisibility(GONE);
        }
    }

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }


    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public android.os.Handler.Callback getRightImgCallback() {
        return rightImgCallback;
    }

    public void setRightImgCallback(android.os.Handler.Callback rightImgCallback) {
        this.rightImgCallback = rightImgCallback;
    }

    public android.os.Handler.Callback getRightBtnCallback() {
        return rightBtnCallback;
    }

    public void setRightBtnCallback(android.os.Handler.Callback rightBtnCallback) {
        this.rightBtnCallback = rightBtnCallback;
    }

    public android.os.Handler.Callback getRightImgCallback_1() {
        return rightImgCallback_1;
    }

    public void setRightImgCallback_1(android.os.Handler.Callback rightImgCallback_1) {
        this.rightImgCallback_1 = rightImgCallback_1;
    }
}
