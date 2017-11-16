package com.trade.eight.moudle.chatroom.gift.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.chatroom.gift.GiftObj;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.moudle.chatroom.gift.ani.GiftAnimationUtil;
import com.trade.eight.moudle.chatroom.viewholder.ChatRoomViewHolderHelper;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Log;

import java.util.Map;

/**
 * Created by dufangzhu on 2017/4/7.
 * 礼物显示的布局进行封装
 */
public class GiftLayout extends FrameLayout {
    public static final int TIME_ANI_GIFT_IN = 200;
    public static final int TIME_ANI_LAYOUT_IN = 300;

    public GiftLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GiftLayout(Context context) {
        super(context);
        init(context);
    }

    public GiftLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    ImageView img_gift;
    TextView tv_number;
    View rootView;

    ImageView tempView;
    ImageView img_level, img_templevel;
    TextView tv_nickname;
    TextView tv_tempnickname;

    Context context;

    protected void init(Context context) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.gift_show_layout, this, false);

        img_level = (ImageView) rootView.findViewById(R.id.img_level);
        img_templevel = (ImageView) rootView.findViewById(R.id.img_templevel);

        tv_nickname = (TextView) rootView.findViewById(R.id.tv_nickname);
        img_gift = (ImageView) rootView.findViewById(R.id.img_gift);
        tv_number = (TextView) rootView.findViewById(R.id.tv_number);

        tv_tempnickname = (TextView) rootView.findViewById(R.id.tv_tempnickname);
        tempView = (ImageView) rootView.findViewById(R.id.tempView);

        addView(rootView);

    }

    protected void initValue(GiftObj giftObj) {
        if (giftObj == null)
            return;
        if (giftObj.getMessage() == null)
            return;
        String nick = ChatRoomViewHolderHelper.subNick4GiftLive(ChatRoomViewHolderHelper.getNick((ChatRoomMessage) giftObj.getMessage()));
        tv_nickname.setText(nick);
        tv_tempnickname.setText(nick);

//        int level = ChatRoomViewHolderHelper.getLevel(context, ((ChatRoomMessage) giftObj.getMessage()));
//        if (GiftPanUtil.liveLevelMap.containsKey(level)) {
//            img_level.setImageResource(GiftPanUtil.liveLevelMap.get(level));
//            img_level.setVisibility(View.VISIBLE);
//            img_templevel.setVisibility(View.VISIBLE);
//        } else {
//            img_level.setVisibility(View.GONE);
//            img_templevel.setVisibility(View.GONE);
//        }

        Map<String, Object> map = giftObj.getMessage().getRemoteExtension();
        if (map != null && map.containsKey(UserInfo.ULEVELNUM)) {
            int level = (int) map.get(UserInfo.ULEVELNUM);
            if (GiftPanUtil.liveLevelMap.containsKey(level)) {
                img_level.setImageResource(GiftPanUtil.liveLevelMap.get(level));
                img_level.setVisibility(View.VISIBLE);
                img_templevel.setVisibility(View.VISIBLE);
            } else {
                img_level.setVisibility(View.GONE);
                img_templevel.setVisibility(View.GONE);
            }
        }

        //优先获取本地图片，本地图片如果没有
        if (GiftPanUtil.giftRes.containsKey(giftObj.getGiftId())) {
            img_gift.setImageResource(GiftPanUtil.giftRes.get(giftObj.getGiftId()));
        } else {
            //网络加载
            if (GiftPanUtil.giftCashMap.containsKey(giftObj.getGiftId())) {
                GiftObj obj = GiftPanUtil.giftCashMap.get(giftObj.getGiftId());
                ImageLoader.getInstance().displayImage(obj.getGiftPic(),
                        img_gift, AppImageLoaderConfig.getCommonDisplayImageOptions(context, BaseInterface.getLoadingDrawable(context, false)));
            }
        }

        //注意这里的type，已经不是管理员了
//        MemberType memberType = ChatRoomViewHolderHelper.getMemberType((ChatRoomMessage) giftObj.getMessage());
//        boolean isAdmin = false;
//        if (giftObj.getMessage().getMsgType() == MsgTypeEnum.custom
//            && (memberType == MemberType.ADMIN
//                || memberType == MemberType.CREATOR)) {
//            isAdmin = true;
//            //管理员
//            ViewGroup.LayoutParams p = img_level.getLayoutParams();
//            p.width = Utils.dip2px(context, 15);
//            p.height = Utils.dip2px(context, 15);
//            img_level.setLayoutParams(p);
//            img_level.setImageResource(R.drawable.chat_room_ic_v);
////            img_level.setVisibility(View.GONE);
//        }
        tv_number.setText("x" + giftObj.getGiftNum());
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearAnimation();
            //消失动画
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.gift_laytou_out));
            //使用GONE不会显示动画
            setVisibility(View.INVISIBLE);
        }
    };


    /**
     * @param giftObj 包含message
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startAni(GiftObj giftObj) {
        initValue(giftObj);
        if (getVisibility() == View.VISIBLE) {
            //如果当前是可见的两种情况
            //1、连击
            //2、更换了用户发送

            //判断是同一个人 连击
            boolean isLianji = false;
            int num = 0;
            if (getTag() != null) {
                GiftObj cashGift = (GiftObj) getTag();
                //同一个账号发送的同一种礼物
                if (cashGift != null
                        && cashGift.getMessage() != null
                        && cashGift.getMessage().getFromAccount().equals(giftObj.getMessage().getFromAccount())
                        && cashGift.getGiftId().equals(giftObj.getGiftId())) {
                    num = cashGift.getExNum();
                    isLianji = true;
                }
            }
            int showNum = (num + giftObj.getGiftNum());
            if (isLianji) {
                //只需要动画显示数字放大
                tv_number.setText("x" + showNum);
                //额外标记的数字
                giftObj.setExNum(showNum);
                GiftAnimationUtil.scaleGiftNum(tv_number).start();
            } else {
                //不同用户发送
                initAni();
            }

        } else {
            //更换了用户发送
            clearAnimation();
            initAni();
        }
        //使用view来标记当前礼物
        giftObj.setExTime(System.currentTimeMillis());
        setTag(giftObj);
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    /**
     * 初始化进入动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void initAni() {
        //整体的布局进入动画
        ObjectAnimator flyFromLtoR = GiftAnimationUtil.createFlyFromLtoR(this, -getWidth(), 0, TIME_ANI_LAYOUT_IN, new OvershootInterpolator());
        flyFromLtoR.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisibility(View.VISIBLE);
                setAlpha(1f);
//                img_gift.setVisibility(View.INVISIBLE);
//                isShowing = true;
//                anim_num.setText("x " + 1);
                Log.i("TAG", "flyFromLtoR A start");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tv_number.setVisibility(View.VISIBLE);

            }
        });

        //送出的礼物飞入动画
//        ObjectAnimator flyFromLtoR2 = GiftAnimationUtil.createFlyFromLtoR(img_gift, -getWidth() / 3, 0, TIME_ANI_GIFT_IN, new DecelerateInterpolator());
//        flyFromLtoR2.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                img_gift.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
////                GiftAnimationUtil.startAnimationDrawable(anim_light);
//                tv_number.setVisibility(View.VISIBLE);
//            }
//        });


        //数字放大动画
//        ObjectAnimator scaleGiftNum = GiftAnimationUtil.scaleGiftNum(tv_number);

        //执行动画
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(flyFromLtoR);

//        animSet.play(flyFromLtoR).before(flyFromLtoR2);
//        animSet.play(scaleGiftNum).after(flyFromLtoR2);
        animSet.start();
    }


}
