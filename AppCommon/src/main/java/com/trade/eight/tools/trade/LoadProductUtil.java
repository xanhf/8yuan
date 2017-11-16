package com.trade.eight.tools.trade;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.app.ServiceException;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.baksource.BakSourceService;

import java.util.List;

/**
 * Created by fangzhu on 16/6/21.
 */
public class LoadProductUtil {
    /**
     * 交易数据没有初始化的时候 进入详情页调用
     *  if (new OptionalDao(getActivity()).queryOptionalsBySymbol(optional.getContract()) == null)
     * @param activity
     * @param callback
     */
    public static void loadTradeData (final BaseActivity activity, final String excode, final Handler.Callback callback) {
        if (activity == null)
            return;

        new AsyncTask<Void, Void, List<Optional>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected List<Optional> doInBackground(Void... params) {
                try {
                    return BakSourceService.getOptionalsByType(activity, excode, excode);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Optional> optionals) {
                super.onPostExecute(optionals);
                try {
                    if (activity.isFinishing())
                        return;
                    activity.hideNetLoadingProgressDialog();
                    if (callback != null) {
                        Message message = new Message();
                        message.obj = optionals;
                        callback.handleMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
