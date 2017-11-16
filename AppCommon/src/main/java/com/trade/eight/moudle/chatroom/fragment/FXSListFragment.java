package com.trade.eight.moudle.chatroom.fragment;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.live.LiveTeacherData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.chatroom.adapter.FXSLiveTimeAdaper;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.CircleImageView;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.HashMap;


/**
 * 分析师
 */
public class FXSListFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    public static final String TAG = "FXSListFragment";

    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    private BaseActivity mContext;
    private View view;
    FXSLiveTimeAdaper fxsLiveTimeAdaper;

    LiveRoomNew liveRoomNew;

    private View headView;

    CircleImageView img_fxs_avatar;
    TextView text_fxs_name;
    TextView text_fxs_title;
    TextView text_fxs_introduction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_chatroom_fxs, null);
        liveRoomNew = (LiveRoomNew) getArguments().getSerializable("liveroomnew");
        Log.e(TAG, "onCreateView");
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (BaseActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
//            mPullRefreshListView.doPullRefreshing(true, 0);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    public void initView(View view) {
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.list_view);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullLoadEnabled(false);
        mPullRefreshListView.setPullRefreshEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();

        headView = View.inflate(mContext, R.layout.head_chatroom_fxs, null);
        img_fxs_avatar = (CircleImageView) headView.findViewById(R.id.img_fxs_avatar);
        text_fxs_title = (TextView) headView.findViewById(R.id.text_fxs_title);
        text_fxs_name = (TextView) headView.findViewById(R.id.text_fxs_name);
        text_fxs_introduction = (TextView) headView.findViewById(R.id.text_fxs_introduction);

        listView.addHeaderView(headView);
        fxsLiveTimeAdaper = new FXSLiveTimeAdaper(mContext, 0, liveRoomNew.getLiveTimeList());
        listView.setAdapter(fxsLiveTimeAdaper);
        listView.setDividerHeight(0);
        listView.setSelector(getResources().getDrawable(android.R.color.transparent));

        displayViews();
    }

    private void displayViews() {
        if (liveRoomNew.getSegmentModel() != null) {
            ImageLoader.getInstance().displayImage(liveRoomNew.getSegmentModel().getAuthorAvatar(), img_fxs_avatar, AppImageLoaderConfig.getCommonDisplayImageOptions(getActivity(), R.drawable.img_avater_loading));
            text_fxs_title.setText( ConvertUtil.NVL(liveRoomNew.getSegmentModel().getProfessionalTitle(), ""));
            text_fxs_name.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getAuthorName(), ""));
            text_fxs_introduction.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getIntroduction(), ""));
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
