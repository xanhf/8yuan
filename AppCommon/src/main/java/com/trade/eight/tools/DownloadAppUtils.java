package com.trade.eight.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fangzhu on 2015/7/8.
 */
public class DownloadAppUtils {
    public static final int MSG_DOWNLOAD_START = 17;
    public static final int MSG_DOWNLOAD_DOWNLOADING = 16;
    public static final int MSG_DOWNLOAD_SETMAX = 18;
    public static final int MSG_DOWNLOAD_UPDATE = 19;
    public static final int MSG_DOWNLOAD_FINISH = 20;
    public static final int MSG_DOWNLOAD_ERROR = 21;
    public static final int MSG_DOWNLOAD_ERROR_NETWORK = 22;
    Context context = null;
    /**
     * 下载成功的文件名 不需要后缀.apk
     */
    String fileName = null;
    /**
     * umeng 事件统计的ID
     */
    String onEventId = null;
    boolean forceStop = false;//强制停止
    Dialog appDownloadDialog = null;
    Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            TextView status_tv = null;
            TextView progress_tv = null;
            ProgressBar progressbar = null;
            if (appDownloadDialog != null) {
                status_tv = (TextView) appDownloadDialog.findViewById(R.id.status_tv);
                progress_tv = (TextView) appDownloadDialog.findViewById(R.id.progress_tv);
                progressbar = (ProgressBar) appDownloadDialog.findViewById(R.id.progressbar);
            }

            switch (msg.what) {
                case MSG_DOWNLOAD_SETMAX:
                    if (progressbar != null) progressbar.setMax(msg.arg2);
                    break;

                case MSG_DOWNLOAD_START:
                    if (status_tv != null) status_tv.setText("开始下载...");
                    if (progress_tv != null) progress_tv.setText("0%");
                    break;

                case MSG_DOWNLOAD_UPDATE:
                    if (status_tv != null) status_tv.setText("正在下载...");
                    int downLoadFileSize = msg.arg1;
                    int fileSize = msg.arg2;
                    if (progressbar != null) progressbar.setProgress(downLoadFileSize);
                    float result = 0;
                    if (fileSize <= 0) {
                        result = 0;
                    } else {
                        result = (float) downLoadFileSize * 100 / fileSize;
                    }
                    if (progress_tv != null) progress_tv.setText("" + (int) result + "%");

                    break;

                case MSG_DOWNLOAD_FINISH:
                    try {

                        String task = getSavePath(fileName, MSG_DOWNLOAD_DOWNLOADING);
                        File tmpFile = new File(task);
                        if (tmpFile != null) {
                            tmpFile.renameTo(new File(getSavePath(fileName, MSG_DOWNLOAD_FINISH)));
                        }
                        if (appDownloadDialog != null) {
                            appDownloadDialog.dismiss();
                        }
                        installAPK(fileName);
                        try {
                            if (onEventId != null)
                                MyAppMobclickAgent.onEvent(context, onEventId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case MSG_DOWNLOAD_ERROR:
                    toast("下载失败，请检查您的存储状态和网络链接");
                    break;
                case MSG_DOWNLOAD_ERROR_NETWORK:
                    toast(context.getResources().getString(R.string.network_problem));
                    break;
            }

        }
    };
    public DownloadAppUtils(Context context) {
        this.context = context;
    }

    public void showDownloadDialog(final String url, final String fileName) {
        try {
            //if not init value fileName
            if (this.fileName == null || this.fileName.trim().length() == 0)
                this.fileName = fileName;
            //if set value fileName null
            if (this.fileName == null || this.fileName.trim().length() == 0)
                this.fileName = System.currentTimeMillis() + "";

            appDownloadDialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
            appDownloadDialog.setContentView(R.layout.dialog_app_download);
            appDownloadDialog.setCancelable(false);
            Button doback_btn = (Button) appDownloadDialog.findViewById(R.id.doback_btn);
            if (doback_btn != null) {
                doback_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forceStop = true;
                        appDownloadDialog.dismiss();
                    }
                });
            }
            appDownloadDialog.show();
            new Thread() {
                @Override
                public void run() {
                    try {
                        downloadFile(url, getSavePath(fileName, MSG_DOWNLOAD_START));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private String getDownloadDir() {
        String downloadDirName = new File(PreferenceSetting.getDownloadDir(context)).getPath();
        downloadDirName += File.separator + "app/";
        File downloadDir = new File(downloadDirName);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        return downloadDirName;
    }

    private String getSavePath(String fileName, int status) {
        if (fileName != null && fileName.trim().length() > 0) {
            fileName = fileName.trim();
            if (fileName.equals("null"))
                fileName = System.currentTimeMillis() + "";
        } else
            fileName = System.currentTimeMillis() + "";
        if (status != MSG_DOWNLOAD_FINISH)
            return getDownloadDir() + "task_" + fileName + ".apk";
        return getDownloadDir() + fileName + ".apk";
    }

    private void installAPK(String fileName) {
        try {
            String savepath = getSavePath(fileName, MSG_DOWNLOAD_FINISH);
            String miniType = "application/vnd.android.package-archive";
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            //设置intent的file与MimeType
            intent.setDataAndType(Uri.fromFile(new File(savepath)), miniType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File f = new File(getSavePath(fileName, MSG_DOWNLOAD_FINISH));
                if (f != null && f.isFile()) f.delete();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void downloadFile(String url, final String filepath) throws Exception {
        downloadHandler.sendEmptyMessage(MSG_DOWNLOAD_START);
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        int fileSize = conn.getContentLength();// get file size
        if (fileSize <= 0)
            throw new RuntimeException("无法获知文件大小 ");
        if (is == null) {
            downloadHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR_NETWORK);
            return;
//            throw new RuntimeException("stream is null");
        }

        FileOutputStream fos = new FileOutputStream(filepath);
        byte buf[] = new byte[1024];
        int downLoadFileSize = 0;

        if (fileSize > 0) {
            Message m = downloadHandler.obtainMessage();
            m.arg2 = fileSize;
            m.what = MSG_DOWNLOAD_SETMAX;
            m.sendToTarget();
        }

        do {
            if (forceStop) {
                forceStop = false;
                break;
            }
            int status = MSG_DOWNLOAD_UPDATE;
            int numread = is.read(buf);
            if (numread == -1) {
                //完成
                Message m = downloadHandler.obtainMessage();
                m.what = MSG_DOWNLOAD_FINISH;
                m.sendToTarget();
                break;
            }


            fos.write(buf, 0, numread);
            downLoadFileSize += numread;

            //后台下载 就不需要更新进度条 只在显示dialog的时候更新
            Message m = downloadHandler.obtainMessage();
            m.arg1 = downLoadFileSize;
            m.arg2 = fileSize;
            m.what = MSG_DOWNLOAD_UPDATE;
            m.sendToTarget();

        } while (true);
        try {
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOnEventId() {
        return onEventId;
    }

    public void setOnEventId(String onEventId) {
        this.onEventId = onEventId;
    }
}
