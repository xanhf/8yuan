package com.trade.eight.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.netease.nim.uikit.common.util.file.*;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.trade.eight.app.ServiceException;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.auth.entity.IdCardObj;
import com.trade.eight.net.okhttp.OkHttpFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by fangzhu on 16/3/27.
 */
public class UploadFileUtil {
    static final String TAG = "UploadFileUtil";
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private static final String CHARSET = "utf-8"; // 设置编码
    private int readTimeOut = 10 * 1000; // 读取超时
    private int connectTimeout = 10 * 1000; // 超时时间

    public String toUploadFile(Context context, File file, String requestURL,
                               Map<String, String> param) throws Exception {
        if (!Utils.checkNetWork(context)) {
            return null;
        }
//        Log.v(TAG, "RequestURL=" + requestURL);
//        String result = null;
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//
//        try {
//            URL url = new URL(requestURL);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(readTimeOut);
//            conn.setConnectTimeout(connectTimeout);
//            conn.setDoInput(true); // 允许输入流
//            conn.setDoOutput(true); // 允许输出流
//            conn.setUseCaches(false); // 不允许使用缓存
//            conn.setRequestMethod("POST"); // 请求方式
//            conn.setRequestProperty("Charset", CHARSET); // 设置编码
//            conn.setRequestProperty("connection", "keep-alive");
//            conn.setRequestProperty("user-agent", "Android");
//            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//            /**
//             * 当文件不为空，把文件包装并且上传
//             */
//            dos = new DataOutputStream(conn.getOutputStream());
//            StringBuffer sb = null;
//            String params = "";
//
//            /***
//             * 以下是用于上传参数
//             */
//            if (param != null && param.size() > 0) {
//                Iterator<String> it = param.keySet().iterator();
//                while (it.hasNext()) {
//                    sb = new StringBuffer();
//                    String key = it.next();
//                    String value = param.get(key);
//                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
//                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
//                    sb.append(value).append(LINE_END);
//                    params = sb.toString();
//                    dos.write(params.getBytes());
//                }
//            }
//            sb = new StringBuffer();
//            /**
//             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
//             * filename是文件的名字，包含后缀名的 比如:abc.png
//             */
//            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
//            sb.append("Content-Disposition:form-data; name=\"" + fileKey
//                    + "\"; filename=\"" + "bitmap.png" + "\"" + LINE_END);
//            sb.append("Content-Type:image/png" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
//            sb.append(LINE_END);
//            params = sb.toString();
//            sb = null;
//
//            dos.write(params.getBytes());
//            /**上传文件 文件过大时压缩文件*/
//
//            if (is == null) {
//                return null;
//            }
//
//            byte[] bytes = new byte[1024];
//            int len = 0;
//            int curLen = 0;
//            while ((len = is.read(bytes)) != -1) {
//                curLen += len;
//                dos.write(bytes, 0, len);
//            }
//            is.close();
//
//            dos.write(LINE_END.getBytes());
//            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
//            dos.write(end_data);
//            dos.flush();
//            result = StringUtil.convertStreamToString(conn.getInputStream());
//        } catch (SocketTimeoutException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                    is = null;
//                }
//                if (dos != null) {
//                    dos = null;
//                }
//                if (conn != null) {
//                    conn.disconnect();
//                    conn = null;
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;


        //追加参数
        Map<String, Object> requestMap = new LinkedHashMap<>();
        for (String key : param.keySet()) {
            Object object = param.get(key);
            requestMap.put(key, object.toString());
        }
        requestMap.put("file", file);
        return OkHttpFactory.getInstance().uploadFile(requestURL, requestMap, OkHttpFactory.CONTENT_TYPE_IMG, file.getName());
    }


    /**
     *
     * @param context
     * @param file 文件
     * @param requestURL api地址
     * @param param 参数
     * @param isOrig 是否使用原始图片 false：会压缩图片
     * @param callback 回调
     */
    public void uploadFile(final Context context, final File file,
                           final String requestURL,
                           final Map<String, String> param,
                           final boolean isOrig,
                           final LoadCallBack callback) {
        new AsyncTask<Void, Void, CommonResponse<IdCardObj>>() {
            String localPath = "";
            @Override
            protected CommonResponse<IdCardObj> doInBackground(Void... params) {
                try {
                    File imgFile = file;

                    /**
                     * 压缩图片
                     */
                    if (isOrig) {
                        //压缩图片大小  这里直接使用云信之前用到的  可以考虑使用鲁班压缩
                        String mimeType = com.netease.nim.uikit.common.util.file.FileUtil.getExtensionName(file.getAbsolutePath());
                        imgFile = ImageUtil.getScaledImageFileWithMD5(file, mimeType);
                        if (imgFile == null) {
                            Log.e(TAG, "getScaledImage error");
                            return null;
                        } else {
                            ImageUtil.makeThumbnail(context, imgFile);
                        }
                    }
                    localPath = imgFile.getAbsolutePath();
                    return CommonResponse.fromJson(toUploadFile(context, imgFile, requestURL, param),  IdCardObj.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<IdCardObj> response) {
                if (callback != null) {
                    callback.hand(response, localPath);
                }
            }
        }.execute();
    }

    /**
     * 上传成功
     */
    public interface LoadCallBack  {
        void hand(CommonResponse<IdCardObj> response, String localPath);
    }
}
