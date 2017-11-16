package com.trade.eight.moudle.trade.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeRedPacket;
import com.trade.eight.entity.trade.TradeRedPacketDetail;
import com.trade.eight.moudle.trade.adapter.MyRedPacketAdapter;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by ocean_chy on 16/6/5.
 * 农交所红包
 */
public class TradeRedPacketAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {
    public static final String TAG = "TradeVoucherAct";

    TradeRedPacketAct context = this;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    //红包adapter
    MyRedPacketAdapter myVoucherAdapter = null;

    View headerView;

    TextView tv_animaion;
    TextView tv_redpacket_currenttotal;
    TextView tv_redpacket_total;
    TextView tv_redpacket_ValidDate;

    View emptyView;

    int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_redpacket);
        setAppCommonTitle("我的红包");
        initViews();
    }


    void initViews() {

        emptyView = findViewById(R.id.emptyView);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        headerView = View.inflate(this, R.layout.head_trade_redpacket, null);

        tv_animaion = (TextView) headerView.findViewById(R.id.tv_animaion);
        tv_redpacket_currenttotal = (TextView) headerView.findViewById(R.id.tv_redpacket_currenttotal);
        tv_redpacket_total = (TextView) headerView.findViewById(R.id.tv_redpacket_total);
        tv_redpacket_ValidDate = (TextView) headerView.findViewById(R.id.tv_redpacket_ValidDate);


        listView.addHeaderView(headerView);

        myVoucherAdapter = new MyRedPacketAdapter(context, 0, new ArrayList<TradeRedPacketDetail>());
        listView.setAdapter(myVoucherAdapter);


        findViewById(R.id.btn_des).setOnClickListener(this);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void playAnimation() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tv_animaion, "alpha", 0f, 1f);
//        alpha.setDuration(1000);//设置动画时间
//        alpha.setInterpolator(new DecelerateInterpolator());//设置动画插入器，减速
//        alpha.start();//启动动画。

        ObjectAnimator translationDown = ObjectAnimator.ofFloat(tv_animaion, "Y",
                tv_animaion.getY(), tv_animaion.getY() + 60);
//        translationUp.setInterpolator(new DecelerateInterpolator());
//        translationUp.setDuration(1000);
//        translationUp.start();

        AnimatorSet animatorSet = new AnimatorSet();//组合动画

        animatorSet.setDuration(5000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(translationDown).with(alpha);//两个动画同时开始
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv_animaion.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }

    @Override
    public void onResume() {
        super.onResume();
        mPullRefreshListView.doPullRefreshing(true, 0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_des) {
            Intent intent = new Intent(this, VoucherRuleAct.class);
            intent.putExtra("from", VoucherRuleAct.FROMREDPACKET);
            startActivity(intent);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        new GetTradeVoucherListTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page++;
        new GetTradeVoucherListTask().execute();
    }

    class GetTradeVoucherListTask extends AsyncTask<String, Void, CommonResponse<TradeRedPacket>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse<TradeRedPacket> doInBackground(String... params) {
            return TradeHelp.getRedPacketList(context, page);
        }

        @Override
        protected void onPostExecute(CommonResponse<TradeRedPacket> result) {
            // TODO Auto-generated method stub
            if (isFinishing())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();
            emptyView.setVisibility(View.GONE);
            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {

                    tv_redpacket_currenttotal.setText(result.getData().getBalance() + "");
                    tv_redpacket_total.setText(result.getData().getAmount() + "");
                    tv_redpacket_ValidDate.setText("有效期至: " + result.getData().getCouponValidDate());

                    //没有代金券
                    if (page == 1)
                        myVoucherAdapter.clear();

                    if (result.getData() != null && (result.getData().getFlows() != null && result.getData().getFlows().size() > 0)) {
                        myVoucherAdapter.setItems(result.getData().getFlows(), false);
                        if (!TextUtils.isEmpty(result.getData().getLastIn())) {
                            tv_animaion.setVisibility(View.VISIBLE);
                            playAnimation();
                        }
                    } else if (page > 1 && (result.getData() == null || result.getData().getFlows().size() == 0)) {
                        showCusToast("没有更多数据");
                    }

                    if (page == 1 && (result.getData() == null || result.getData().getFlows() == null || result.getData().getFlows().size() == 0)) {
                        //没有数据
                        myVoucherAdapter.setItems(new ArrayList<TradeRedPacketDetail>(),false);
                        tv_redpacket_currenttotal.setText("0");
                        tv_redpacket_total.setText("0");
                        tv_redpacket_ValidDate.setText("有效期至: 无");
                    } else {

                    }

                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
                            showCusToast(result.getErrorInfo());
                        }
                        //重新登录
                        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context), null, new DialogUtil.AuthTokenCallBack() {
                            @Override
                            public void onPostClick(Object obj) {

                            }

                            @Override
                            public void onNegClick() {
                                finish();
                            }
                        });
                        return;
                    }
                }
                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);

            } else {
                showCusToast("网络连接失败！");
            }

        }
    }
}
