package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.trade.TradeRechargeTypeAndMoneyData;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.moudle.trade.fragment.CashInChooseMoney;
import com.trade.eight.moudle.trade.fragment.CashInChooseMoneyFrag;
import com.trade.eight.moudle.trade.fragment.CashInChoosePlatformFrag;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.OpenActivityUtil;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by developer on 16/1/18.
 * 充值的 act
 * 小额充值暂时不需要
 */
public class CashInAct extends BaseActivity implements CashInChooseMoney {

    private String TAG = "CashInAct";

    CashInAct context = this;

    CashInChoosePlatformFrag cashInChoosePlatformFrag;


    private int state = CASHIN_CHOOSEMONEYFRAG;
    public static final int CASHIN_CHOOSEMONEYFRAG = 0;
    public static final int CASHIN_CHOOSEPLADFORMFRAG = 1;

    private String choosedMoney;



    public static void start(Context context) {
        Intent intent = new Intent(context, CashInAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cashin);

        //检测登录状态
        if (!new UserInfoDao(context).isLogin()) {
            finish();
            Map<String, String> map = new HashMap<String, String>();
            Intent intent = OpenActivityUtil.initAction(context, LoginActivity.class, OpenActivityUtil.ACT_CASHIN, map);
            if (intent != null)
                startActivity(intent);
            return;
        }
        EventBus.getDefault().register(CashInAct.this);
        setAppCommonTitle("充值");


//        WebActivity.start(context,"充值","\"https://statecheck.swiftpass.cn/pay/wappay?token_id=5350b597541455bc8d220ed75cf236c3&service=pay.weixin.wappayv2&showwxtitle=1");
        chooseFragment(new CashInChooseMoneyFrag());
    }

    private void chooseFragment(BaseFragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.line_fragcontanier, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void chooseMoney(String money,TradeRechargeTypeAndMoneyData tradeRechargeTypeAndMoneyData) {
        state  = CASHIN_CHOOSEPLADFORMFRAG;
        choosedMoney = money;
        cashInChoosePlatformFrag = new CashInChoosePlatformFrag();
        Bundle bundle = new Bundle();
        bundle.putString("money",choosedMoney);
        bundle.putSerializable("rechargeType",tradeRechargeTypeAndMoneyData);
        cashInChoosePlatformFrag.setArguments(bundle);
        chooseFragment(cashInChoosePlatformFrag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(CashInEvent event) {
        if(event.isSuccess()){
            doMyfinish();
        }else{
            DialogUtil.showMsgDialog(context, ConvertUtil.NVL(event.getErrorMessage(), "提交失败"), "确定");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cashInChoosePlatformFrag.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
}
