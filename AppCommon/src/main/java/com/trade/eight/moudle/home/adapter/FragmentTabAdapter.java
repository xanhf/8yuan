package com.trade.eight.moudle.home.adapter;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.trade.eight.base.BaseFragment;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.tools.Log;

import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/11/2.
 */
public class FragmentTabAdapter implements  RadioGroup.OnCheckedChangeListener{
    public static final String TAG = "FragmentTabAdapter";
    private List<Fragment> fragments;
    private RadioGroup rgs;
    private FragmentActivity fragmentActivity;
    private int fragmentContentId;

    private int currentTab;

    private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener;

    private String currentTag = MainActivity.HOME;
    public static HashMap<String, Integer> tabTag = new HashMap<String, Integer>();

    static {
        tabTag.put(MainActivity.HOME, 0);
        tabTag.put(MainActivity.MARKET, 1);
        tabTag.put(MainActivity.WEIPAN, 2);
        tabTag.put(MainActivity.LIVING, 3);
        tabTag.put(MainActivity.ME, 4);
    }

    public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs,int index) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;

        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(index));
        ft.commit();
        rgs.setOnCheckedChangeListener(this);
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(String currentTag) {
        this.currentTag = currentTag;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        for(int i = 0; i < rgs.getChildCount(); i++){
            if(rgs.getChildAt(i).getId() == checkedId){
                Fragment fragment = fragments.get(i);
                FragmentTransaction ft = obtainFragmentTransaction(i);
                ft.hide(getCurrentFragment()).commit();
                Log.v(TAG, "isadd="+fragment.isAdded());
                if(fragment.isAdded()){
                    fragment.onResume();
                }else{
                    //添加并且强制关联activity
                    fragmentActivity.getSupportFragmentManager().beginTransaction()
                            .add(fragmentContentId, fragment).attach(fragment).commit();
                }
                showTab(i);

                if(null != onRgsExtraCheckedChangedListener){
                    onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup, checkedId, i);
                }
            }
        }
    }


    private void showTab(int idx){
        for(int i = 0; i < fragments.size(); i++){
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);

            if(idx == i){
                ft.show(fragment);
                ((BaseFragment)fragment).onFragmentVisible(true);
            }else{
                ft.hide(fragment);
                ((BaseFragment)fragment).onFragmentVisible(false);

            }
            ft.commit();
        }
        currentTab = idx;
    }

    public void setCurrentTabByTag(String tag){
        ((RadioButton) rgs.getChildAt(tabTag.get(tag))).setChecked(true);
        currentTag = tag;
    }

    private FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
       /* if(index > currentTab){
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        }else{
            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }*/
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment(){
        return fragments.get(currentTab);
    }

    public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
        return onRgsExtraCheckedChangedListener;
    }

    public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
        this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
    }


    public static class OnRgsExtraCheckedChangedListener{
        public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index){

        }
    }
}
