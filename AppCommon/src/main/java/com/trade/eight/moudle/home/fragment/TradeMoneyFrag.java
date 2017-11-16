package com.trade.eight.moudle.home.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeOrderAndUserInfoData;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.auth.AuthUploadIdCardAct;
import com.trade.eight.moudle.auth.data.AuthDataHelp;
import com.trade.eight.moudle.auth.entity.CardAuthObj;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.activity.CashHistoryAct;
import com.trade.eight.moudle.trade.activity.CashOutAct;
import com.trade.eight.moudle.trade.activity.FXBTGCashInH5Act;
import com.trade.eight.moudle.trade.activity.TradeHistoryAct;
import com.trade.eight.moudle.trade.cashinout.CashOutCardManageAct;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.trade.TradeConfig;

/**
 * Created by Administrator
 * <p/>
 * 交易 金额
 */
public class TradeMoneyFrag extends BaseFragment implements View.OnClickListener {
    String TAG = "TradeMoneyFrag";

    private View ll_trade_order_cashin;
    private TextView text_trade_order_cashintips;
    private View ll_trade_order_cashout;
    private TextView text_trade_order_cashouttips;
    private View ll_trade_order_mangaecard;
    private View ll_trade_order_tradehistory;
    private View ll_trade_order_cashhistory;
    private static Dialog tokenDlg = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(TAG, "onSaveInstanceState != null");
            if (savedInstanceState.getBoolean("isHidden")) {
                Log.v(TAG, "onSaveInstanceState != null && isHidden == true");
                getChildFragmentManager().beginTransaction().hide(this).commit();
            }
        }
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        if (isVisible) {
//            TradeOrderAndUserInfoData tradeOrderAndUserInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).getTradeOrderAndUserInfoData();
//            if (tradeOrderAndUserInfoData != null) {
//                text_trade_order_cashintips.setText(ConvertUtil.NVL(tradeOrderAndUserInfoData.getRechargeMinAmountDesc(), ""));
//                text_trade_order_cashouttips.setText(ConvertUtil.NVL(tradeOrderAndUserInfoData.getCashOutMinAmountDesc(), ""));
//            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putBoolean("isHidden", isHidden());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frag_trade_money, null);
        initView(view);
//        EventBus.getDefault().post(new SetViewHeightEvent(Utils.dip2px(getActivity(),0)));
        return view;
    }

    void initView(View view) {
        ll_trade_order_cashin = view.findViewById(R.id.ll_trade_order_cashin);
        text_trade_order_cashintips = (TextView) view.findViewById(R.id.text_trade_order_cashintips);
        ll_trade_order_cashout = view.findViewById(R.id.ll_trade_order_cashout);
        text_trade_order_cashouttips = (TextView) view.findViewById(R.id.text_trade_order_cashouttips);
        ll_trade_order_mangaecard = view.findViewById(R.id.ll_trade_order_mangaecard);
        ll_trade_order_tradehistory = view.findViewById(R.id.ll_trade_order_tradehistory);
        ll_trade_order_cashhistory = view.findViewById(R.id.ll_trade_order_cashhistory);

        ll_trade_order_cashin.setOnClickListener(this);
        ll_trade_order_cashout.setOnClickListener(this);
        ll_trade_order_mangaecard.setOnClickListener(this);
        ll_trade_order_tradehistory.setOnClickListener(this);
        ll_trade_order_cashhistory.setOnClickListener(this);

//        TradeOrderAndUserInfoData tradeOrderAndUserInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).getTradeOrderAndUserInfoData();
//        if (tradeOrderAndUserInfoData != null) {
//            text_trade_order_cashintips.setText(ConvertUtil.NVL(tradeOrderAndUserInfoData.getRechargeMinAmountDesc(), ""));
//            text_trade_order_cashouttips.setText(ConvertUtil.NVL(tradeOrderAndUserInfoData.getCashOutMinAmountDesc(), ""));
//        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(TAG, "isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_trade_order_cashin) {
            checkAuthStatus(id);
        } else if (id == R.id.ll_trade_order_cashout) {
            checkAuthStatus(id);
        } else if (id == R.id.ll_trade_order_mangaecard) {
            checkAuthStatus(id);
        } else if (id == R.id.ll_trade_order_tradehistory) {
            TradeHistoryAct.startAct(getActivity());
        } else if (id == R.id.ll_trade_order_cashhistory) {
            CashHistoryAct.startAct(getActivity());
        }
    }

    /**
     * 检查实名认证状态
     *
     * @param viewID
     */
    private void checkAuthStatus(final int viewID) {

        UserInfoDao userInfoDao = new UserInfoDao(getActivity());
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo.getAuthStatus() == CardAuthObj.STATUS_SUCCESS) {
            if (viewID == R.id.ll_trade_order_cashin) {
                FXBTGCashInH5Act.startCashin(getActivity());
            } else if (viewID == R.id.ll_trade_order_cashout) {
                CashOutAct.startAct(getActivity());
            } else if (viewID == R.id.ll_trade_order_mangaecard) {
                CashOutCardManageAct.startAct(getActivity());
            }
        } else {
            AuthDataHelp.checkStatus((BaseActivity) getActivity(), new NetCallback((BaseActivity) getActivity()) {
                @Override
                public void onFailure(String resultCode, String resultMsg) {

                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(resultCode)) {
                        //刷新过程中token过期
                        getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //重新登录
                                        showDialog();
                                    }
                                }
                        );
                    } else {
                        showCusToast(ConvertUtil.NVL(resultMsg, getActivity().getResources().getString(R.string.network_problem)));
                    }
                }

                @Override
                public void onResponse(String response) {
                    CommonResponse<CardAuthObj> commonResponse = CommonResponse.fromJson(response,CardAuthObj.class);
                    if (commonResponse != null
                            && commonResponse.isSuccess()
                            && commonResponse.getData() != null) {
                        int authStatus = commonResponse.getData().getStatus();
                        if (authStatus == CardAuthObj.STATUS_SUCCESS) {
                            if (viewID == R.id.ll_trade_order_cashin) {
                                FXBTGCashInH5Act.startCashin(getActivity());
                            } else if (viewID == R.id.ll_trade_order_cashout) {
                                CashOutAct.startAct(getActivity());
                            } else if (viewID == R.id.ll_trade_order_mangaecard) {
                                CashOutCardManageAct.startAct(getActivity());
                            }
                        } else if (authStatus == CardAuthObj.STATUS_CHECKING) {
                            showCusToast(getActivity().getResources().getString(R.string.card_status_checking_1));
                        } else {
                            showCusToast(getActivity().getResources().getString(R.string.card_status_tips));
                            AuthUploadIdCardAct.start(getActivity());
                        }
                    }
                }
            });
        }
    }


    /**
     * dialog的处理
     */
    void showDialog() {

        if (tokenDlg != null) {
            if (tokenDlg.isShowing())
                return;
        }
        Log.e(TAG, "showDialog========");
        if (!isAdded())
            return;
        if (isDetached())
            return;

        DialogUtil.showTokenDialog((BaseActivity) getActivity(), TradeConfig.getCurrentTradeCode(getActivity()), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                if (tokenDlg != null) {
                    if (tokenDlg.isShowing())
                        return;
                }
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {

            }

            @Override
            public void onNegClick() {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//跳转到main  必须要有
                getActivity().startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
