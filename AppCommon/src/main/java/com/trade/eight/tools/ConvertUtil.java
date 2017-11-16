package com.trade.eight.tools;

import java.text.NumberFormat;
import java.util.List;

public class ConvertUtil {
    final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '+', '/',
    };

    /**
     * 将 long[] 转换成 Long[]
     *
     * @param values
     * @return Long[]
     */
    public static Long[] getLongArray(long[] values) {
        if (values == null) {
            return null;
        }

        Long[] result = new Long[values.length];
        for (int i = 0, length = values.length; i < length; i++) {
            result[i] = new Long(values[i]);
        }

        return result;
    }

    /**
     * 将 String 转换成 Long[]
     *
     * @param values
     * @return Long[]
     */
    public static Long[] getLongArray(String values) {
        if (values == null || values.length() == 0) {
            return null;
        }

        String[] strValues = values.split(",");
        Long[] result = new Long[strValues.length];
        for (int i = 0, length = strValues.length; i < length; i++) {
            result[i] = new Long(Long.parseLong(strValues[i]));
        }

        return result;
    }

    /**
     * 将 valueList 转换成 Long[]
     *
     * @param valueList
     * @return Long[]
     */
    public static Long[] getLongArray(List valueList) {
        if (valueList == null || valueList.size() == 0) {
            return null;
        }

        Long[] result = new Long[valueList.size()];
        for (int i = 0, length = valueList.size(); i < length; i++) {
            result[i] = (Long) valueList.get(i);
        }

        return result;
    }

    /**
     * 将 valueList 转换成 long[]
     *
     * @param valueList
     * @return Long[]
     */
    public static long[] getLittleLongArray(List valueList) {
        if (valueList == null || valueList.size() == 0) {
            return null;
        }

        long[] result = new long[valueList.size()];
        for (int i = 0, length = valueList.size(); i < length; i++) {
            result[i] = ((Long) valueList.get(i)).longValue();
        }

        return result;
    }

    /**
     * 将 逗号分隔的 long型数据转换成 long[]
     *
     * @param value
     * @return long[]
     */
    public static long[] getLittleLongArray(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        String[] values = value.split(",");
        long[] result = new long[values.length];

        for (int i = 0, length = values.length; i < length; i++) {
            result[i] = Long.parseLong(values[i]);
        }

        return result;
    }

    /**
     * 将 List 转换成 1,aa,2
     *
     * @param valueList
     * @return Long[]
     */
    public static String getStringValueSplitByComma(List valueList) {
        if (valueList == null || valueList.size() == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0, size = valueList.size(); i < size; i++) {
            sb.append(",").append(valueList.get(i).toString());
        }

        if (sb.length() > 0) {
            sb.delete(0, 1);
        }

        return sb.toString();
    }

    /**
     * 将 long[] 转换成 Long[]
     *
     * @param values
     * @return Long[]
     */
    public static String getStringValueSplitByComma(long[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = values.length; i < length; i++) {
            sb.append(",").append(values[i]);
        }

        if (sb.length() > 0) {
            sb.delete(0, 1);
        }

        return sb.toString();
    }

    /**
     * 字符串按字节截取
     *
     * @param str   原字符
     * @param len   截取长度
     * @param elide 省略符
     * @return String
     * @since 2006.07.20
     */

    public static String subString(String str, int len, String elide) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }

        str = str.trim();
        byte[] strByte = str.getBytes();
        int strLen = strByte.length;
        int elideLen = (elide.trim().length() == 0) ? 0 : elide.getBytes().length;

        if (len >= strLen || len < 1) {
            return str;
        }

        if (len - elideLen > 0) {
            len = len - elideLen;
        }

        int count = 0;
        for (int i = 0; i < len; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }

        if (count % 2 != 0) {
            len = (len == 1) ? len + 1 : len - 1;
        }

        return new String(strByte, 0, len) + elide.trim();
    }

    public static String NVL(Object obj, String value) {
        try {
            if (obj == null) {
                return value;
            }
            if (String.valueOf(obj).trim().equals("")) {
                return value;
            }
            if (String.valueOf(obj).trim().equalsIgnoreCase("null")) {
                return value;
            }
            return String.valueOf(obj);
        } catch (Exception e) {
            return value;
        }
    }

    public static long NVL(Object obj, long value) {
        try {
            if (obj == null) {
                return value;
            }
            if (String.valueOf(obj).trim().equals("")) {
                return value;
            }
            if (String.valueOf(obj).trim().equalsIgnoreCase("null")) {
                return value;
            }
            return Long.parseLong(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static int NVL(Object obj, int value) {
        try {
            if (obj == null) {
                return value;
            }
            if (String.valueOf(obj).trim().equals("")) {
                return value;
            }
            if (String.valueOf(obj).trim().equalsIgnoreCase("null")) {
                return value;
            }
            return Integer.parseInt(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * 把10进制的数字转换成64进制
     *
     * @param number
     * @param shift
     * @return
     */
    public static String CompressNumber(long number, int shift) {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << shift;
        long mask = radix - 1;
        do {
            buf[--charPos] = digits[(int) (number & mask)];
            number >>>= shift;
        } while (number != 0);
        return new String(buf, charPos, (64 - charPos));
    }

    /**
     * 把64进制的字符串转换成10进制
     *
     * @param decompStr
     * @return
     */
    public static long UnCompressNumber(String decompStr) {
        long result = 0;
        for (int i = decompStr.length() - 1; i >= 0; i--) {
            if (i == decompStr.length() - 1) {
                result += getCharIndexNum(decompStr.charAt(i));
                continue;
            }
            for (int j = 0; j < digits.length; j++) {
                if (decompStr.charAt(i) == digits[j]) {
                    result += ((long) j) << 6 * (decompStr.length() - 1 - i);
                }
            }
        }
        return result;
    }

    /**
     * @param ch
     * @return
     */
    private static long getCharIndexNum(char ch) {
        int num = ((int) ch);
        if (num >= 48 && num <= 57) {
            return num - 48;
        } else if (num >= 97 && num <= 122) {
            return num - 87;
        } else if (num >= 65 && num <= 90) {
            return num - 29;
        } else if (num == 43) {
            return 62;
        } else if (num == 47) {
            return 63;
        }
        return 0;
    }

    public static double round(double v, int scale) {
        String temp = "#,##0.";
        for (int i = 0; i < scale; i++) {
            temp += "0";
        }
        return Double.valueOf(new java.text.DecimalFormat(temp).format(v));
    }

    /**
     * 计算两个整数所占的百分比
     * @param one
     * @param two
     * @return
     */
    public static int calculateTwoIntPercent(int one,int two){
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        String result = numberFormat.format((float)one/(float)(one+two)*100);
        return Integer.parseInt(result);
    }

}
