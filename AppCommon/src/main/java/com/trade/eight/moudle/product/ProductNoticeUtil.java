package com.trade.eight.moudle.product;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.product.ProductNoticeInfo;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.product.adapter.ProductNoticeAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.view.swpilistview.SwipeListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/21.
 * 行情提醒 列表面板工具类
 */

public class ProductNoticeUtil implements View.OnClickListener {
    BaseActivity context;
    Dialog pnListDlg;
    ProductNoticeAdapter productNoticeAdapter;

    ProductNoticeInfo productNoticeInfo;

    String excode = null;
    String code = null;

    TextView text_pn_left;
    TextView text_pn_title;
    TextView text_pn_right;
    SwipeListView list_pn;
    Button btn_add_pn;
    View line_pn_empty;

    private int leftType = -1;
    private static final int LEFT_EDIT = 1;//当前为编辑按钮 即显示为编辑
    private static final int LEFT_EDIT_CLICK = 2;// 编辑按钮点击过 即显示为取消

    public ProductNoticeUtil(BaseActivity context, String excode, String code) {
        this.context = context;
        this.excode = excode;
        this.code = code;
        initListDlg();
    }

    private void initListDlg() {
        if (pnListDlg != null && pnListDlg.isShowing())
            return;
        pnListDlg = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        pnListDlg.setContentView(R.layout.layout_productnotice_list);
        Window w = pnListDlg.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) context.getResources().getDimension(R.dimen.margin_481dp);
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(R.style.dialog_trade_ani);
        pnListDlg.setCancelable(true);
        findViews();

        list_pn.setAdapter(productNoticeAdapter);
        getProductNoticeInfo();
    }

    private void findViews() {
        list_pn = (SwipeListView) pnListDlg.findViewById(R.id.list_pn);
        productNoticeAdapter = new ProductNoticeAdapter(context, 0, new ArrayList<ProductNotice>(), list_pn, this);
        list_pn.setAdapter(productNoticeAdapter);
        line_pn_empty = pnListDlg.findViewById(R.id.line_pn_empty);
        btn_add_pn = (Button) pnListDlg.findViewById(R.id.btn_add_pn);
        btn_add_pn.setOnClickListener(this);

        text_pn_left = (TextView) pnListDlg.findViewById(R.id.text_pn_left);
        text_pn_title = (TextView) pnListDlg.findViewById(R.id.text_pn_title);
        text_pn_right = (TextView) pnListDlg.findViewById(R.id.text_pn_right);

        text_pn_left.setOnClickListener(this);
        text_pn_right.setOnClickListener(this);
    }

    /**
     * 获取行情提醒列表
     */
    public void getProductNoticeList() {
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(context);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("excode", productNoticeInfo.getExcode());
        request.put("code", productNoticeInfo.getCode());
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.URL_PRODUCRTNOTICE_LIST, request, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                context.showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String str) {
                CommonResponse4List<ProductNotice> response = CommonResponse4List.fromJson(str,ProductNotice.class);
                List<ProductNotice> productNoticeList = response.getData();
                if (productNoticeList != null && productNoticeList.size() > 0) {
                    list_pn.setVisibility(View.VISIBLE);
                    productNoticeAdapter.clear();
                    productNoticeAdapter.setShowEdit(false);
                    for (ProductNotice productNotice : productNoticeList) {
                        productNoticeAdapter.add(productNotice);
                    }
                    productNoticeAdapter.notifyDataSetChanged();
                    line_pn_empty.setVisibility(View.GONE);
                    leftType = LEFT_EDIT;
                } else {
                    list_pn.setVisibility(View.GONE);
                    line_pn_empty.setVisibility(View.VISIBLE);
                    leftType = -1;
                }
                controlLeftTvDisplay();
            }
        },false);
    }

    /**
     * 控制左侧按钮显示
     */
    private void controlLeftTvDisplay() {
        switch (leftType) {
            case -1:
                text_pn_left.setVisibility(View.INVISIBLE);
                break;
            case LEFT_EDIT:
                text_pn_left.setVisibility(View.VISIBLE);
                text_pn_left.setText("编辑");
                break;
            case LEFT_EDIT_CLICK:
                text_pn_left.setText("取消");
                break;
        }

    }

    /**
     * 删除行情提醒
     */
    private void delateProductNotice(final ProductNotice productNotice) {

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
                    productNoticeAdapter.remove(productNotice);
                    productNoticeAdapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_EDIT_DELETE));
                    if (productNoticeAdapter.getCount() == 0) {
                        list_pn.setVisibility(View.GONE);
                        line_pn_empty.setVisibility(View.VISIBLE);
                        leftType = -1;
                    }
                    controlLeftTvDisplay();
                }
            }
        },true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add_pn:
                //NullPointerException check by fangzhu
                if (productNoticeInfo == null)
                    return;
                if (productNoticeAdapter.getCount() >= productNoticeInfo.getLimitSize()) {
                    DialogUtil.showTitleAndContentDialog(context, context.getResources().getString(R.string.product_notice_numtitle, productNoticeInfo.getLimitSize()), context.getResources().getString(R.string.product_notice_numcontent), null, null);
                    return;
                }
                ProductNoticeListUtil productNoticeListUtil = new ProductNoticeListUtil(context, productNoticeInfo, this);
                productNoticeListUtil.showDlg();
                break;
            case R.id.text_pn_right:
                pnListDlg.dismiss();
                break;
            case R.id.text_pn_left:
                performLeftTvClick();
                break;
        }
    }

    /**
     * 左侧按钮点击
     */
    private void performLeftTvClick() {
        switch (leftType) {
            case LEFT_EDIT:
                leftType = LEFT_EDIT_CLICK;
                productNoticeAdapter.setShowEdit(true);
                break;
            case LEFT_EDIT_CLICK:
                leftType = LEFT_EDIT;
                productNoticeAdapter.setShowEdit(false);
                break;
        }
        controlLeftTvDisplay();
    }

    /**
     * 获取产品行情提醒基本信息
     */
    private void getProductNoticeInfo() {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("excode", excode);
        request.put("code", code);
        request.put("exchangeId", "5");
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.URL_PRODUCRTNOTICE_INFO, request, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                context.showCusToast(resultMsg);
                pnListDlg.dismiss();
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<ProductNoticeInfo> commonResponse = CommonResponse.fromJson(response,ProductNoticeInfo.class);
                if (commonResponse.success) {
                    productNoticeInfo = commonResponse.getData();
                    if (productNoticeInfo == null) {
                        pnListDlg.dismiss();
                        return;
                    }
                    if (productNoticeInfo.getStatus() == 1) {
                        DialogUtil.showMsgDialog(context, "该产品不允许添加行情提醒", "知道了");
                        pnListDlg.dismiss();
                        return;
                    }
                    getProductNoticeList();
                }
            }
        },false);
    }

    public void showDlg() {
        pnListDlg.show();
    }

    public void editProductNotice(ProductNotice productNotice) {
        ProductNoticeListUtil productNoticeListUtil = new ProductNoticeListUtil(context, productNoticeInfo, this, productNotice);
        productNoticeListUtil.showDlg();
    }

    public void deleteProductNotice(ProductNotice productNotice) {
        delateProductNotice(productNotice);
    }
}
