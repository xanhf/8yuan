package com.trade.eight.moudle.chatroom.gift;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.chatroom.fragment.GiftPagerAdapter;
import com.trade.eight.moudle.chatroom.gift.view.GiftLayout;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by dufangzhu on 2017/4/5.
 * 礼物弹窗面板
 */

public class GiftPanUtil {
    /**
     * 收到的数据格式
     * type:1
     * data: {giftId=2, giftName=掌声, giftNum=1}
     */
    public static final int CUSTOM_TYPE_GIFT = 1;
    public static final String KEY_TYPE = "type";
    public static final String KEY_DATA = "data";


    //直播室等级显示
    public static HashMap<Integer, Integer> liveLevelMap = new HashMap<>();

    static {
        liveLevelMap.put(1, R.drawable.live_level_v1);
        liveLevelMap.put(2, R.drawable.live_level_v2);
        liveLevelMap.put(3, R.drawable.live_level_v3);
        liveLevelMap.put(4, R.drawable.live_level_v4);
        liveLevelMap.put(5, R.drawable.live_level_v5);
        liveLevelMap.put(6, R.drawable.live_level_v6);
        liveLevelMap.put(7, R.drawable.live_level_v7);

    }

    //这里优先会取本地的图片
    public static HashMap<String, Integer> giftRes = new HashMap<>();

    static {
        giftRes.put("12", R.drawable.live_gift_applause);
        giftRes.put("13", R.drawable.live_gift_great);
        giftRes.put("14", R.drawable.live_gift_driver);

    }

    //礼物的网络图片，第一次加载后缓存
    public static HashMap<String, GiftObj> giftCashMap = new HashMap<>();


    /*最外层直播室列表对象*/
    LiveRoomNew roomNew;
    BaseActivity context;

    public GiftPanUtil(BaseActivity context, LiveRoomNew roomNew) {
        this.roomNew = roomNew;
        this.context = context;
        init(context);
    }

    PopupWindow window;
    View contentView;

    public void init(Activity context) {
        contentView = View.inflate(context, R.layout.chatroom_gift_pan, null);
        window = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);

        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.ani_in_bottm_out_bottom);
        //防止被底部虚拟键挡住
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        // 在底部显示
//        window.showAtLocation(context.findViewById(R.id.textMessageLayout),
//                Gravity.BOTTOM, 0, 0);


    }

    private void initValue(GiftSuperObj obj) {
        if (window == null)
            return;
        if (roomNew == null)
            return;
        if (obj == null)
            return;

        ImageView img_head = (ImageView) contentView.findViewById(R.id.img_head);
        if (new UserInfoDao(context).isLogin()) {
            String pic = new UserInfoDao(context).queryUserInfo().getAvatar();
            if (!StringUtil.isEmpty(pic)) {
                ImageLoader.getInstance().displayImage(pic,
                        img_head, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.liveroom_icon_person));
            }
        }

        TextView tv_teacher = (TextView) contentView.findViewById(R.id.tv_teacher);
        if (tv_teacher != null) {
            tv_teacher.setText(roomNew.getAuthorName());
        }
        TextView tv_valpoint = (TextView) contentView.findViewById(R.id.tv_valpoint);
        if (tv_valpoint != null) {
            tv_valpoint.setText(obj.getValidPoints() + "");
        }

        View closeView = contentView.findViewById(R.id.closeView);
        if (closeView != null) {
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (window != null)
                        window.dismiss();
                }
            });
        }


        View btn_send = contentView.findViewById(R.id.btn_send);
        if (btn_send != null) {
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGift();
                }
            });
        }
    }

    /*获取到的数据*/
    GiftSuperObj giftSuperObj;

    public void sendGift() {
        final ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);
        if (viewPager.getAdapter() == null
                || ((GiftPagerAdapter) viewPager.getAdapter()).getSelectObj() == null) {
            context.showCusToast("请选择您要送出的礼物");
            return;
        }
        GiftObj giftObj = ((GiftPagerAdapter) viewPager.getAdapter()).getSelectObj();
        if (giftSuperObj != null
                && giftSuperObj.getValidPoints() < giftObj.getPoins()) {
            showPointDlg();
            return;
        }
        //发送礼物
        send(context, giftObj, roomNew);
    }

    View currentDotView;
    private ArrayList<View> dotViewList = new ArrayList<View>();
    private List<View> mListViews = new ArrayList<>();
    int pageItemSize = 3;//每页显示的条数

    /**
     * 初始化viewpager
     *
     * @param context
     * @param list
     */
    public void initViewPager(Context context, List<GiftObj> list) {
        if (list == null || list.size() == 0)
            return;
        final ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);
        //每页三个的list
        List<List<GiftObj>> optList = new ArrayList<List<GiftObj>>();
        mListViews.clear();
        int page = list.size() / pageItemSize;
        if (list.size() % pageItemSize > 0)
            ++page;
        for (int i = 0; i < page; i++) {
            mListViews.add(View.inflate(context, R.layout.item_viewpager_gift, null));
            int end = (i + 1) * pageItemSize;
            List<GiftObj> listItem = list.subList(i * pageItemSize, end >= list.size() ? list.size() : end);
            optList.add(listItem);
        }


        GiftPagerAdapter adapter = new GiftPagerAdapter(context, mListViews, optList);
        viewPager.setAdapter(adapter);
        adapter.setCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //双击发送礼物
                sendGift();
                return false;
            }
        });
        viewPager.setCurrentItem(0);
//        viewPager.setOnPageChangeListener(new OptionsPagerChangeLister(optList));

        LinearLayout dotlayout = (LinearLayout) contentView.findViewById(R.id.dotlayout);
        if (dotlayout != null) {
            if (page > 1)
                dotlayout.setVisibility(View.VISIBLE);
            else
                dotlayout.setVisibility(View.GONE);

            dotlayout.removeAllViews();
            dotViewList.clear();
            int width = Utils.dip2px(context, 5);
            int margin = Utils.dip2px(context, 2);
            for (int i = 0; i < mListViews.size(); i++) {
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
                params.setMargins(margin, 0, margin, 0);
                textView.setLayoutParams(params);
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_nav_dotview));
                dotlayout.addView(textView);
                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                dotViewList.add(textView);
            }
        }
        currentDotView = dotViewList.get(0);
        currentDotView.setSelected(true);
    }


    /**
     * 获取到礼物数据之后 显示礼物面板
     *
     * @param context
     */
    public void load(final BaseActivity context) {
        new AsyncTask<Void, Void, CommonResponse<GiftSuperObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                context.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<GiftSuperObj> doInBackground(Void... params) {
                return GiftHelper.getIntegral(context);
            }

            @Override
            protected void onPostExecute(CommonResponse<GiftSuperObj> giftSuperObjCommonResponse) {
                super.onPostExecute(giftSuperObjCommonResponse);
                if (context.isFinishing())
                    return;
                context.hideNetLoadingProgressDialog();
                String errorInfo = context.getResources().getString(R.string.network_problem);
                if (giftSuperObjCommonResponse == null) {
                    context.showCusToast(errorInfo);
                    return;
                }
                if (!giftSuperObjCommonResponse.isSuccess()) {
                    context.showCusToast(ConvertUtil.NVL(giftSuperObjCommonResponse.getErrorInfo(), errorInfo));
                    return;
                }
                if (giftSuperObjCommonResponse.getData() == null) {
                    context.showCusToast(ConvertUtil.NVL(giftSuperObjCommonResponse.getErrorInfo(), errorInfo));
                    return;
                }
                // 在底部显示
                window.showAtLocation(context.findViewById(R.id.textMessageLayout),
                        Gravity.BOTTOM, 0, 0);
                //获取到数据
                giftSuperObj = giftSuperObjCommonResponse.getData();
                initValue(giftSuperObjCommonResponse.getData());
                initViewPager(context, giftSuperObjCommonResponse.getData().giftList);

            }
        }.execute();
    }

    /**
     * 发送礼物
     *
     * @param context
     * @param giftObj
     * @param roomNew
     */
    public void send(final BaseActivity context, final GiftObj giftObj, final LiveRoomNew roomNew) {
        new AsyncTask<Void, Void, CommonResponse<GiftSuperObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                context.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<GiftSuperObj> doInBackground(Void... params) {
                return GiftHelper.send(context, giftObj, roomNew);
            }

            @Override
            protected void onPostExecute(CommonResponse<GiftSuperObj> giftSuperObjCommonResponse) {
                super.onPostExecute(giftSuperObjCommonResponse);
                if (context.isFinishing())
                    return;
                context.hideNetLoadingProgressDialog();
                String errorInfo = context.getResources().getString(R.string.network_problem);
                if (giftSuperObjCommonResponse == null) {
                    context.showCusToast(errorInfo);
                    return;
                }
                if (!giftSuperObjCommonResponse.isSuccess()) {
                    if (ApiConfig.ERROR_CODE_POINE_LESS.equals(giftSuperObjCommonResponse.getErrorCode())) {
                        showPointDlg();
                        return;
                    }
                    context.showCusToast(ConvertUtil.NVL(giftSuperObjCommonResponse.getErrorInfo(), errorInfo));
                    return;
                }
                if (giftSuperObjCommonResponse.getData() == null) {
                    context.showCusToast(ConvertUtil.NVL(giftSuperObjCommonResponse.getErrorInfo(), errorInfo));
                    return;
                }

                //更新数据
                giftSuperObj = giftSuperObjCommonResponse.getData();
                initValue(giftSuperObjCommonResponse.getData());
//                initViewPager(context, giftSuperObjCommonResponse.getData().giftList);

            }
        }.execute();
    }

    /**
     * 积分不足的提示
     */
    public void showPointDlg() {
        String title = contentView.getResources().getString(R.string.gift_point_less_title);
        String msg = contentView.getResources().getString(R.string.gift_point_less_msg);
        String btnStr = contentView.getResources().getString(R.string.gift_point_less_btn);
        DialogUtil.showTitleAndContentDialog(context, title, msg, btnStr, null);
    }

    public static void showGift(GiftObj giftObj, GiftLayout view01, GiftLayout view02) {
        if (view01.getVisibility() != View.VISIBLE
                && view02.getVisibility() != View.VISIBLE) {
            //都没显示 取第一个
            view01.startAni(giftObj);
        } else if (view01.getVisibility() == View.VISIBLE
                && view02.getVisibility() == View.VISIBLE) {
            //都显示
            if (isLianji(giftObj, view01)) {
                view01.startAni(giftObj);
            } else if (isLianji(giftObj, view02)) {
                view02.startAni(giftObj);
            } else if (view01.getTag() != null
                    && view02.getTag() != null
                    && ((GiftObj) view01.getTag()).getExTime() - ((GiftObj) view02.getTag()).getExTime() > 0) {
                //view01 刚设置过礼物
                view02.startAni(giftObj);
            } else {
                view01.startAni(giftObj);
            }
        } else if (view01.getVisibility() == View.VISIBLE) {
            //第一个在显示
            if (isLianji(giftObj, view01)) {
                view01.startAni(giftObj);
            } else {
                view02.startAni(giftObj);
            }
        } else {
            //第二个在显示
            if (isLianji(giftObj, view02)) {
                view02.startAni(giftObj);
            } else {
                view01.startAni(giftObj);
            }
        }
    }

    public static boolean isLianji(GiftObj giftObj, GiftLayout view) {
        boolean isLianji = false;
        if (view.getVisibility() == View.VISIBLE
                && view.getTag() != null) {
            GiftObj cashGift = (GiftObj) view.getTag();
            //同一个账号发送的同一种礼物
            if (cashGift != null
                    && cashGift.getMessage() != null
                    && cashGift.getMessage().getFromAccount().equals(giftObj.getMessage().getFromAccount())
                    && cashGift.getGiftId().equals(giftObj.getGiftId())) {
                isLianji = true;
            }
        }
        return isLianji;
    }


}
