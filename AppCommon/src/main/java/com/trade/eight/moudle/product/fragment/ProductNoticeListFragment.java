package com.trade.eight.moudle.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.product.ProductNoticeInfo;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.product.ProductNoticeEvent;
import com.trade.eight.moudle.product.activity.ProductNoticeActivity;
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
 * 时间：on 2017/2/8.
 * 行情提醒列表展示
 */

public class ProductNoticeListFragment extends BaseFragment implements View.OnClickListener {

    TextView text_pn_left;
    TextView text_pn_title;
    TextView text_pn_right;
    SwipeListView list_pn;
    Button btn_add_pn;
    View line_pn_empty;

    ProductNoticeInfo productNoticeInfo;
    ProductNoticeAdapter productNoticeAdapter;

    ProductNoticeActivity productNoticeActivity;

    private int leftType = -1;
    private static final int LEFT_EDIT = 1;//当前为编辑按钮 即显示为编辑
    private static final int LEFT_EDIT_CLICK = 2;// 编辑按钮点击过 即显示为取消

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_productnotice_list, null);
        productNoticeActivity = (ProductNoticeActivity) getActivity();
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        list_pn = (SwipeListView) view.findViewById(R.id.list_pn);
        productNoticeAdapter = new ProductNoticeAdapter((ProductNoticeActivity) getActivity(), 0, new ArrayList<ProductNotice>(), list_pn);
        list_pn.setAdapter(productNoticeAdapter);
        line_pn_empty = view.findViewById(R.id.line_pn_empty);
        btn_add_pn = (Button) view.findViewById(R.id.btn_add_pn);
        btn_add_pn.setOnClickListener(this);

        text_pn_left = (TextView) view.findViewById(R.id.text_pn_left);
        text_pn_title = (TextView) view.findViewById(R.id.text_pn_title);
        text_pn_right = (TextView) view.findViewById(R.id.text_pn_right);

        text_pn_left.setOnClickListener(this);
        text_pn_right.setOnClickListener(this);

        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        productNoticeInfo = (ProductNoticeInfo) bundle.getSerializable("productNoticeInfo");
        getProductNoticeList();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add_pn:
                if (productNoticeAdapter.getCount() >= productNoticeInfo.getLimitSize()) {
                    DialogUtil.showTitleAndContentDialog(getActivity(), getResources().getString(R.string.product_notice_numtitle, productNoticeInfo.getLimitSize()), getResources().getString(R.string.product_notice_numcontent), null, null);
                    return;
                }
                EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_ADD));
                break;
            case R.id.text_pn_right:
                productNoticeActivity.finish();
                break;
            case R.id.text_pn_left:
                performLeftTvClick();
                break;
        }
    }

    public void onEventMainThread(ProductNoticeEvent obj) {
        switch (obj.eventType) {
            case ProductNoticeEvent.EVENTTYPE_DELETE:
                delateProductNotice(obj.productNotice);
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
     * 控制左侧按钮显示
     */
    private void controlLeftTvDisplay() {
        switch (leftType) {
            case -1:
                text_pn_left.setVisibility(View.GONE);
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
     * 获取行情提醒列表
     */
    private void getProductNoticeList() {
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(getActivity());
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("excode", productNoticeInfo.getExcode());
        request.put("code", productNoticeInfo.getCode());

        HttpClientHelper.doPostOption((BaseActivity) getActivity(), AndroidAPIConfig.URL_PRODUCRTNOTICE_LIST, request, null, new NetCallback((BaseActivity) getActivity()) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String str) {
                CommonResponse4List<ProductNotice> response = CommonResponse4List.fromJson(str,ProductNotice.class);
                List<ProductNotice> productNoticeList = response.getData();
                if (productNoticeList != null && productNoticeList.size() > 0) {
                    list_pn.setVisibility(View.VISIBLE);
                    productNoticeAdapter.clear();
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
     * 删除行情提醒
     */
    private void delateProductNotice(final ProductNotice productNotice) {

        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(getActivity());
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("pid", productNotice.getPid() + "");

        HttpClientHelper.doPostOption((BaseActivity) getActivity(), AndroidAPIConfig.URL_PRODUCRTNOTICE_DELETE, request, null, new NetCallback((BaseActivity) getActivity()) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response,TempObject.class);
                if (commonResponse.success) {
                    productNoticeAdapter.remove(productNotice);
                    productNoticeAdapter.notifyDataSetChanged();

                    if (productNoticeAdapter.getCount() == 0) {
                        list_pn.setVisibility(View.GONE);
                        line_pn_empty.setVisibility(View.VISIBLE);
                        leftType = -1;
                    }
                    controlLeftTvDisplay();
                }
            }
        },false);
    }
}
