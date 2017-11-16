package com.trade.eight.moudle.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.ShareTools;
import com.trade.eight.tools.Utils;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangzhu on 16/4/1.
 * 分享有礼页面
 * 暂时只分享微信朋友圈
 * 20160820 分享只送哈贵的券
 */
public class ShareAct extends BaseActivity {
    public static final String TAG = "ShareAct";
    ShareTools shareTools;
    ShareAct context;
    View rootView;
    String exchangeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        //检测登录状态
        if (!new UserInfoDao(context).isLogin()) {
            finish();
            Map<String, String> map = new HashMap<String, String>();
            Intent intent = OpenActivityUtil.initAction(context, LoginActivity.class, OpenActivityUtil.ACT_SHARE, map);
            if (intent != null)
                startActivity(intent);
            return;
        }

        setContentView(R.layout.act_share);
        shareTools = new ShareTools(this);
        setAppCommonTitle(getResources().getString(R.string.title_week_share));
        rootView = findViewById(R.id.rootView);

        //设置高度
        int minh = (int)getResources().getDimension(R.dimen.shareact_min_h);
        int screenH = Utils.getScreenH(context) - Utils.dip2px(context, 40);
        if (screenH > minh) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenH);
            LinearLayout layout = (LinearLayout)findViewById(R.id.contentLL);
            layout.setLayoutParams(layoutParams);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ShareAct.class);
        context.startActivity(intent);
    }

    public void myClick(View view) {
        if (view.getId() == R.id.btn_share) {
            if (!new UserInfoDao(context).isLogin()) {
                showCusToast("请登录");
                return;
            }
            MyAppMobclickAgent.onEvent(context, "page_share", "btn_share");

            //test just share
//            shareTools.shareToWeiXinCircle(getResources().getString(R.string.app_name), "www.umeng.com", umShareListener);

            //逻辑：1、先检查 接口是否能分享
            //2、然后再分享
            //3、分享成功之后 再调用回调成功接口
//            try {
//                doShare();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            alertDlg();
            try {
                doShare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shareTools.onActivityResult(requestCode, resultCode, data);
    }


    void alertDlg () {
        new AsyncTask<Void, Void, CommonResponse4List<Exchange>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected CommonResponse4List<Exchange> doInBackground(Void... params) {
                try {
                    Map<String, String> map = ApiConfig.getCommonMap(context);
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context,map));
                    String url = AndroidAPIConfig.URL_EXCHANGE_LIST;
                    String res = HttpClientHelper.getStringFromPost(context, url, map);
                    return CommonResponse4List.fromJson(res, Exchange.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(final CommonResponse4List<Exchange> response) {
                super.onPostExecute(response);
                hideNetLoadingProgressDialog();
                if (response == null) {
                    showCusToast(getResources().getString(R.string.network_problem));
                    return;
                }
                if (!response.isSuccess() || response.getData() == null) {
                    showCusToast(ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.network_problem)));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (Build.VERSION.SDK_INT > 11) {
                    builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
                }
                builder.setTitle("请选择交易所");
                String str[]= new String[response.getData().size()];
                for (int i = 0; i < response.getData().size(); i ++) {
                    str[i] = response.getData().get(i).getExchangeName();
                }
                builder.setItems(str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exchangeId = response.getData().get(i).getExchangeId() + "";

                        try {
                            doShare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                builder.show();
            }
        }.execute();



    }
    /**
     * 获取分享地址
     */
    public void doShare() throws Exception {
        new AsyncTask<Void, Void, CommonResponse<ShareEntity>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected CommonResponse<ShareEntity> doInBackground(Void... params) {
                try {
                    Map<String, String> paraMap = ApiConfig.getCommonMap(context);
                    paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());

                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

                    String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_GET_URL, paraMap);
                    Log.v(TAG, "result=" + result);
                    if (result == null)
                        return null;
                    CommonResponse response = CommonResponse.fromJson(result, ShareEntity.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<ShareEntity> response) {
                super.onPostExecute(response);
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        MyAppMobclickAgent.onEvent(context, "page_share_step1_share_success", new UserInfoDao(context).queryUserInfo().getUserId());

                        //分享，并设置成功后的回调
                        shareTools.shareToWeiXinCircle(R.drawable.app_icon, response.getData(), umShareListener);
                    } else {
                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "信息获取失败"));
                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    /**
     * 分享的回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(context, " 分享成功", Toast.LENGTH_SHORT).show();
            try {
                doShareCallBack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showCusToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showCusToast("分享已取消");
        }
    };

    /**
     * 分享成功之后回调接口
     */
    public void doShareCallBack() throws Exception {
        new AsyncTask<Void, Void, CommonResponse<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected CommonResponse<String> doInBackground(Void... params) {
                try {
                    Map<String, String> paraMap = ApiConfig.getCommonMap(context);
                    paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
//                    paraMap.put(TradeProduct.PARAM_EXCHANGE_ID, exchangeId);

                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

                    String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_SUCCESS, paraMap);
                    Log.v(TAG, "result=" + result);
                    if (result == null)
                        return null;
                    CommonResponse response = CommonResponse.fromJson(result, String.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<String> response) {
                super.onPostExecute(response);
                if (isFinishing())
                    return;
                if (response != null) {
                    if (response.isSuccess()) {
                        //成功统计  这里用上uid
                        try {
                            MyAppMobclickAgent.onEvent(context, "page_share_step2_getQuan_success", new UserInfoDao(context).queryUserInfo().getUserId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //成功 可以弹出提示
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "已处理成功"));
//                        Toast.makeText(context, ConvertUtil.NVL(response.getErrorInfo(), "已处理成功"), Toast.LENGTH_LONG).show();
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "分享成功"), "确定");
                    } else {
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "信息获取失败"));
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "信息获取失败"), "确定");

                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }
}
