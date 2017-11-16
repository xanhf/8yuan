package com.trade.eight.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by fangzhu on 16/4/4.
 * <p/>
 * 1、application 中初始化
 * 2、activity 中 new 出对象
 * 3、调用 shareToWeiXinCircle
 * 4、调用onActivityResult
 */
public class ShareTools {
    BaseActivity mActivity;
    /**
     * 分享的回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
            mActivity.showCusToast("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(mActivity, "分享失败", Toast.LENGTH_SHORT).show();
            mActivity.showCusToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(mActivity, "分享取消", Toast.LENGTH_SHORT).show();
            mActivity.showCusToast("分享已取消");
        }
    };

    public ShareTools(BaseActivity mActivity) {
        this.mActivity = mActivity;
        //设置dialog的样式
        Config.dialog = mActivity.getmProgressDialog("请稍等");
        //开启debug
        com.umeng.socialize.utils.Log.LOG = BaseInterface.isDubug;
        //toast 提示
//        Config.IsToastTip = false;

    }

    /**
     * 微信分享 初始化
     * appkey appsecret
     */
    public static void initPlatfrom() {
        PlatformConfig.setWeixin(BaseInterface.WX_APP_KEY, BaseInterface.WX_APP_SECRET);
    }

    public Dialog getShareDialog(Activity context) {
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.trade_share_dialog);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(R.style.ani_in_bottm_out_bottom);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;

    }

    /**
     * 传入ShareEntity 直接分享
     *
     * @param context
     * @param entity
     */
    public void showShareDialog(final Activity context, final ShareEntity entity) {
        final Dialog dialog = getShareDialog(context);
        final View shareFriendView = dialog.findViewById(R.id.shareFriendView);
        if (shareFriendView != null) {
            shareFriendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    shareToWeiXinFriend(R.drawable.app_icon, entity, umShareListener);
                }
            });
        }
        final View shareCircleView = dialog.findViewById(R.id.shareCircleView);
        if (shareCircleView != null) {
            shareCircleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    shareToWeiXinCircle(R.drawable.app_icon, entity, umShareListener);
                }
            });
        }
        dialog.show();
    }

    /**
     * 传入ShareEntity 直接分享 带有回调地址
     * @param context
     * @param entity
     * @param hasCallBack
     */

    public void showShareDialog(final Activity context, final ShareEntity entity,boolean hasCallBack) {
        final Dialog dialog = getShareDialog(context);
        final View shareFriendView = dialog.findViewById(R.id.shareFriendView);
        if (shareFriendView != null) {
            shareFriendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    shareToWeiXinFriend(R.drawable.app_icon, entity, new UMShareListener() {
                        @Override
                        public void onResult(SHARE_MEDIA platform) {
                            HttpClientHelper.doGetOption(mActivity, entity.getCallback(), null, new NetCallback(mActivity) {
                                @Override
                                public void onFailure(String resultCode, String resultMsg) {

                                }

                                @Override
                                public void onResponse(String response) {

                                }
                            },false);
                            mActivity.showCusToast("分享成功");
                        }

                        @Override
                        public void onError(SHARE_MEDIA platform, Throwable t) {
                            mActivity.showCusToast("分享失败");
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA platform) {
                            mActivity.showCusToast("分享已取消");
                        }
                    });
                }
            });
        }
        final View shareCircleView = dialog.findViewById(R.id.shareCircleView);
        if (shareCircleView != null) {
            shareCircleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    shareToWeiXinCircle(R.drawable.app_icon, entity, new UMShareListener() {
                        @Override
                        public void onResult(SHARE_MEDIA platform) {

                            HttpClientHelper.doGetOption(mActivity, entity.getCallback(), null, new NetCallback(mActivity) {
                                @Override
                                public void onFailure(String resultCode, String resultMsg) {

                                }

                                @Override
                                public void onResponse(String response) {

                                }
                            },false);

                            mActivity.showCusToast("分享成功");
                        }

                        @Override
                        public void onError(SHARE_MEDIA platform, Throwable t) {
                            mActivity.showCusToast("分享失败");
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA platform) {
                            mActivity.showCusToast("分享已取消");
                        }
                    });
                }
            });
        }
        dialog.show();
    }

    /**
     * 分享到微信朋友圈
     */
    public void shareToWeiXinCircle(int drawableId, ShareEntity entity, UMShareListener shareListener) {
        try {
            boolean isInstall = UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE);
            if (!isInstall) {
                mActivity.showCusToast("您还没有安装微信客户端");
                return;
            }

//        boolean isInstall = isWeixinAvilible(mActivity);
//        if (!isInstall) {
//            Toast.makeText(mActivity, "您还没有安装微信客户端", Toast.LENGTH_SHORT).show();
//            return;
//        }
            UMImage image = new UMImage(mActivity, drawableId);

            //网络图片
            if (entity.getPic() != null
                    && entity.getPic().startsWith("http")) {
                image = new UMImage(mActivity, entity.getPic());
            }
            String title = ConvertUtil.NVL(entity.getTitle(), "");
            String content = ConvertUtil.NVL(entity.getContent(), "");
            //处理title content空字符
            if (StringUtil.isEmpty(title)
                    && !StringUtil.isEmpty(content))
                title = content;
            if (StringUtil.isEmpty(content)
                    && !StringUtil.isEmpty(title))
                content = title;

//            String title = ConvertUtil.NVL(entity.getContent(), mActivity.getResources().getString(R.string.app_name));

            //注意 这里只显示title,但是 withText  链接点击加载
            ShareAction shareAction = new ShareAction(mActivity)
                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .setCallback(shareListener)
                    .withTitle(title)
                    .withText(content)
                    .withTargetUrl(entity.getUrl())
                    .withMedia(image);
            shareAction.share();


//            UMShareAPI shareAPI = UMShareAPI.get(mActivity);
//            shareAPI.doShare(mActivity, shareAction, shareListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareToWeiXinFriend(int drawableId, ShareEntity entity, UMShareListener shareListener) {
        try {
            boolean isInstall = UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE);
            if (!isInstall) {
                mActivity.showCusToast("您还没有安装微信客户端");
                return;
            }


            UMImage image = new UMImage(mActivity, drawableId);

            //网络图片
            if (entity.getPic() != null
                    && entity.getPic().startsWith("http")) {
                image = new UMImage(mActivity, entity.getPic());
            }
            String title = ConvertUtil.NVL(entity.getTitle(), "");
            String content = ConvertUtil.NVL(entity.getContent(), "");
            //处理title content空字符
            if (StringUtil.isEmpty(title)
                    && !StringUtil.isEmpty(content))
                title = content;
            if (StringUtil.isEmpty(content)
                    && !StringUtil.isEmpty(title))
                content = title;

            //注意 这里只显示title,但是 withText  链接点击加载
            ShareAction shareAction = new ShareAction(mActivity)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .setCallback(shareListener)
                    .withTitle(title)
                    .withText(content)
                    .withTargetUrl(entity.getUrl())
                    .withMedia(image);
            shareAction.share();


//            UMShareAPI shareAPI = UMShareAPI.get(mActivity);
//            shareAPI.doShare(mActivity, shareAction, shareListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在当前activity 的onActivityResult中调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            UMShareAPI.get(mActivity).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UMShareListener getUmShareListener() {
        return umShareListener;
    }

    public void setUmShareListener(UMShareListener umShareListener) {
        this.umShareListener = umShareListener;
    }
}
