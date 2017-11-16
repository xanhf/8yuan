package com.trade.eight.moudle.home.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.config.OnLineHelper;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RoomData;
import com.trade.eight.entity.home.HomePagerItem;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.chatroom.IndexLiveItemClickEvent;
import com.trade.eight.moudle.chatroom.activity.ChatRoomActivity;
import com.trade.eight.moudle.home.adapter.IndexLiveV4Adapter;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.AutoScrollViewPager;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * 直播房间list
 * 云信直播室的逻辑 必须1、先登录云信账号，2、然后再进入云信直播室；两步操作
 * 20160716 修改逻辑为：
 * 1、先取用户信息判断是否已经注册过云信 就是判断是否输入过昵称
 * 2、输入过昵称，直接进入直播室，云信直播室的登录放在里面登录
 * 3、没有输入过昵称，弹框输入昵称，成功后直接进直播室，云信直播室的登录放在里面登录
 */
public class IndexLiveFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ExpandableListView> {
    public static final String TAG = "IndexLiveFragment";
    private static final int LIMIT = 100;
    private RelativeLayout rel_titlebar;
    PullToRefreshExpandListView mPullRefreshListView;
    private ExpandableListView listView;
    private IndexLiveV4Adapter callListAdapter;
    private Context mContext;
    private int mCurrentPage = 1;
    private boolean isClear = true;
    View view = null;

    View headerView = null;
    AutoScrollViewPager autoViewPager = null;
    private ArrayList<View> dotViewList = new ArrayList<View>();
    View currentDotView = null;
    List<HomePagerItem> homePagerItemList = null;

    boolean isVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(TAG, "onSaveInstanceState != null");
            if (savedInstanceState.getBoolean("isHidden")) {
                Log.v(TAG, "onSaveInstanceState != null && isHidden == true");
                getFragmentManager().beginTransaction().hide(this).commit();
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putBoolean("isHidden", isHidden());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_index_live, null);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    /**
     * 进入直播室去
     */
    void enterRoom() {
//        "4562098" test room
//        String roomId = "4562098";
//        int sendPicStatus = LiveRoomNew.sendPicStatus_ENABLE;
//        item.setChatRoomId(roomId);
//        item.setSendPicStatus(sendPicStatus);
//        String roomId = item.getChatRoomId();
//        int sendPicStatus = item.getSendPicStatus();
//        ChatRoomActivity.start(getActivity(), item.getChannelStatus(), roomId, item.getChannelName(), item.getRtmpDownstreamAddress(), item.getChannelId(), sendPicStatus);

        ChatRoomActivity.start(getActivity(), item);

    }

    Dialog dialog = null;

    void hideDialog() {
        if (dialog != null)
            dialog.dismiss();
    }


    /**
     * get room data
     * <p>
     * 如果房间信息返回 需要输入 昵称的错误 就需要注册一下云信
     */
    class GetDataTask extends AsyncTask<String, Void, CommonResponse<RoomData>> {
        boolean isCallBack;

        public GetDataTask(boolean isCallBack) {
            this.isCallBack = isCallBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getLoadingDlg(getActivity(), null);
            dialog.show();
            try {
                //必须先判断是否登录了云信，才能执行注销操作，不然云信会出错
                if (NIMClient.getStatus() == StatusCode.LOGINED) {
                    //已经登录了云信了
                    if (new UserInfoDao(getActivity()).isLogin()) {
                        String uid = new UserInfoDao(getActivity()).queryUserInfo().getUserId();
                        if (!OnLineHelper.isSameAccount(getActivity(), uid)) {
                            //认为是和客服聊天的云信id登录
                            OnLineHelper.logoutCurrentAccount(getActivity());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected CommonResponse<RoomData> doInBackground(String... params) {
            try {
                if (!new UserInfoDao(getActivity()).isLogin()) {
                    return null;
                }
                Map<String, String> stringMap = ApiConfig.getCommonMap(getActivity());
                stringMap.put(UserInfo.UID, new UserInfoDao(getActivity()).queryUserInfo().getUserId());
                stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(getActivity(), stringMap));
                //这里一定不要使用get请求，get请求有时候出现服务端验证失败，可能是空格转义的问题
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_CHATROOM_ROOMID, stringMap);
                if (str != null) {
                    return CommonResponse.fromJson(str, RoomData.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<RoomData> res) {
            super.onPostExecute(res);
            if (isDetached())
                return;
            if (!isAdded())
                return;
            hideDialog();
            if (res != null) {
                //已经注册过
                if (res.isSuccess()) {
                    RoomData data = res.getData();
                    if (data == null)
                        return;
                    //保存token
                    UserInfoDao dao = new UserInfoDao(getActivity());
                    UserInfo userInfo = dao.queryUserInfo();
                    if (userInfo != null) {
                        try {
                            userInfo.setAccId(AESUtil.decrypt(data.getAccId()));
                            userInfo.setToken_IM(AESUtil.decrypt(data.getToken()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dao.addOrUpdate(userInfo);
                    }
//                    if (isCallBack)
//                        login(data);
                    enterRoom();
                } else if (ApiConfig.ERROR_CODE_NOT_REG.equals(res.getErrorCode())) {
                    //没有注册过，注册云信 ，同时返回房间信息和token
                    //客户端弹出输入昵称dialog 输入昵称之后再 进入房间
                    showDialogInput(getActivity(), false);


                } else {
                    showCusToast(ConvertUtil.NVL(res.getErrorInfo(), "信息获取失败"));
                }

            } else {
                showCusToast("信息获取失败");

            }

        }
    }

    /**
     * 输入昵称
     *
     * @param activity
     * @param isCallBack 是否跳转
     * @return
     */
    public Dialog showDialogInput(final Activity activity, final boolean isCallBack) {
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_input_dialog);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        params.width = (int) (screenWidth * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nikName = editText.getText().toString();
                if (!StringUtil.isNickName(getActivity(), nikName)) {
                    return;
                }

                dialog.dismiss();
                new RegTask().execute(nikName);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    class RegTask extends AsyncTask<String, Void, CommonResponse<RoomData>> {
        //        boolean isCallBack;
//
//        public RegTask(boolean tag) {
//            isCallBack = tag;
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getLoadingDlg(getActivity(), null);
            dialog.show();
        }

        String userNameNew;

        @Override
        protected CommonResponse<RoomData> doInBackground(String... params) {
            try {
                if (!new UserInfoDao(getActivity()).isLogin()) {
                    return null;
                }
                UserInfo userInfo = new UserInfoDao(getActivity()).queryUserInfo();
                Map<String, String> stringMap = ApiConfig.getCommonMap(getActivity());
                stringMap.put(UserInfo.UID, userInfo.getUserId());
//                stringMap.put("name", ConvertUtil.NVL(userInfo.getNickName(), ConvertUtil.NVL(userInfo.getUserName(), userInfo.getMobileNum())));
                userNameNew = params[0];
                stringMap.put("name", userNameNew);

                stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(getActivity(), stringMap));
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_CHATROOM_UPDATE_NAME, stringMap);
                if (str != null) {
                    return CommonResponse.fromJson(str, RoomData.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<RoomData> res) {
            super.onPostExecute(res);
            if (isDetached())
                return;
            if (!isAdded())
                return;
            hideDialog();
            if (res != null) {
                //成功
                if (res.isSuccess()) {
                    RoomData data = res.getData();
                    if (data == null)
                        return;
                    //保存token
                    UserInfoDao dao = new UserInfoDao(getActivity());
                    UserInfo userInfo = dao.queryUserInfo();
                    if (userInfo != null) {
                        try {
                            userInfo.setAccId(AESUtil.decrypt(data.getAccId()));
                            userInfo.setToken_IM(AESUtil.decrypt(data.getToken()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //注册成功需要更新本地用户信息
                        userInfo.setUserName(userNameNew);
                        dao.addOrUpdate(userInfo);
                    }
                    enterRoom();

                } else {
                    showCusToast(ConvertUtil.NVL(res.getErrorInfo(), "信息获取失败"));
                }

            } else {
                showCusToast("信息获取失败");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");


    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        this.isVisible  = isVisible;
        if (mPullRefreshListView == null)
            return;
        if (isVisible) {
            onPullDownToRefresh();
        }
    }

    LiveRoomNew item;

    public void initView(View view) {
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(getResources().getString(R.string.tab_main_live));
        }

        View btn_help = view.findViewById(R.id.btn_help);
        if (btn_help != null) {
            btn_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_LIVE, "btn_help");
                    OnLineHelper.getInstance().startP2p((BaseActivity) getActivity());
                }
            });
        }

        View btn_plan = view.findViewById(R.id.btn_plan);
        if (btn_plan != null) {
            btn_plan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_LIVE, "在线客服");
                    OnLineHelper.getInstance().startP2p((BaseActivity) getActivity());

                }
            });
        }

        rel_titlebar = (RelativeLayout) view.findViewById(R.id.rel_titlebar);
        mPullRefreshListView = (PullToRefreshExpandListView) view
                .findViewById(R.id.list_view);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullLoadEnabled(false);
        mPullRefreshListView.setPullRefreshEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        //去掉group点击事件
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //group不允许点击
                return true;
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int groupPosition, final int childPosition, long l) {
                return true;
            }
        });
        //去掉下划线
        listView.setGroupIndicator(null);


        headerView = View.inflate(getActivity(), R.layout.index_live_head, null);
        autoViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.autoViewPager);
        listView.addHeaderView(headerView);

        callListAdapter = new IndexLiveV4Adapter((BaseActivity) getActivity());
        listView.setAdapter(callListAdapter);
        listView.setDividerHeight(0);
        listView.setSelector(getResources().getDrawable(android.R.color.transparent));
        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
        mPullRefreshListView.doPullRefreshing(false, 0);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //必须登录
                if (!new UserInfoDao(getActivity()).isLogin()) {
                    DialogUtil.showLoginConfirmDlg(getActivity(), "直播需要登录后才能观看", true, new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            return false;
                        }
                    });
                    return;
                }
                //首先获取数据接口 检查是否为第一次 进直播，需要输入昵称
                item = (LiveRoomNew) parent.getAdapter().getItem(position);
                if (item.getChannelStatus() == LiveRoomNew.STATUS_ON) {

                } else {

                }
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_LIVE, item.getChannelName());

                //fragment 不会销毁
                //按下就去获取信息，不要先获取，之后缓存用户信息，
//                dialog = DialogMaker.showProgressDialog(getActivity(), "加载中");

                new GetDataTask(true).execute();
            }
        });

//        view.findViewById(R.id.btn_plan).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MyAppMobclickAgent.onEvent(getActivity(), "page_index_live", "liveTimePlan");
//                WebActivity.start(getActivity(), getResources().getString(R.string.live_plan), AndroidAPIConfig.URL_LIVE_PLAN);
//            }
//        });
    }

    void onPullDownToRefresh() {
        isClear = true;
        new GetDataListTask().execute("");
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        onPullDownToRefresh();
    }


    private List<String> groupList = new ArrayList<>();
    private HashMap<String, List<LiveRoomNew>> childMap = new HashMap<>();

    // 网络访问成功
    private void onMyExecute(List<LiveRoomNew> liveRoomNews) {
        if (isClear) {
//            groupList.clear();
//            childMap.clear();
            groupList = new ArrayList<>();
            childMap = new HashMap<>();
        }
        for (int i = 0; i < liveRoomNews.size(); i++) {
            LiveRoomNew liveRoomNew = liveRoomNews.get(i);
            // 只展示没有隐藏 视频直播室 以及免费直播室
            if (liveRoomNew.getHidden() == LiveRoomNew.ISHIDDEN_NO && liveRoomNew.getIsPay() == LiveRoomNew.ISPAY_NO && liveRoomNew.getChannelType() == LiveRoomNew.CHANNELTYPE_VIDEO) {
                if (liveRoomNew.getChannelStatus() != LiveRoomNew.CT_STATUS_PALY) {//休息中
                    if(childMap.containsKey("即将开始")){

                    }else{
                        childMap.put("即将开始",new ArrayList<LiveRoomNew>());
                    }
                    childMap.get("即将开始").add(liveRoomNew);
                } else {
                    if(childMap.containsKey("正在直播")){

                    }else{
                        childMap.put("正在直播",new ArrayList<LiveRoomNew>());
                    }
                    childMap.get("正在直播").add(liveRoomNew);
                }
            }
        }
        groupList.addAll(childMap.keySet());

        if(groupList.size()>1&&groupList.get(0).equals("即将开始")){
            List<String> tempList = new ArrayList<>();
            tempList.add(0,groupList.get(1));
            tempList.add(1,groupList.get(0));
            groupList.clear();
            groupList.addAll(tempList);
        }
        callListAdapter.setGroupList(groupList);
        callListAdapter.setChildMap(childMap);
        callListAdapter.notifyDataSetChanged();
        for (int i = 0; i < callListAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }


    class GetDataListTask extends AsyncTask<String, Void, List<LiveRoomNew>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<LiveRoomNew> doInBackground(String... params) {
            if (isClear) {
                mCurrentPage = 1;
            } else {
                mCurrentPage++;
            }
            try {
                Map<String, String> stringMap = ApiConfig.getCommonMap(getActivity());
                stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(getActivity(), stringMap));
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_LIVE_LIST_V4, stringMap);
                if (str != null) {
                    CommonResponse4List<LiveRoomNew> response = CommonResponse4List.fromJson(str, LiveRoomNew.class);
                    if (response != null && response.isSuccess()) {
                        if (response.getData() == null)
                            return new ArrayList<LiveRoomNew>();
                        return response.getData();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<LiveRoomNew> list) {
            if (!isAdded()) {
                return;
            }
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);

            if (mCurrentPage == 1) {
                initViewPager();
            }

            if (list != null) {
                onMyExecute(list);
            } else {
                showCusToast("网络连接失败！");
            }
        }
    }

    void initViewPager() {
        HashMap<String, String> request = new HashMap<>();
        request.put("callType", HomePagerItem.REQUEST_LIVE);
        String url = AndroidAPIConfig.URL_ADS_4HOMEANDLIVE;
        HttpClientHelper.doPostOption((BaseActivity) getActivity(), url, request, null, new NetCallback((BaseActivity) getActivity()) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {

            }

            @Override
            public void onResponse(String response) {
                CommonResponse4List<HomePagerItem> commonResponse4List = CommonResponse4List.fromJson(response, HomePagerItem.class);
                homePagerItemList = commonResponse4List.getData();
                if (homePagerItemList != null
                        && homePagerItemList.size() > 0) {
//                    headerView.setVisibility(View.VISIBLE);
//                    headerView.setPadding(0, 0, 0, 0);
                    listView.removeHeaderView(headerView);
                    listView.addHeaderView(headerView);
                    initViewPager(homePagerItemList);
                } else {
//                    headerView.setVisibility(View.GONE);
//                    headerView.setPadding(0, -headerView.getHeight(), 0, 0);
                    listView.removeHeaderView(headerView);
                }
            }
        }, false);
    }

    void initViewPager(List<HomePagerItem> pagerItemList) {

        List<View> mListViews = new ArrayList<>();
        for (int i = 0; i < pagerItemList.size(); i++) {
            mListViews.add(View.inflate(getActivity(), R.layout.home_top_pager_item, null));
        }
        autoViewPager.setAdapter(new MyViewPagerAdapter(mListViews, pagerItemList));

        autoViewPager.startAutoScroll();
        autoViewPager.setInterval(5000);//自动切换时间
        autoViewPager.setOnPageChangeListener(new MOnpagerChangeLister());

        LinearLayout dotlayout = (LinearLayout) headerView.findViewById(R.id.dotlayout);
        if (dotlayout != null) {
            dotlayout.removeAllViews();
            dotViewList.clear();
            dotlayout.setVisibility(View.VISIBLE);

            int width = Utils.dip2px(getActivity(), 10);
            int margin = Utils.dip2px(getActivity(), 2);
            for (int i = 0; i < mListViews.size(); i++) {
                TextView textView = new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, margin);
                params.setMargins(margin, 0, margin, 0);
                textView.setLayoutParams(params);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_top_pager_dot_bg));
                dotlayout.addView(textView);
                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autoViewPager.setCurrentItem(index);
                    }
                });
                dotViewList.add(textView);
            }
        }
        currentDotView = dotViewList.get(0);
        currentDotView.setSelected(true);
        autoViewPager.setCurrentItem(0);
    }

    /**
     * 首页滚动banner
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;
        List<HomePagerItem> pagerItemList;


        public MyViewPagerAdapter(List<View> mListViews, List<HomePagerItem> pagerItemList) {
            this.mListViews = mListViews;
            this.pagerItemList = pagerItemList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            position = position % pagerItemList.size();
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡

            final int mPos = position % pagerItemList.size();
            container.addView(mListViews.get(mPos), 0);//添加页卡

            View itemView = mListViews.get(mPos);
            ImageView item_img = (ImageView) itemView.findViewById(R.id.item_img);
            ImageLoader.getInstance().displayImage(pagerItemList.get(mPos).getImage_url(),
                    item_img, AppImageLoaderConfig.getCommonDisplayImageOptions(getActivity(), BaseInterface.getLoadingDrawable(getActivity(), true)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        currentDotView.setSelected(true);//第一次click 会失去selected效果，这里强制设置 true
                        String url = pagerItemList.get(mPos).getUrl();
                        if (url != null && url.startsWith(OpenActivityUtil.SCHEME_SUB)) {
                            startActivity(OpenActivityUtil.getIntent(getActivity(), url));
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), WebActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", pagerItemList.get(mPos).getTitle());
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return mListViews.get(mPos);
        }

        @Override
        public int getCount() {
            return mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
    }

    /**
     * 首页banner监听
     */
    private class MOnpagerChangeLister implements
            ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (dotViewList == null || dotViewList.size() == 0)
                return;
            arg0 = arg0 % dotViewList.size();

            if (arg0 < dotViewList.size()) {
                if (currentDotView != null)
                    currentDotView.setSelected(false);
                currentDotView = dotViewList.get(arg0);
                currentDotView.setSelected(true);
            }
        }
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        isClear = true;
        new GetDataListTask().execute("");
    }


    /**
     * 选择要进入的房间
     *
     * @param event
     */
    public void onEventMainThread(IndexLiveItemClickEvent event) {

        if(!isVisible){
            return;
        }

        //必须登录
        if (!new UserInfoDao(getActivity()).isLogin()) {
            DialogUtil.showLoginConfirmDlg(getActivity(), "直播需要登录后才能观看", true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return false;
                }
            });
            return;
        }
        //首先获取数据接口 检查是否为第一次 进直播，需要输入昵称
        item = event.liveRoomNew;
        if (item.getChannelStatus() == LiveRoomNew.STATUS_ON) {

        } else {

        }
        MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_LIVE, item.getChannelName());
        new GetDataTask(true).execute();


        HashMap<String, String> stringMap = new HashMap<>();
        stringMap.put(UserInfo.UID, new UserInfoDao(getActivity()).queryUserInfo().getUserId());
        stringMap.put("authorId", item.getAuthorId() + "");

        //进入聊天室 添加分析师观看记录 本地记录是否保存
        if (!PreferenceSetting.getBoolean(getActivity(), new UserInfoDao(getActivity()).queryUserInfo().getUserId() + "" + item.getAuthorId())) {
            HttpClientHelper.doPostOption((BaseActivity) getActivity(), AndroidAPIConfig.URL_LIVE_WATCHADD, stringMap, null, new NetCallback((BaseActivity) getActivity()) {
                @Override
                public void onFailure(String resultCode, String resultMsg) {

                }

                @Override
                public void onResponse(String response) {
                    //本地记录保存
                    PreferenceSetting.setBoolean(getActivity(), new UserInfoDao(getActivity()).queryUserInfo().getUserId() + "" + item.getAuthorId(), true);
                }
            }, false);
        }
    }
}









