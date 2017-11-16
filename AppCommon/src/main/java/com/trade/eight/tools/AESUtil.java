package com.trade.eight.tools;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 * 注意这里的base64  不要用android自带的，有不一样的地方
 * 使用android包自带的base64 出现不能打印出加密后的字符串，但是能打印出解密的字符串
 *
 * 这里用到的key 不直接显示，也是base64加密之后的值，用的时候base64解密
 */
public class AESUtil {
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     * 真实值
     */
//    private static final String sKey = "5fjyU#Mvyfp[^CMx";
//    private static final String ivParameter = "i%hRCnu~~.B#y+Y(";

    /*不将key直接暴露，这里写的是先用base64加密，代码中用的时候再去解密*/
    private static final String sKey = "NWZqeVUjTXZ5ZnBbXkNNeA==";
    private static final String ivParameter = "aSVoUkNudX5+LkIjeStZKA==";


    public static String encrypt(String encData, String secretKey, String vector) throws Exception {

        if (secretKey == null) {

            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return MyBase64.encode(encrypted);// 此处使用BASE64做转码。
    }


    // 加密
    public static String encrypt(String sSrc) throws Exception {
        return encrypt(sSrc, new String(MyBase64.decode(sKey)), new String(MyBase64.decode(ivParameter)));// 此处使用BASE64做转码。
    }

    // 解密
    public static String decrypt(String sSrc) throws Exception {
        try {
            return decrypt(sSrc, new String(MyBase64.decode(sKey)), new String(MyBase64.decode(ivParameter)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static String decrypt(String sSrc, String key, String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = MyBase64.decode(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuilder strBuf = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static void main(String[] args) throws Exception {
        // 需要加密的字串
        String cSrc = "123456";

        // 加密
        long lStart = System.currentTimeMillis();
        String enString = AESUtil.encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
        String DeString = AESUtil.decrypt("3jspQ5tsnDDGDVkcsCV+ZQ==");
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

}