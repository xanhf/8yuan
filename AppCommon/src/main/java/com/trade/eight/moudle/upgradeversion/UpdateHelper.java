package com.trade.eight.moudle.upgradeversion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.upgradeversion.entity.UpgradeInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/20.
 */

public class UpdateHelper {
    public static final String TAG = "UpgradeService";
    Context context;

    public UpdateHelper(Context context) {
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public static CommonResponse<UpgradeInfo> getUpgradeInfo(Context context) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("version", AppSetting.getInstance(context).getAppVersionCode() + "");
        map = ApiConfig.getParamMap(context, map);
        try {
//            String result = submitPostData(map, AndroidAPIConfig.URL_UPGRADEINFO);
            String result = HttpClientHelper.getStringFromGet(context, AndroidAPIConfig.URL_UPGRADEINFO, map);
            CommonResponse<UpgradeInfo> response = CommonResponse.fromJson(result, UpgradeInfo.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 版本更新 dialog
     *
     * @param context
     * @param upgradeInfo
     */
    public static Dialog showUpgradeDlg(final Activity context, final UpgradeInfo upgradeInfo) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_upgradeinfo);
        dialog.setCancelable(true);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView tv_upgradeversion = (TextView) dialog.findViewById(R.id.tv_upgradeversion);
        tv_upgradeversion.setText(context.getResources().getString(R.string.upgrade_version, upgradeInfo.getVersion()));

        TextView tv_upgradecontent = (TextView) dialog.findViewById(R.id.tv_upgradecontent);
        tv_upgradecontent.setText(upgradeInfo.getContent());

        Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra("upgradeInfo", upgradeInfo);
                context.startService(intent);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

}
