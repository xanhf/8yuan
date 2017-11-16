package com.trade.eight.moudle.upgradeversion;

import android.app.Dialog;
import android.os.AsyncTask;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.upgradeversion.entity.UpgradeInfo;
import com.trade.eight.tools.ConvertUtil;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/21.
 */

public class CheckUpgradeInfoTask extends AsyncTask<String, Void, CommonResponse<UpgradeInfo>> {
    BaseActivity baseActivity;
    int from = 0;
    public static final int FROM_MAIN = 0;
    public static final int FROM_ME = 1;
    Dialog dialog;

    public CheckUpgradeInfoTask(BaseActivity baseActivity, int from) {
        this.baseActivity = baseActivity;
        this.from = from;
    }
    public boolean isDlgShowing () {
        if (dialog == null)
            return false;
        return dialog.isShowing();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected CommonResponse<UpgradeInfo> doInBackground(String... params) {
        return UpdateHelper.getUpgradeInfo(baseActivity);
    }

    @Override
    protected void onPostExecute(CommonResponse<UpgradeInfo> result) {
        super.onPostExecute(result);
        if (baseActivity.isFinishing())
            return;
        if (result != null) {
            if (result.isSuccess()) {
                if (dialog == null || !dialog.isShowing())
                    dialog = UpdateHelper.showUpgradeDlg(baseActivity, result.getData());
            }
            if (!result.isSuccess()) {
                if (from == FROM_ME) {
                    baseActivity.showCusToast(ConvertUtil.NVL(result.getErrorInfo(),
                            baseActivity.getString(R.string.network_problem)));
                }
            }
        } else {
            if (from == FROM_ME) {
                baseActivity.showCusToast(baseActivity.getString(R.string.network_problem));
            }

        }
    }
}