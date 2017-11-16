package com.trade.eight.tools;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;


public class MemoryStatus {
    static final int ERROR = -1;
    private static final String TAG = "MemoryStatus";

    public static String getMntPath(String obj) {
        if (obj == null || obj.trim().length() == 0) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
//			if(obj.endsWith(PreferenceSetting.MEDIAFILE_SUBDIR)){
//				obj = obj.substring(0,obj.lastIndexOf(PreferenceSetting.MEDIAFILE_SUBDIR));
//			}
            return obj;
        }

    }

    static public boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    static public String getAvailableInternalMemorySizeText() {
        return formatSize(getAvailableInternalMemorySize());
    }

    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    static public String getTotalInternalMemorySizeText() {
        return formatSize(getTotalInternalMemorySize());
    }

    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    static public String getAvailableExternalMemorySizeText() {
        return formatSize(getAvailableExternalMemorySize());
    }

    static public long getAvailableExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    static public String getAvailableExternalMemorySizeText(String mntPath) {
        return formatSize(getAvailableExternalMemorySize(mntPath));
    }

    static public long getAvailableExternalMemorySize(String mntPath) {
        try {
            StatFs stat = new StatFs(getMntPath(mntPath));
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return ERROR;
        }
    }

    static public String getAlreadyUsedExternalMemorySizeText() {
        return formatSize(getAlreadyUsedExternalMemorySize());
    }

    static public long getAlreadyUsedExternalMemorySize() {
        return getTotalExternalMemorySize() - getAvailableExternalMemorySize();
    }

    static public String getAlreadyUsedExternalMemorySizeText(String mtnPath) {
        return formatSize(getAlreadyUsedExternalMemorySize(mtnPath));
    }

    static public long getAlreadyUsedExternalMemorySize(String mtnPath) {
        return getTotalExternalMemorySize(mtnPath) - getAvailableExternalMemorySize(mtnPath);
    }

    static public String getTotalExternalMemorySizeText() {
        return formatSize(getTotalExternalMemorySize());
    }

    static public long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    static public String getTotalExternalMemorySizeText(String mntPath) {
        Log.v(TAG, "mntPath = " + mntPath);
        return formatSize(getTotalExternalMemorySize(mntPath));
    }

    static public long getTotalExternalMemorySize(String mntPath) {
        try {
            StatFs stat = new StatFs(getMntPath(mntPath));
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return ERROR;
        }
    }

    static public String formatSize(long size) {
        String suffix = "B";
        boolean decimal = false;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    decimal = true;
//					size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        String result = "0";
        if (decimal) {
            int commaOffset = resultBuffer.length() - 3;
            resultBuffer.insert(commaOffset, '.');
            result = resultBuffer.substring(0, commaOffset + 2);
        } else {
            result = resultBuffer.toString();
        }
        result += suffix;
        return result;
    }

    static public String formatSize4MB(long size) {
        String suffix = "B";
        Double sizeADouble = 0D;
        String result = "0";
        try {
            if (size >= 1024) {
                suffix = "KB";
                sizeADouble = size / 1024D;
                if (sizeADouble >= 1024) {
                    suffix = "MB";
                    sizeADouble /= 1024;
                    if (sizeADouble >= 1024) {
                        suffix = "GB";
                        sizeADouble /= 1024;
                    }
                }
            }
            sizeADouble = ConvertUtil.round(sizeADouble, 1);
            if (sizeADouble.toString().endsWith(".0")) {
                result = sizeADouble.toString().substring(0, sizeADouble.toString().indexOf(".0"));
            } else {
                result = sizeADouble.toString();
            }
        } catch (Exception e) {
            return result + suffix;
        }
        return result + suffix;
    }


}
