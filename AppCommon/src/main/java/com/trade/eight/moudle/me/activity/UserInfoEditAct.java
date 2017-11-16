package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;

/**
 * Created by fangzhu on 16/3/27.
 */
public class UserInfoEditAct extends BaseActivity implements View.OnClickListener{
    public static final String TAG = "UserInfoEditAct";
    UserInfoEditAct context = this;
    Button btnCommit;
    EditText editText;
    public static final int EDIT_TYPE_NICKNAME = 1;
    public static final int EDIT_TYPE_BIRTHDAY = 2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_userinfo_edit);
        setAppCommonTitle("修改昵称");
        initViews();


    }

    void initViews () {
        editText = (EditText)findViewById(R.id.editText);
        btnCommit = (Button)findViewById(R.id.btnCommit);
        btnCommit.setOnClickListener(this);
        UserInfo userInfo = new UserInfoDao(context).queryUserInfo();
        if (userInfo == null) {
            showCusToast("请登录");
            finish();
            return;
        }
        String name = ConvertUtil.NVL(userInfo.getNickName(), ConvertUtil.NVL(userInfo.getUserName(), ConvertUtil.NVL(userInfo.getMobileNum(), "")));
        editText.setText(name);
        editText.requestFocus();
        editText.setSelectAllOnFocus(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCommit) {
            String nickName = editText.getText().toString().trim();
            if (!StringUtil.isNickName(context, nickName)) {
                return;
            }
            new UpdateTask().execute(nickName);
        }
    }

    class UpdateTask extends AsyncTask<String, Void, CommonResponse<String>> {
        String nickName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNetLoadingProgressDialog("更新中");
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected CommonResponse<String> doInBackground(String... params) {
            try {
                nickName = params[0];
                return new UserService(context).updateNickName(nickName);
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
//                    UserInfoDao dao = new UserInfoDao(context);
//                    UserInfo userInfo = dao.queryUserInfo();
//                    if (userInfo != null) {
//                        userInfo.setNickName(nickName);
//                        dao.addOrUpdate(userInfo);
//                    }
                    showCusToast("修改成功！");
                    finish();
                } else {
                    showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "网络异常"));
                }
            } else {
                showCusToast("网络异常");
            }




        }
    }
}
