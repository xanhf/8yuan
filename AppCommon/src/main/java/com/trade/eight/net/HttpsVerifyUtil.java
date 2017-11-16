package com.trade.eight.net;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by fangzhu on 2017/1/9.
 * https跳过验证
 */

public class HttpsVerifyUtil {

    /**
     * 跳过https的ssl
     * 在 URL url = new URL(path); 之后调用
     * @param api
     */
    public static void verifySSL(String api) {
        if (api == null)
            return;
        //不是https，就不用处理
        if (!api.startsWith("https"))
            return;
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        // 注意这部分一定要 
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                //这里直接true，跳过就可以
                return true;
            }
        });
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
