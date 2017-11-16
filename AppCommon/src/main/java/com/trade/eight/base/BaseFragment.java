package com.trade.eight.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trade.eight.tools.DialogUtil;

import java.text.SimpleDateFormat;

public abstract class BaseFragment extends Fragment {

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String FAIL = "FAIL";


    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void showCusToast(String msg) {
//		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        DialogUtil.toast(getActivity(), msg);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param isVisible 当前fragment 是否可见  (目前只在首页 MainActivity中使用)
     */
    public void onFragmentVisible(boolean isVisible) {
        if (!isAdded())
            return;
        if (isDetached())
            return;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // StatService.onPause(this);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // StatService.onResume(this);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

}
