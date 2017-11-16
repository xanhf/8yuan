package com.trade.eight.moudle.upgradeversion;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/21.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.upgradeversion.entity.UpgradeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadService extends Service {
    private static final int NOTIFY_ID = 0;
    private int progress;
    private NotificationManager mNotificationManager;
    private boolean canceled;

    private DownloadBinder binder;
    private boolean serviceIsDestroy = false;

    private Context mContext = this;

    private UpgradeInfo upgradeInfo;

    private String rootPath = "";
    private String fileName;
    private String absolutePath;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 下载完毕
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    installApk();
                    break;
                case 2:
                    // 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        RemoteViews contentview = mNotification.contentView;
                        contentview.setTextViewText(R.id.tv_upgradeprogress, rate + "%");
//                        contentview.setProgressBar(R.id.progressbar, 100, rate, false);
                    } else {
                       /* // 下载完毕后变换通知形式
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView = null;
                        Intent intent = new Intent(mContext, NotificationUpdateActivity.class);
                        // 告知已完成
                        intent.putExtra("completed", "yes");
                        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", contentIntent);
                        *///
                        serviceIsDestroy = true;
                        stopSelf();// 停掉服务自身
                    }
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub

        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        binder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);


        StringBuilder sb = new StringBuilder();
        File file = getExternalCacheDir();
        if (file != null) {
            rootPath = file.getAbsolutePath();
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath())
                    .append("/Android/data/")
                    .append(getPackageName())
                    .append("/cache/");
            rootPath = sb.toString();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        upgradeInfo = (UpgradeInfo) intent.getSerializableExtra("upgradeInfo");
        fileName = upgradeInfo.getUrl().substring(upgradeInfo.getUrl().lastIndexOf("/"));
        absolutePath = rootPath + fileName;

        if (downLoadThread == null || !downLoadThread.isAlive()) {

            progress = 0;
            setUpNotification();
            new Thread() {
                public void run() {
                    // 下载
                    startDownload();
                }

                ;
            }.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class DownloadBinder extends Binder {
        public void start() {
            if (downLoadThread == null || !downLoadThread.isAlive()) {

                progress = 0;
                setUpNotification();
                new Thread() {
                    public void run() {
                        // 下载
                        startDownload();
                    }

                    ;
                }.start();
            }
        }

        public void cancel() {
            canceled = true;
        }

        public int getProgress() {
            return progress;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean serviceIsDestroy() {
            return serviceIsDestroy;
        }

        public void cancelNotification() {
            mHandler.sendEmptyMessage(2);
        }


    }

    private void startDownload() {
        // TODO Auto-generated method stub
        canceled = false;
        downloadApk();
    }

    //
    Notification mNotification;

    // 通知栏

    /**
     * 创建通知
     */
    private void setUpNotification() {
        int icon = R.drawable.app_icon;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);
        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_upgradenotifaction);

        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, MainActivity.class);
        // 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
        // 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
        // 是这么理解么。。。
        // intent.setAction(Intent.ACTION_MAIN);
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    //
    /**
     * 下载apk
     *
     * @param url
     */
    private Thread downLoadThread;

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(absolutePath);
        if (!apkfile.exists()) {

            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //7.0文件权限问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //authority  必须和manifest中的一致
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.eight.qihuo.fileprovider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);


//        if (!SafetyUtils.checkPagakgeName(mContext, absolutePath)) {
//            Toast.makeText(mContext, "升级包被恶意软件篡改 请重新升级下载安装", Toast.LENGTH_SHORT).show();
//            apkfile.delete();
//            return;
//        }


//        switch (SafetyUtils.checkPagakgeSign(mContext, absolutePath)) {
//
//            case SafetyUtils.SUCCESS:
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
//                mContext.startActivity(i);
//                break;
//
//            case SafetyUtils.SIGNATURES_INVALIDATE:
//
//                Toast.makeText(mContext, "升级包安全校验失败 请重新升级", Toast.LENGTH_SHORT).show();
//                apkfile.delete();
//                break;
//
//            case SafetyUtils.VERIFY_SIGNATURES_FAIL:
//                Toast.makeText(mContext, "升级包为盗版应用 请重新升级", Toast.LENGTH_SHORT).show();
//                apkfile.delete();
//                break;
//        }


    }

    private int lastRate = 0;
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                URL url = new URL(upgradeInfo.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                /*File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }*/
                String apkFile = absolutePath;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = progress;
                    if (progress >= lastRate + 1) {
                        mHandler.sendMessage(msg);
                        lastRate = progress;
                    }
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(0);
                        // 下载完了，cancelled也要设置
                        canceled = true;
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!canceled);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

}
