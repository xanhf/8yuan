package com.trade.eight.moudle.product;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.product.ProductNoticeInfo;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.Utils;

import java.text.DecimalFormat;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/21.
 * 行情提醒 添加/编辑面板工具类
 */

public class ProductNoticeListUtil implements View.OnClickListener {
    BaseActivity context;
    ProductNoticeInfo productNoticeInfo;
    ProductNotice productNotice;
    ProductNoticeUtil productNoticeUtil;
    Dialog pnEditDlg;

    TextView text_pn_left;
    TextView text_pn_title;
    TextView text_pn_right;
    EditText edit_pnedit_price;
    LinearLayout line_pn_inputtips;
    TextView text_pn_inputtips;
    RadioGroup rg_pn_range;
    TextView text_pn_rangtips;
    LinearLayout line_check_pn_day;
    CheckBox check_pn_day;
    LinearLayout line_check_pn_week;
    CheckBox check_pn_week;
    Button btn_delete_pn;

    private int NOTICERANGE_DAY = 1;// 提醒当日有效
    private int NOTICERANGE_WEEK = 2;// 提醒本周有效
    int noticeRange = NOTICERANGE_DAY;

    String[] pointList = null;
    private int selectPoint = 0;

    DecimalFormat decimalFormat = new DecimalFormat("######.##");


    View  upView,downView;
    TextView textUpView,textDownView;


    public ProductNoticeListUtil(BaseActivity context, ProductNoticeInfo productNoticeInfo, ProductNoticeUtil productNoticeUtil) {
        this.context = context;
        this.productNoticeInfo = productNoticeInfo;
        this.productNoticeUtil = productNoticeUtil;

        initData();
        initListDlg();
    }

    public ProductNoticeListUtil(BaseActivity context, ProductNoticeInfo productNoticeInfo, ProductNoticeUtil productNoticeUtil, ProductNotice productNotice) {
        this.context = context;
        this.productNoticeInfo = productNoticeInfo;
        this.productNotice = productNotice;
        this.productNoticeUtil = productNoticeUtil;

        initData();
        initListDlg();
    }

    private void initListDlg() {
        if (pnEditDlg != null && pnEditDlg.isShowing())
            return;
        pnEditDlg = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        pnEditDlg.setContentView(R.layout.layout_productnotice_edit);
        Window w = pnEditDlg.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) context.getResources().getDimension(R.dimen.margin_481dp);
        w.setGravity(Gravity.BOTTOM);
        w.setAttributes(params);
        w.setWindowAnimations(R.style.dialog_trade_ani);
        pnEditDlg.setCancelable(true);
        findViews();
    }

    public void showDlg() {
        pnEditDlg.show();
    }

    private void initData() {
        pointList = productNoticeInfo.getPointListStr().split(",");
    }

    private void findViews() {
        text_pn_left = (TextView) pnEditDlg.findViewById(R.id.text_pn_left);
        text_pn_title = (TextView) pnEditDlg.findViewById(R.id.text_pn_title);
        text_pn_right = (TextView) pnEditDlg.findViewById(R.id.text_pn_right);

        text_pn_left.setOnClickListener(this);
        text_pn_right.setOnClickListener(this);

        edit_pnedit_price = (EditText) pnEditDlg.findViewById(R.id.edit_pnedit_price);
        edit_pnedit_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_pn_inputtips.setVisibility(View.GONE);
                line_pn_inputtips.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = edit_pnedit_price.getText().toString();
                showRangTips();
                if (!TextUtils.isEmpty(input)) {
                    int index = input.indexOf(".");
                    if (index > 0) {
                        if (index + ProFormatConfig.getProFormatMap(productNoticeInfo.getExcode()+"|"+productNoticeInfo.getCode()) < input.length() - 1) {
                            edit_pnedit_price.setText(input.substring(0, index + ProFormatConfig.getProFormatMap    (productNoticeInfo.getExcode()+"|"+productNoticeInfo.getCode())+1));
                            edit_pnedit_price.setSelection(edit_pnedit_price.getText().toString().length());
                        }
                    }
                }
            }
        });
        text_pn_inputtips = (TextView) pnEditDlg.findViewById(R.id.text_pn_inputtips);
        line_pn_inputtips = (LinearLayout) pnEditDlg.findViewById(R.id.line_pn_inputtips);
        rg_pn_range = (RadioGroup) pnEditDlg.findViewById(R.id.rg_pn_range);
        for (int i = 0; i < rg_pn_range.getChildCount(); i++) {
            ((RadioButton) rg_pn_range.getChildAt(i)).setText(pointList[i]);
        }
        rg_pn_range.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        //选中提示语跟随变化
                        selectPoint = i;
                        showRangTips();
                    }
                }
            }
        });
        text_pn_rangtips = (TextView) pnEditDlg.findViewById(R.id.text_pn_rangtips);
        text_pn_rangtips.setText(context.getString(R.string.product_notice_tips3, productNoticeInfo.getName(), "~", "~"));
        line_check_pn_day = (LinearLayout) pnEditDlg.findViewById(R.id.line_check_pn_day);
        line_check_pn_day.setOnClickListener(this);
        check_pn_day = (CheckBox) pnEditDlg.findViewById(R.id.check_pn_day);
        line_check_pn_week = (LinearLayout) pnEditDlg.findViewById(R.id.line_check_pn_week);
        line_check_pn_week.setOnClickListener(this);
        check_pn_week = (CheckBox) pnEditDlg.findViewById(R.id.check_pn_week);
        btn_delete_pn = (Button) pnEditDlg.findViewById(R.id.btn_delete_pn);
        btn_delete_pn.setOnClickListener(this);

        upView = pnEditDlg.findViewById(R.id.upView);
        downView = pnEditDlg.findViewById(R.id.downView);

        upView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upView.setSelected(true);
                downView.setSelected(false);
            }
        });
        downView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upView.setSelected(false);
                downView.setSelected(true);
            }
        });

        upView.setSelected(true);
        downView.setSelected(false);
        displayView();
    }

    /**
     * 编辑状态初始化界面
     */
    private void displayView() {
        if (productNotice == null) {
            text_pn_title.setText("添加提醒");
            btn_delete_pn.setVisibility(View.GONE);
            return;
        }

        if(productNotice.getBuyType()== TradeOrder.BUY_DOWN){
            upView.setSelected(false);
            downView.setSelected(true);
        }else{
            upView.setSelected(true);
            downView.setSelected(false);
        }

        text_pn_title.setText("编辑提醒");

        edit_pnedit_price.setText(productNotice.getCustomizedProfit());
        for (int i = 0; i < pointList.length; i++) {
            if (pointList[i].equals(productNotice.getFloatUpProfit())) {
                //选中提示语跟随变化
                selectPoint = i;
                ((RadioButton) rg_pn_range.getChildAt(i)).setChecked(true);
            }
        }
        showRangTips();
        if (productNotice.getCycleType() == 1) {
            check_pn_day.setChecked(true);
            check_pn_week.setChecked(false);
            noticeRange = NOTICERANGE_DAY;
        } else {
            check_pn_day.setChecked(false);
            check_pn_week.setChecked(true);
            noticeRange = NOTICERANGE_WEEK;
        }
        btn_delete_pn.setVisibility(View.VISIBLE);
    }

    /**
     * 范围提示语
     */
    private void showRangTips() {
        String input = edit_pnedit_price.getText().toString();
        if (TextUtils.isEmpty(input)) {
            text_pn_rangtips.setText(context.getString(R.string.product_notice_tips3, productNoticeInfo.getName(), "~", "~"));
            return;
        }
        double low = NumberUtil.subtract(Double.parseDouble(input), Double.parseDouble(pointList[selectPoint]));
        double high = NumberUtil.add(Double.parseDouble(input), Double.parseDouble(pointList[selectPoint]));
        text_pn_rangtips.setText(context.getString(R.string.product_notice_tips3,
                productNoticeInfo.getName(),
                ProFormatConfig.formatByCodes(productNoticeInfo.getExcode() + "|" + productNoticeInfo.getCode(), low),
                ProFormatConfig.formatByCodes(productNoticeInfo.getExcode() + "|" + productNoticeInfo.getCode(), high)));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_delete_pn://删除提醒
                delateProductNotice();
                break;
            case R.id.line_check_pn_day:
                if (noticeRange == NOTICERANGE_DAY) {

                } else {
                    check_pn_day.setChecked(true);
                    check_pn_week.setChecked(false);
                    noticeRange = NOTICERANGE_DAY;
                }
                break;
            case R.id.line_check_pn_week:
                if (noticeRange == NOTICERANGE_WEEK) {

                } else {
                    check_pn_day.setChecked(false);
                    check_pn_week.setChecked(true);
                    noticeRange = NOTICERANGE_WEEK;
                }
                break;
            case R.id.text_pn_left:
//                EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_EDIT_CANCLE));
                Utils.hideSoft(context, edit_pnedit_price);
                pnEditDlg.dismiss();
                break;
            case R.id.text_pn_right:
                Utils.hideSoft(context, edit_pnedit_price);
                if (productNotice == null) {
                    saveProductNotice();
                } else {
                    editProductNotice();
                }
                break;
        }
    }

    /**
     * 存储行情提醒
     */
    private void saveProductNotice() {

        String customizedProfit = edit_pnedit_price.getText().toString();
        if (TextUtils.isEmpty(customizedProfit)) {
            line_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setText(R.string.product_notice_error1);
            return;
        }
        if(customizedProfit.length()>10){
            line_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setText(R.string.product_notice_error2);
            return;
        }
        text_pn_right.setClickable(false);
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(context);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("excode", productNoticeInfo.getExcode());
        request.put("code", productNoticeInfo.getCode());
        request.put("customizedProfit", customizedProfit);
        request.put("floatPoint", pointList[selectPoint] + "");
        request.put("cycleType", noticeRange + "");
        if(upView.isSelected()){
            request.put("buyType", TradeOrder.BUY_UP + "");
        }else{
            request.put("buyType", TradeOrder.BUY_DOWN + "");
        }
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.URL_PRODUCRTNOTICE_ADD, request, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                if(resultCode.equals("260009")){
                    line_pn_inputtips.setVisibility(View.VISIBLE);
                    text_pn_inputtips.setVisibility(View.VISIBLE);
                    text_pn_inputtips.setText(resultMsg);
                }
                context.showCusToast(resultMsg);
                text_pn_right.setClickable(true);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response,TempObject.class);
                if (commonResponse.success) {
                    pnEditDlg.dismiss();
                    productNoticeUtil.getProductNoticeList();
                    EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_SAVE));
                }
            }
        },true);
    }

    /**
     * 编辑行情提醒
     */
    private void editProductNotice() {

        String customizedProfit = edit_pnedit_price.getText().toString();
        if (TextUtils.isEmpty(customizedProfit)) {
            line_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setText(R.string.product_notice_error1);
            return;
        }
        if(customizedProfit.length()>10){
            line_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setVisibility(View.VISIBLE);
            text_pn_inputtips.setText(R.string.product_notice_error2);
            return;
        }
        text_pn_right.setClickable(false);
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(context);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("excode", productNoticeInfo.getExcode());
        request.put("code", productNoticeInfo.getCode());
        request.put("customizedProfit", customizedProfit);
        request.put("floatPoint", pointList[selectPoint] + "");
        request.put("cycleType", noticeRange + "");
        request.put("pid", productNotice.getPid() + "");
        if(upView.isSelected()){
            request.put("buyType", TradeOrder.BUY_UP + "");
        }else{
            request.put("buyType", TradeOrder.BUY_DOWN + "");
        }
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.URL_PRODUCRTNOTICE_EDIT, request, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                if(resultCode.equals("260009")){
                    line_pn_inputtips.setVisibility(View.VISIBLE);
                    text_pn_inputtips.setVisibility(View.VISIBLE);
                    text_pn_inputtips.setText(resultMsg);
                }
                context.showCusToast(resultMsg);
                text_pn_right.setClickable(true);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response,TempObject.class);
                if (commonResponse.success) {
                    pnEditDlg.dismiss();
                    productNoticeUtil.getProductNoticeList();
                    EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_SAVE));
                }
            }
        },true);
    }

    /**
     * 删除行情提醒
     */
    private void delateProductNotice() {

        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(context);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("pid", productNotice.getPid() + "");
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.URL_PRODUCRTNOTICE_DELETE, request, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                context.showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response,TempObject.class);
                if (commonResponse.success) {
                    pnEditDlg.dismiss();
                    productNoticeUtil.getProductNoticeList();
                    EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_EDIT_DELETE));
                }
            }
        },true);

    }
}
