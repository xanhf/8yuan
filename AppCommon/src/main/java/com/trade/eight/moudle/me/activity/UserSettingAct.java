package com.trade.eight.moudle.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.sharejoin.JoinVouchObj;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.entity.unifypwd.AccountCheckBindAndRegData;
import com.trade.eight.moudle.floatvideo.event.EventFloat;
import com.trade.eight.moudle.outterapp.ImageViewAttachActivity;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdFirstStepAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdResetAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdResetExchangeAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdSecondAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.trade.UnifyTradePwdHelp;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.CircleImageView;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 16/3/27.
 */
public class UserSettingAct extends BaseActivity {
    public static final String TAG = "UserSettingAct";
    UserSettingAct context = this;
    public static final String MOBCLICK_TAG = "page_UserSetting";
    // constant
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;
    CircleImageView img_person;
    TextView tv_nickname, tv_uname;

    AccountCheckBindAndRegData accountCheckBindAndRegData;

    private LinearLayout ll_unifypwd;//设置交易密码(新用户) 统一交易密码(老用户)  均在未统一之前
    private TextView text_unifypwd;
    private LinearLayout ll_change_tradepwd;// 修改交易密码 (新老用户统一之后) 老用户未统一之前(按交易所修改)
    private TextView text_change_tradepwd;
    private TextView text_change_tradepwdtips;

    private boolean isUnfiyPwd = false;// 是否统一过交易面膜
    private boolean isNewAccount = false;// 是否为新用户

    LinearLayout line_root;

    AppTitleView title_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_setting);
        initViews();
    }

    void initViews() {

        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(UserSettingAct.this);
        title_view.setAppCommTitle("账户管理");

        line_root = (LinearLayout) findViewById(R.id.line_root);

        img_person = (CircleImageView) findViewById(R.id.img_person);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_uname = (TextView) findViewById(R.id.tv_uname);

        ll_unifypwd = (LinearLayout) findViewById(R.id.ll_unifypwd);
        text_unifypwd = (TextView) findViewById(R.id.text_unifypwd);
        ll_change_tradepwd = (LinearLayout) findViewById(R.id.ll_change_tradepwd);
        text_change_tradepwd = (TextView) findViewById(R.id.text_change_tradepwd);
        text_change_tradepwdtips = (TextView) findViewById(R.id.text_change_tradepwdtips);
        // 检查用户当前交易密码
//        checkUnifyPWd();
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void myClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_user) {

            PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
            option.titleResId = R.string.set_head_image;
            option.crop = true;
            option.multiSelect = false;
            option.cropOutputImageWidth = 720;
            option.cropOutputImageHeight = 720;
            PickImageHelper.pickImage(context, PICK_AVATAR_REQUEST, option);

            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "setAvatar");
        } else if (id == R.id.ll_nick) {
            Intent intent = new Intent(context, UserInfoEditAct.class);
            startActivity(intent);

            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "setNickName");

        } else if (id == R.id.ll_pwd) {
            if (!new UserInfoDao(context).isLogin())
                return;
            Intent intent = new Intent(context, ResetPwdIndexAct.class);
            intent.putExtra("phone", new UserInfoDao(context).queryUserInfo().getMobileNum());
            startActivity(intent);

            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "findPwd");

        } else if (id == R.id.img_person) {
            UserInfoDao dao = new UserInfoDao(context);
            String avatar = dao.queryUserInfo().getAvatar();
            if (StringUtil.isEmpty(avatar)) {
                //如果还没有上传过头像 弹出选择框
                View ll_user = findViewById(R.id.ll_user);
                if (ll_user != null)
                    ll_user.performClick();
                return;
            }
//            avatar = avatar.replace("avatar/3/", "avatar/1/");
            avatar = UserInfo.getLargeAvatar(avatar);
            ImageViewAttachActivity.start(context, avatar, false);

            MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "view_Large_Avatar");

        } else if (id == R.id.btn_loginout) {

            DialogUtil.showConfirmDlg(context, "确定退出该用户？", new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {

                    HttpClientHelper.doPostOption(context,
                            AndroidAPIConfig.URL_TRADE_USER_LOGIN_OUT,
                            null,
                            null,
                            new NetCallback(context) {
                                @Override
                                public void onFailure(String resultCode, String resultMsg) {

                                }

                                @Override
                                public void onResponse(String response) {

                                }
                            },
                            false);

                    new UserService(context).loginOut();
                    finish();
                    MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "logout");
                    /*退出隐藏视频*/
                    EventFloat.postHideEvent();
                    return false;
                }
            });
        } else if (id == R.id.ll_change_tradepwd) {
            if (isUnfiyPwd) {// 统一过交易密码  去修改密码
                startActivity(new Intent(context, TradeUnifyPwdResetAct.class));
            } else {
                if (!isNewAccount) {// 老用户未统一过密码  按交易所修改
                    showPopChangeTradePwd();
                }
            }
        } else if (id == R.id.ll_unifypwd) {
            if (isNewAccount) {// 新用户 统一交易密码
                TradeUnifyPwdSecondAct.startTradeUnifyPwdSecondAct(UserSettingAct.this, true);
            } else {// 老用户 先验证交易密码
                Intent intent = new Intent(UserSettingAct.this, TradeUnifyPwdFirstStepAct.class);
                intent.putExtra("accountCheckBindAndRegData", accountCheckBindAndRegData);
                startActivity(intent);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUserValue();
    }


    void updateUserValue() {
        //设置新的用户信息
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (userInfoDao.isLogin()) {
            UserInfo userInfo = userInfoDao.queryUserInfo();
//            tv_nickname.setText(ConvertUtil.NVL(userInfo.getNickName(), userInfo.getUserName()));
//            tv_uname.setText(ConvertUtil.NVL(userInfo.getUserName(), userInfo.getMobileNum()));
            tv_nickname.setText(ConvertUtil.NVL(userInfo.getNickName(), ""));
            tv_uname.setText(ConvertUtil.NVL(userInfo.getMobileNum(), ""));

            ImageLoader.getInstance().displayImage(userInfo.getAvatar(), img_person, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.img_me_headdefault));
        } else {
            img_person.setImageResource(R.drawable.img_me_headdefault);
            tv_nickname.setText("--");
            tv_uname.setText("--");
        }
    }

    /**
     * 图片返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            //updateAvatar
            if (path != null) {
                Log.v(TAG, "path=" + path);
                new UpdateTask().execute(path);
            }
        }
    }


    class UpdateTask extends AsyncTask<String, Void, CommonResponse<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNetLoadingProgressDialog("上传中");
        }

        @Override
        protected CommonResponse<String> doInBackground(String... params) {
            try {
                File file = new File(params[0]);
                if (!file.exists())
                    return null;
                return new UserService(context).updateAvatar(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<String> response) {
            super.onPostExecute(response);
            hideNetLoadingProgressDialog();
            if (response != null) {
                if (response.isSuccess()) {
                    updateUserValue();
                    showCusToast("修改成功！");
//                    finish();
                } else {
                    showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "网络异常"));
                }
            } else {
                showCusToast("网络异常");
            }

        }
    }


    /**
     * 检查统一密码
     * 1  已经统一  只显示修改密码
     * 2  新用户    未统一  只显示设置交易密码
     * 3  老用户    未统一  显示统一交易密码以及按交易所修改密码
     */
    void checkUnifyPWd() {
        UserInfoDao dao = new UserInfoDao(UserSettingAct.this);
        if (!dao.isLogin())
            return;
        boolean isUnifyPWD = TradeConfig.getAccountIsUnifyPWD(UserSettingAct.this, dao.queryUserInfo().getUserId());
        isUnfiyPwd = isUnifyPWD;
        if (isUnifyPWD) {//本地记录已经统一
            ll_unifypwd.setVisibility(View.GONE);
            ll_change_tradepwd.setVisibility(View.VISIBLE);
            return;
        }

        //检查是否统一过交易密码 以及交易所注册情况
        new AsyncTask<Void, Void, CommonResponse<AccountCheckBindAndRegData>>() {

            @Override
            protected CommonResponse<AccountCheckBindAndRegData> doInBackground(Void... params) {
                return UnifyTradePwdHelp.checkAccountBind(UserSettingAct.this);
            }

            @Override
            protected void onPostExecute(CommonResponse<AccountCheckBindAndRegData> result) {
                super.onPostExecute(result);
                if (result != null) {
                    if (result.isSuccess()) {
                        accountCheckBindAndRegData = result.getData();
                        if (accountCheckBindAndRegData.isBind()) {// 统一过交易密码
                            isUnfiyPwd = true;
                            ll_unifypwd.setVisibility(View.GONE);
                            ll_change_tradepwd.setVisibility(View.VISIBLE);
                        } else {
                            isUnfiyPwd = false;
                            if (accountCheckBindAndRegData.isNewAccount()) {//新用户
                                isNewAccount = true;
                                ll_unifypwd.setVisibility(View.VISIBLE);
                                ll_change_tradepwd.setVisibility(View.GONE);
                                text_unifypwd.setText(R.string.ll_us_unifypwd_set);
                            } else {// 老用户
                                isNewAccount = false;
                                ll_unifypwd.setVisibility(View.VISIBLE);
                                ll_change_tradepwd.setVisibility(View.VISIBLE);
                                text_unifypwd.setText(R.string.ll_us_unifypwd);
                                text_change_tradepwdtips.setText(accountCheckBindAndRegData.getExchangePwdInfo());
                            }
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * 按交易所修改密码
     */
    void showPopChangeTradePwd() {
        View view = View.inflate(UserSettingAct.this, R.layout.layout_change_tradepwd, null);

        int w = ViewGroup.LayoutParams.MATCH_PARENT;
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(view, w, h);

        popupWindow.setFocusable(true);//影响listView的item点击
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        LinearLayout ll_change_tradepwd_gg = (LinearLayout) view.findViewById(R.id.ll_change_tradepwd_gg);
        LinearLayout ll_change_tradepwd_hg = (LinearLayout) view.findViewById(R.id.ll_change_tradepwd_hg);
        LinearLayout ll_change_tradepwd_jn = (LinearLayout) view.findViewById(R.id.ll_change_tradepwd_jn);
        TextView text_change_tradepwd_cancle = (TextView) view.findViewById(R.id.text_change_tradepwd_cancle);
        ll_change_tradepwd_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(UserSettingAct.this, TradeConfig.code_gg);
                popupWindow.dismiss();
            }
        });
        ll_change_tradepwd_hg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(UserSettingAct.this, TradeConfig.code_hg);
                popupWindow.dismiss();
            }
        });
        ll_change_tradepwd_jn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(UserSettingAct.this, TradeConfig.code_jn);
                popupWindow.dismiss();
            }
        });
        text_change_tradepwd_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        for (AccountCheckBindAndRegData.ExchangeRegInfo exchangeRegInfo : accountCheckBindAndRegData.getExchanges()) {
            if (exchangeRegInfo.isReg()) {
                switch (exchangeRegInfo.getExchangeId()) {
                    case 1:
                        ll_change_tradepwd_gg.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        ll_change_tradepwd_hg.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        ll_change_tradepwd_jn.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                switch (exchangeRegInfo.getExchangeId()) {
                    case 1:
                        ll_change_tradepwd_gg.setVisibility(View.GONE);
                        break;
                    case 2:
                        ll_change_tradepwd_hg.setVisibility(View.GONE);
                        break;
                    case 3:
                        ll_change_tradepwd_jn.setVisibility(View.GONE);
                        break;
                }
            }
        }
        popupWindow.showAtLocation(line_root, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
