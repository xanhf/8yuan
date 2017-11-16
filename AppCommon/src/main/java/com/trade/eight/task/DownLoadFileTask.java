package com.trade.eight.task;

import android.content.Context;
import android.os.AsyncTask;

import com.trade.eight.moudle.listener.FileLoadListener;
import com.trade.eight.tools.FileOperator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadFileTask {

    private Context activity;

    public DownLoadFileTask(Context context) {
        this.activity = context;
    }

    public void downloadImage(String imageUrl, FileLoadListener listener) {
        new DownLoadImageTask(imageUrl, listener).execute("");
    }


    class DownLoadImageTask extends AsyncTask<String, Void, String> {

        private FileLoadListener listener;

        private String imageUrl;

        private File file;

        public DownLoadImageTask(String imageUrl, FileLoadListener listener) {
            this.imageUrl = imageUrl;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (listener != null)
                listener.onLoadingStarted();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            FileOutputStream fos = null;
            InputStream is = null;
            file = FileOperator.getImageByUrl(activity, imageUrl);
            if (file == null)
                return null;
            else if (file.exists()) {
                return "succ";
            }

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                is = con.getInputStream();
                byte[] bs = new byte[32 * 1024];
                // 读取到的数据长度
                int len = -1;
                fos = new FileOutputStream(file);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    fos.write(bs, 0, len);
                    fos.flush();
                }
                return "succ";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    fos = null;
                    is = null;
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if ("succ".equals(result)) {
                if (listener != null)
                    listener.onLoadingComplete();
            } else {
                if (listener != null)
                    listener.onLoadingFailed();
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            if (listener != null)
                listener.onLoadingCancelled();
        }

    }


}
