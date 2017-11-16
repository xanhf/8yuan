package com.trade.eight.moudle.trade.cashinout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.BanksBranchData;
import com.trade.eight.entity.trade.City;
import com.trade.eight.entity.trade.Province;
import com.trade.eight.moudle.trade.activity.BankBranchAct;
import com.trade.eight.moudle.trade.activity.BankListAct;
import com.trade.eight.moudle.trade.activity.CityAct;
import com.trade.eight.moudle.trade.activity.ProvinceAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 提现绑定银行卡
 */

public class CashOutBindCardAct extends BaseActivity implements View.OnClickListener {
    CashOutBindCardAct context = this;
    public static final int CODE_REQ_CHOOSE_BANK = 10;
    public static final int CODE_RES_GET_BANK = 11;

    public static final int CODE_REQ_CHOOSE_PROVICE = 12;
    public static final int CODE_RES_GET_PROVICE = 13;

    public static final int CODE_REQ_CHOOSE_CITY = 14;
    public static final int CODE_RES_GET_CITY = 15;

    public static final int CODE_REQ_CHOOSE_BANK_BRANCH = 16;
    public static final int CODE_RES_GET_BANK_BRANCH = 17;


    View bankMore;
    TextView ed_bank;
    View bankprovince;
    TextView ed_province;
    View bankcity;
    TextView ed_city;
    View bankbranch;
    TextView ed_branch;
    EditText ed_banknum;

    private Banks banks;
    private City city;
    Province province;
    BanksBranchData banksBranchData;

    public static void start(Context context) {
        Intent intent = new Intent(context, CashOutBindCardAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cashout_bind_card);
        setAppCommonTitle(getResources().getString(R.string.trade_order_bindcard_title));
        bankMore = findViewById(R.id.bankMore);
        ed_bank = (TextView) findViewById(R.id.ed_bank);
        bankprovince = findViewById(R.id.bankprovince);
        ed_province = (TextView) findViewById(R.id.ed_province);
        bankcity = findViewById(R.id.bankcity);
        ed_city = (TextView) findViewById(R.id.ed_city);
        bankbranch = findViewById(R.id.bankbranch);
        ed_branch = (TextView) findViewById(R.id.ed_branch);
        ed_banknum = (EditText) findViewById(R.id.ed_banknum);
        ed_banknum.addTextChangedListener(new TextWatcher() {

            private int oldLength = 0;
            private boolean isChange = true;
            private int curLength = 0;
            private int emptyNumB = 0;
            private int emptyNumA = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldLength = s.length();
                emptyNumB = 0;
                for (int i = 0; i < s.toString().length(); i++) {
                    if (s.charAt(i) == ' ') emptyNumB++;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curLength = s.length();
                if (curLength == oldLength || curLength <= 3) {
                    isChange = false;
                } else {
                    isChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChange) {
                    int selectIndex = ed_banknum.getSelectionEnd();//获取光标位置
                    String content = s.toString().replaceAll(" ", "");
                    StringBuffer sb = new StringBuffer(content);
                    //遍历加空格
                    int index = 1;
                    emptyNumA = 0;
                    for (int i = 0; i < content.length(); i++) {
                        if ((i + 1) % 4 == 0) {
                            sb.insert(i + index, " ");
                            index++;
                            emptyNumA++;
                        }
                    }
                    String result = sb.toString();
                    if (result.endsWith(" ")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    ed_banknum.setText(result);
                    if (emptyNumA > emptyNumB)
                        selectIndex = selectIndex + (emptyNumA - emptyNumB);
                    //处理光标位置
                    if (selectIndex > result.length() || selectIndex + 1 == result.length()) {
                        selectIndex = result.length();
                    } else if (selectIndex < 0) {
                        selectIndex = 0;
                    }
                    ed_banknum.setSelection(selectIndex);
                    isChange = false;
                }

            }
        });

        bankMore.setOnClickListener(this);
        bankprovince.setOnClickListener(this);
        bankcity.setOnClickListener(this);
        bankbranch.setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    void submit() {
        final String bankName = ed_bank.getText().toString();
        final String province = ed_province.getText().toString();
        final String city = ed_city.getText().toString();
        final String branch = ed_branch.getText().toString();
        final String idCard = getCardNum();

        if (StringUtil.isEmpty(bankName)) {
            showCusToast(getResources().getString(R.string.cash_out_input_bank));
            return;
        }
        if (StringUtil.isEmpty(province)) {
            showCusToast(getResources().getString(R.string.cash_out_input_provice));
            return;
        }
        if (StringUtil.isEmpty(city)) {
            showCusToast(getResources().getString(R.string.cash_out_input_city));
            return;
        }
        if (StringUtil.isEmpty(branch)) {
            showCusToast(getResources().getString(R.string.cash_out_input_branch));
            return;
        }
        if (StringUtil.isEmpty(idCard)) {
            showCusToast(getResources().getString(R.string.cash_out_input_card));
            return;
        }
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("bankDepositId", banksBranchData.getBankDepositId());
        map.put("bankNo", idCard);
        map.put("province", province);
        map.put("city", city);
        map.put("bankName", bankName);
        map.put("bankBranch", branch);

        HttpClientHelper.doPostOption(CashOutBindCardAct.this,
                AndroidAPIConfig.URL_CASHOUT_BINDCARD,
                map,
                null,
                new NetCallback(CashOutBindCardAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        showCusToast("绑卡成功");
                        doMyfinish();
                    }
                }, true);
    }

    Dialog tokenDlg = null;

    void showTokenDlg() {
        //网页充值的时候判断是否token可用
        if (tokenDlg != null && tokenDlg.isShowing())
            return;
        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {

            }

            @Override
            public void onNegClick() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bankMore) {
            Intent intent = new Intent(context, BankListAct.class)
                    .putExtra("bankName", ed_bank.getText().toString());
            startActivityForResult(intent, CODE_REQ_CHOOSE_BANK);

        } else if (id == R.id.bankprovince) {
            Intent intent = new Intent(context, ProvinceAct.class);
            startActivityForResult(intent, CODE_REQ_CHOOSE_PROVICE);
        } else if (id == R.id.bankcity) {
            if (province == null) {
                showCusToast(getResources().getString(R.string.cash_out_input_provice));
                return;
            }
            Intent intent = new Intent(context, CityAct.class);
            intent.putExtra("object", province);
            //获取到省份之后，选择城市
            startActivityForResult(intent, CODE_REQ_CHOOSE_CITY);
        } else if (id == R.id.bankbranch) {
            if (banks == null) {
                showCusToast(getResources().getString(R.string.cash_out_input_bank));
                return;
            }
            if (city == null) {
                showCusToast(getResources().getString(R.string.cash_out_input_city));
                return;
            }
            Intent intent = new Intent(context, BankBranchAct.class);
            intent.putExtra("banks", banks);
            intent.putExtra("city", city);
            startActivityForResult(intent, CashOutBindCardAct.CODE_REQ_CHOOSE_BANK_BRANCH);
        } else if (id == R.id.btn_submit) {
            submit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQ_CHOOSE_BANK && resultCode == CODE_RES_GET_BANK) {
            //设置选中的银行卡
            if (data == null)
                return;
            banks = (Banks) data.getSerializableExtra("bank");
            if (banks == null)
                return;
            String bankName = banks.getName();
            if (!StringUtil.isEmpty(bankName)) {
                ed_bank.setText(bankName);
            }
        } else if (requestCode == CODE_REQ_CHOOSE_PROVICE && resultCode == CODE_RES_GET_PROVICE) {
            //先跳转到选择省份的act，然后省份的act  finish，返回再start city 的act
            //设置选中的省份
            if (data == null)
                return;
            province = (Province) data.getSerializableExtra("object");
            if (province == null)
                return;
            Intent intent = new Intent(context, CityAct.class);
            intent.putExtra("object", province);
            //获取到省份之后，选择城市
            startActivityForResult(intent, CODE_REQ_CHOOSE_CITY);
            ed_province.setText(province.getName());
        } else if (requestCode == CODE_REQ_CHOOSE_CITY && resultCode == CODE_RES_GET_CITY) {
            //先跳转到选择省份的act，然后省份的act  finish，返回再start city 的act
            //设置选中的省份
            if (data == null)
                return;
            city = (City) data.getSerializableExtra("object");
            if (city == null)
                return;
            if (city != null) {
                ed_city.setText(city.getName());
            }
        } else if (requestCode == CODE_REQ_CHOOSE_BANK_BRANCH && resultCode == CODE_RES_GET_BANK_BRANCH) {
            //先跳转到选择省份的act，然后省份的act  finish，返回再start city 的act
            //设置选中的省份
            if (data == null)
                return;
            banksBranchData = (BanksBranchData) data.getSerializableExtra("object");
            if (banksBranchData == null)
                return;
            if (banksBranchData != null) {
                ed_branch.setText(banksBranchData.getName());
            }
        }

    }

    /**
     * 拦截此方法点击空白隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取银行卡号  去空格
     * @return
     */
    private String getCardNum() {
        String input = ed_banknum.getText().toString();
        if (!TextUtils.isEmpty(input)) {
            input = input.trim().replaceAll(" ", "");
        }
        return input;
    }
}
