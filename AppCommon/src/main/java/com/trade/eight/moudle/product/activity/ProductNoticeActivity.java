package com.trade.eight.moudle.product.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.product.ProductNoticeInfo;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.product.ProductNoticeEvent;
import com.trade.eight.moudle.product.fragment.ProductNoticeEditFragment;
import com.trade.eight.moudle.product.fragment.ProductNoticeListFragment;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Utils;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/8.
 * 行情提醒查看 添加
 */

public class ProductNoticeActivity extends BaseActivity{

    String excode = null;
    String code = null;

    ProductNoticeInfo productNoticeInfo;

    public static void startProductNoticeActivity(Context context,String excode, String code){
        Intent intent = new Intent();
        intent.setClass(context,ProductNoticeActivity.class);
        intent.putExtra("excode", excode);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = this.getWindow();
        // 防止华为等虚拟键盘占用窗口高度  遮住页面内容
        win.getDecorView().setPadding(0, 0, 0, Utils.getVirtualBarHeigh(this));
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int)getResources().getDimension(R.dimen.margin_481dp);
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        setStatusBarTintResource(R.color.transparent);
        setContentView(R.layout.layout_productnotice);
        EventBus.getDefault().register(this);
        initData();
        getProductNoticeInfo();
    }

    private void initData(){
         excode = getIntent().getStringExtra("excode");
         code = getIntent().getStringExtra("code");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    protected void replaceFragment(Fragment fragment,int containerId) {
        if (isFinishing())
            return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if(containerId==R.id.fragment_pncontainer_List){

            transaction.setCustomAnimations(R.anim.in_from_bottom_500, R.anim.out_to_bottom);
            transaction.replace(R.id.fragment_pncontainer_List, fragment);
//        }else if(containerId==R.id.fragment_pncontainer_edit){
//            transaction.setCustomAnimations(R.anim.in_from_bottom_500, R.anim.out_to_bottom);
//            transaction.replace(R.id.fragment_pncontainer_List, fragment);
//        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 获取行情提醒 列表页
     * @return
     */
    private Fragment  getPNListFrag(){
        ProductNoticeListFragment pnListFrag = new ProductNoticeListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productNoticeInfo",productNoticeInfo);
        pnListFrag.setArguments(bundle);
        return pnListFrag;
    }
    /**
     * 获取行情提醒 编辑页
     * @return
     */
    private Fragment  getPNEditFrag(ProductNotice productNotice){
        ProductNoticeEditFragment pnEditFrag = new ProductNoticeEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productNoticeInfo",productNoticeInfo);
        if(productNotice!=null){
            bundle.putSerializable("productNotice",productNotice);
        }
        pnEditFrag.setArguments(bundle);
        return pnEditFrag;
    }

    public void onEventMainThread (ProductNoticeEvent obj) {
        switch (obj.eventType){
            case ProductNoticeEvent.EVENTTYPE_ADD:// 添加提醒
                replaceFragment(getPNEditFrag(null),R.id.fragment_pncontainer_edit);
                break;
            case ProductNoticeEvent.EVENTTYPE_EDIT://编辑提醒
                replaceFragment(getPNEditFrag(obj.productNotice),R.id.fragment_pncontainer_edit);
                break;
            case ProductNoticeEvent.EVENTTYPE_SAVE://添加提醒成功
                replaceFragment(getPNListFrag(),R.id.fragment_pncontainer_List);
                break;
            case ProductNoticeEvent.EVENTTYPE_EDIT_CANCLE:// 编辑操作 取消
                replaceFragment(getPNListFrag(),R.id.fragment_pncontainer_List);
                break;
            case ProductNoticeEvent.EVENTTYPE_EDIT_DELETE://编辑操作  删除提醒
                replaceFragment(getPNListFrag(),R.id.fragment_pncontainer_List);
                break;
        }
    }

    /**
     * 获取产品行情提醒基本信息
     */
    private void getProductNoticeInfo(){
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("excode",excode);
        request.put("code",code);
        HttpClientHelper.doPostOption(this, AndroidAPIConfig.URL_PRODUCRTNOTICE_INFO, request, null, new NetCallback(this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
                ProductNoticeActivity.this.finish();
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<ProductNoticeInfo> commonResponse  = CommonResponse.fromJson(response,ProductNoticeInfo.class);
                if(commonResponse.success){
                    productNoticeInfo = commonResponse.getData();
                    if(productNoticeInfo.getStatus()==1){
                        DialogUtil.showMsgDialog(ProductNoticeActivity.this,"该产品不允许添加行情提醒","知道了");
                        ProductNoticeActivity.this.finish();
                        return;
                    }
                    replaceFragment(getPNListFrag(),R.id.fragment_pncontainer_List);
                }
            }
        },true);
    }
}
