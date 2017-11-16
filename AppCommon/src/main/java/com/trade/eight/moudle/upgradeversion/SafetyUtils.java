package com.trade.eight.moudle.upgradeversion;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/12.
 * 升级包验证
 */

public class SafetyUtils {
    /**
     * install sucess
     */
    protected static final int SUCCESS = 0;
    /**
     * SIGNATURES_INVALIDATE
     */
    protected static final int SIGNATURES_INVALIDATE = 3;
    /**
     * SIGNATURES_NOT_SAME
     */
    protected static final int VERIFY_SIGNATURES_FAIL = 4;
    /**
     * is needcheck
     */
    private static final boolean NEED_VERIFY_CERT = true;

    /**
     * checkPagakgeSigns.
     */
    public static int checkPagakgeSign(Context context, String srcPluginFile) {

        PackageInfo PackageInfo = context.getPackageManager().getPackageArchiveInfo(srcPluginFile, 0);
        //Signature[] pluginSignatures = PackageInfo.signatures;
        Signature[] pluginSignatures = collectCertificates(context, srcPluginFile);
        boolean isDebugable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        if (pluginSignatures == null) {
            Log.e("签名验证失败", srcPluginFile);
            new File(srcPluginFile).delete();
            return SIGNATURES_INVALIDATE;
        } else if (NEED_VERIFY_CERT && !isDebugable) {
            //可选步骤，验证APK证书是否和现在程序证书相同。
            Signature[] mainSignatures = null;
            try {
                PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                mainSignatures = pkgInfo.signatures;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (!isSignaturesSame(mainSignatures, pluginSignatures)) {
                Log.e("升级包证书和旧版本证书不一致", srcPluginFile);
                new File(srcPluginFile).delete();
                return VERIFY_SIGNATURES_FAIL;
            }
        }
        return SUCCESS;
    }

    /**
     * checkPagakgeName
     *
     * @param context
     * @param srcNewFile
     * @return
     */
    public static boolean checkPagakgeName(Context context, String srcNewFile) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(srcNewFile, PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {

            return TextUtils.equals(context.getPackageName(), packageInfo.packageName);
        }
        return false;
    }

    /**
     * checkFile
     *
     * @param aPath   文件路径
     * @param context context
     */
    public static boolean checkFile(String aPath, Context context) {
        File aFile = new File(aPath);
        if (aFile == null || !aFile.exists()) {
            Toast.makeText(context, "安装包已被恶意软件删除", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (context == null) {
            Toast.makeText(context, "安装包异常", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 获取签名
     *
     * @param context
     * @param srcNewFile
     * @return
     */
    static Signature[] collectCertificates(Context context, String srcNewFile) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(srcNewFile, PackageManager.GET_SIGNATURES);

        if (packageInfo != null) {

            return packageInfo.signatures;
        }
        return null;
    }

    /**
     * 验证签名是否一致
     *
     * @param me
     * @param download
     * @return
     */
    static boolean isSignaturesSame(Signature[] me, Signature[] download) {
        if (me == null) {
            return false;
        }
        if (download == null) {
            return false;
        }
        HashSet<Signature> set1 = new HashSet<Signature>();
        for (Signature sig : me) {
            set1.add(sig);
        }
        HashSet<Signature> set2 = new HashSet<Signature>();
        for (Signature sig : download) {
            set2.add(sig);
        }
        //  Make sure s2 contains all signatures in s1.
        if (set1.equals(set2)) {
            return true;
        }
        return false;
    }
}
