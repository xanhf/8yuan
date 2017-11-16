package com.trade.eight.moudle.home.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.home.HomeCalendar;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.tools.ConvertUtil;

/**
 * Created by Administrator on 2015/8/17.
 */
public class HomeCalendarDetailAct extends BaseActivity {
    HomeCalendarDetailAct context = this;
    String TAG = "HomeCalendarDetailAct";
    View loadingView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home_calendar_detail);
        initView();
        getData();

    }
    void getData () {
        new AsyncTask<Void, Void,  CommonResponse<HomeCalendar>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingView != null)
                    loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            protected  CommonResponse<HomeCalendar> doInBackground(Void... params) {
                try {
                    String date = getIntent().getStringExtra("date");
                    String id = getIntent().getStringExtra("id");

                    if (date == null || id == null)
                        return null;

                    String url = AndroidAPIConfig.URL_CALENDER_DETAIL.replace("{date}", date).replace("{id}", id);
                    String res = HttpClientHelper.getStringFromGet(context, url);
                    return  CommonResponse.fromJson(res, HomeCalendar.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute( CommonResponse<HomeCalendar> commonResponse) {
                super.onPostExecute(commonResponse);
                if (loadingView != null)
                    loadingView.setVisibility(View.GONE);
                if (commonResponse != null && commonResponse.isSuccess() && commonResponse.getData() != null)
                    setValue(commonResponse.getData());
                else if (commonResponse != null && commonResponse.getErrorInfo() != null)
                    showCusToast(commonResponse.getErrorInfo());
                else
                    showCusToast("数据获取失败！");
            }


        }.execute();
    }

    void initView() {
        setAppCommonTitle("数据解读");
        loadingView = findViewById(R.id.loadingView);
    }

    void setValue(HomeCalendar calendar) {
        if (calendar == null)
            return;
        TextView ectitle_tv = (TextView) findViewById(R.id.ectitle_tv);
        if (ectitle_tv != null)
            ectitle_tv.setText(ConvertUtil.NVL(calendar.getEcTitle(), ""));
        TextView ecOrg_tv = (TextView) findViewById(R.id.ecOrg_tv);
        if (ecOrg_tv != null)
            ecOrg_tv.setText(ConvertUtil.NVL(calendar.getEcOrg(), ""));
        TextView ecNextTime_tv = (TextView) findViewById(R.id.ecNextTime_tv);
        if (ecNextTime_tv != null)
            ecNextTime_tv.setText(ConvertUtil.NVL(calendar.getEcNextTime(), ""));
        TextView ecMethod_tv = (TextView) findViewById(R.id.ecMethod_tv);
        if (ecMethod_tv != null)
            ecMethod_tv.setText(ConvertUtil.NVL(calendar.getEcMethod(), ""));
        TextView ecEffect_tv = (TextView) findViewById(R.id.ecEffect_tv);
        if (ecEffect_tv != null)
            ecEffect_tv.setText(ConvertUtil.NVL(calendar.getEcEffect(), ""));
        TextView ecFrequency_tv = (TextView) findViewById(R.id.ecFrequency_tv);
        if (ecFrequency_tv != null)
            ecFrequency_tv.setText(ConvertUtil.NVL(calendar.getEcFrequency(), ""));
        TextView ecMeaning_tv = (TextView) findViewById(R.id.ecMeaning_tv);
        if (ecMeaning_tv != null)
            ecMeaning_tv.setText(ConvertUtil.NVL(calendar.getEcMeaning(), ""));
        TextView ecReason_tv = (TextView) findViewById(R.id.ecReason_tv);
        if (ecReason_tv != null)
            ecReason_tv.setText(ConvertUtil.NVL(calendar.getEcReason(), ""));
    }

}
