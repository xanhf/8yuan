package com.trade.eight.moudle.chatroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.CallList;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.view.MyEffectView;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.easylife.ten.lib.R.id.view_topline;


/**
 * 直播室喊单
 * 操作建议
 */
public class CallListFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    public static final String TAG = "CallListFragment";

    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    private CallListAdapter callListAdapter;
    private Context mContext;
    private int mCurrentPage = 1;
    private boolean isClear = true;
    View view = null;
    String roomId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_call_list, null);

        if (getArguments() != null)
            roomId = getArguments().getString("roomId");
        initView(view);
//        doReflash();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    public void doReflash() {
        if (callListAdapter != null && callListAdapter.getCount() > 0) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
        PreferenceSetting.setRefershTime(getActivity(), PreferenceSetting.KEY_REFERSH_TIME_NEWS_CALLLIST, currentTime);
        mPullRefreshListView.doPullRefreshing(false, 0);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
            Log.v(TAG, "setUserVisibleHint " + isVisibleToUser);
            doReflash();
//            isClear = true;
//            new GetDataTask().execute("");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    public void initView(View view) {
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.list_view);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullLoadEnabled(true);
        mPullRefreshListView.setPullRefreshEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();

        callListAdapter = new CallListAdapter(mContext, 0, new ArrayList<CallList>());
        listView.setAdapter(callListAdapter);
        listView.setDividerHeight(0);
        listView.setSelector(getResources().getDrawable(android.R.color.transparent));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //page_chatRoom 使用page tag
        MyAppMobclickAgent.onEvent(getActivity(), "page_chatRoom", "onPullDownToRefresh_callList");
        mPullRefreshListView.setLastUpdatedLabel(""
                + sdf.format(new Date(PreferenceSetting.getRefershTime(mContext, PreferenceSetting.KEY_REFERSH_TIME_NEWS_CALLLIST, System.currentTimeMillis()))));
        isClear = true;
        new GetDataTask().execute("");
    }


    class GetDataTask extends AsyncTask<String, Void, List<CallList>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<CallList> doInBackground(String... params) {
            if (isClear) {
                mCurrentPage = 1;
            } else {
                mCurrentPage++;
            }
            try {
                Map<String, String> stringMap = new LinkedHashMap<>();
                stringMap.put("page", mCurrentPage + "");
                stringMap.put("pageSize", "20");
                stringMap.put("roomId", roomId);
                stringMap = ApiConfig.getParamMap(getActivity(), stringMap);
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_CHATROOM_ADVICE, stringMap);
                if (str != null) {
                    CommonResponse4List<CallList> response = CommonResponse4List.fromJson(str, CallList.class);
                    if (response != null && response.isSuccess()) {
                        if (response.getData() == null)
                            return new ArrayList<CallList>();
                        return response.getData();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<CallList> callList) {
            if (!isAdded()) {
                return;
            }
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            long currentTime = System.currentTimeMillis();
            PreferenceSetting.setRefershTime(mContext, PreferenceSetting.KEY_REFERSH_TIME_NEWS_CALLLIST, currentTime);
            mPullRefreshListView.setLastUpdatedLabel("" + sdf.format(new Date(PreferenceSetting.getRefershTime(mContext, PreferenceSetting.KEY_REFERSH_TIME_NEWS_CALLLIST, System.currentTimeMillis()))));

            if (callList != null) {
                if (isClear) {
                    callListAdapter.clear();
                    callListAdapter.setDataList(callList);
                } else {
                    callListAdapter.addDataList(callList);
                }
//                for (int i = 0; i < callList.size(); i++) {
//                    callListAdapter.add(callList.get(i));
//                }
//                callListAdapter.notifyDataSetChanged();
            } else {
                showCusToast("网络连接失败！");
            }
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        isClear = false;
        new GetDataTask().execute("");
    }

    class CallListAdapter extends ArrayAdapter<CallList> {
        List<CallList> objects;

        public CallListAdapter(Context context, int resource, List<CallList> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        public void setDataList(List<CallList> list) {
            this.objects.clear();
            this.objects.addAll(list);
            notifyDataSetChanged();
        }

        public void addDataList(List<CallList> list) {
            this.objects.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            if (type == 0) {
                ViewHolerType0 viewHolerType0 = null;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.layout_calllist_head, null);
                    viewHolerType0 = new ViewHolerType0();
                    viewHolerType0.text_topcall_time = (TextView) convertView.findViewById(R.id.text_topcall_time);
                    viewHolerType0.text_topcall_tag = (LinearLayout) convertView.findViewById(R.id.text_topcall_tag);
                    viewHolerType0.text_topcall_content = (TextView) convertView.findViewById(R.id.text_topcall_content);
                    convertView.setTag(viewHolerType0);
                } else {
                    viewHolerType0 = (ViewHolerType0) convertView.getTag();
                }

                CallList item = objects.get(position);
                String time = DateUtil.formatDate(new Date(item.getCreateTime()), "MM-dd HH:mm");
                if (DateUtil.formatDate(new Date(item.getCreateTime()), "yyyy-MM-dd").equals(DateUtil.formatDate(new Date(), "yyyy-MM-dd"))) {
                    time = "今天  " + DateUtil.formatDate(new Date(item.getCreateTime()), "HH:mm");
                }
                viewHolerType0.text_topcall_time.setText(time);
                viewHolerType0.text_topcall_content.setText(Html.fromHtml(item.getStyleContent()));
                if (!TextUtils.isEmpty(item.getLabel())) {
                    String[] tags = item.getLabel().split(",");
                    viewHolerType0.text_topcall_tag.removeAllViews();
                    for (int i = 0; i < tags.length; i++) {
                        if (i < 4) {
                            View view = View.inflate(mContext, R.layout.layout_calllist_headtag, null);
                            TextView text_tags = (TextView) view.findViewById(R.id.text_tags);
                            switch (i) {
                                case 0:
                                    text_tags.setBackgroundResource(R.drawable.bg_btn_blue);
                                    text_tags.setTextColor(mContext.getResources().getColor(R.color.sub_blue));
                                    break;
                                case 1:
                                    text_tags.setBackgroundResource(R.drawable.bg_btn_opt_gt);
                                    text_tags.setTextColor(mContext.getResources().getColor(R.color.color_opt_gt));
                                    break;
                                case 2:
                                    text_tags.setBackgroundResource(R.drawable.bg_btn_opt_lt);
                                    text_tags.setTextColor(mContext.getResources().getColor(R.color.color_opt_lt));
                                    break;
                                case 3:
                                    text_tags.setBackgroundResource(R.drawable.bg_btn_color6d92fb);
                                    text_tags.setTextColor(mContext.getResources().getColor(R.color.color_6D92EB));
                                    break;
                            }
                            text_tags.setText(tags[i]);
                            viewHolerType0.text_topcall_tag.addView(view);
                        }
                    }
                }
            } else if (type == 1) {
                ViewHolerType1 viewHolerType1 = null;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_news_call_list, null);
                    viewHolerType1 = new ViewHolerType1();
                    viewHolerType1.view_topline = convertView.findViewById(R.id.view_topline);
                    viewHolerType1.icon = (TextView) convertView.findViewById(R.id.icon);
                    viewHolerType1.effect_bottom= convertView.findViewById(R.id.effect_bottom);
                    viewHolerType1.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolerType1.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    convertView.setTag(viewHolerType1);
                } else {
                    viewHolerType1 = (ViewHolerType1) convertView.getTag();
                }

                CallList item = objects.get(position);
                if (position == 0 || (position != 0 && getItemViewType(position - 1) == 0)) {
                    viewHolerType1.view_topline.setVisibility(View.INVISIBLE);
                    viewHolerType1.icon.setBackgroundColor(getActivity().getResources().getColor(R.color.c_464646));
                    viewHolerType1.view_topline.setBackgroundColor(getActivity().getResources().getColor(R.color.c_464646));
                    viewHolerType1.effect_bottom.setBackgroundColor(getActivity().getResources().getColor(R.color.c_464646));

                } else {
                    viewHolerType1.view_topline.setVisibility(View.VISIBLE);
                    viewHolerType1.icon.setBackgroundResource(R.drawable.chat_text_tab_dot01);
                    viewHolerType1.icon.setBackgroundColor(getActivity().getResources().getColor(R.color.c_999999));
                    viewHolerType1.view_topline.setBackgroundColor(getActivity().getResources().getColor(R.color.c_999999));
                    viewHolerType1.effect_bottom.setBackgroundColor(getActivity().getResources().getColor(R.color.c_999999));

                }
                int h = 0;
                String time = DateUtil.formatDate(new Date(item.getCreateTime()), "MM-dd HH:mm");
                if (DateUtil.formatDate(new Date(item.getCreateTime()), "yyyy-MM-dd").equals(DateUtil.formatDate(new Date(), "yyyy-MM-dd"))) {
                    time = "今天  " + DateUtil.formatDate(new Date(item.getCreateTime()), "HH:mm");
                }
                viewHolerType1.tv_time.setText(time);
                viewHolerType1.tv_content.setText(Html.fromHtml(ConvertUtil.NVL(item.getRemark(), "")));
            }

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            CallList callList = getItem(position);
            if (callList.getTop() > 0) {// 置顶
                return 0;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        class ViewHolerType0 {
            TextView text_topcall_time;
            LinearLayout text_topcall_tag;
            TextView text_topcall_content;
            MyEffectView effect_bottom;
        }

        class ViewHolerType1 {
            View view_topline;
            TextView icon;
            View effect_bottom;
            TextView tv_time;
            TextView tv_content;
        }

    }
}
