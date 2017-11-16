package com.trade.eight.moudle.chatroom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CustomViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList = null;
    private String[] titles = null;


    public CustomViewPagerAdapter(FragmentManager mFragmentManager, ArrayList<Fragment> fragmentList) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        // TODO Auto-generated method stub
//        if (titles != null && titles.length > 0) {
//            return titles[position];
//        } else {
//            return super.getPageTitle(position);
//        }
//
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mFragmentList.get(position).getView());
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position < mFragmentList.size()) {
            fragment = mFragmentList.get(position);
        } else {
            fragment = mFragmentList.get(0);
        }
        return fragment;

    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }
}
