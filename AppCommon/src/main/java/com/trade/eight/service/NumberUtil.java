package com.trade.eight.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by fangzhu on 2015/2/6.
 */
public class NumberUtil {



    /**
     * 仅仅针对报价所用的
     * 最多三位小数   120.0  120.01 120.123
     * 如果上千(>= 1000)并且是整数就显示整数部分 3224 3224.2
     *
     * @param d
     * @return
     */
    public static String beautifulDouble(double d) {
        String str = String.valueOf(d);
        try {
            if (str.contains(".")) {
                String s = str.split("\\.")[1];
                int length = s.length();
                if (length > 3) {
                    BigDecimal big = new BigDecimal(d);
                    double res = big.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                    DecimalFormat df = new DecimalFormat("######0.000");
                    return df.format(res);
                }
                if (d >= 1000) {
                    //小数部分是0
                    if (Integer.parseInt(s) == 0) {
                        return (int) d + "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d + "";
    }

    public static String beautifulDouble(double d, int scale) {
        try {
            BigDecimal big = new BigDecimal(d);
            double res = big.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
            return String.format("%." + scale + "f", res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d + "";
    }

    /**
     * 四舍五入 保留有效两位数
     * @param d
     * @param scale
     * @return
     */
    public static String roundUp(double d, int scale) {
        try {
            BigDecimal big = new BigDecimal(d);
            double res = big.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
            return String.format("%." + scale + "f", res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d + "";
    }
    public static double roundUpDouble(double d, int scale) {
        try {
            BigDecimal big = new BigDecimal(d);
            double res = big.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }
    public static double roundDownDouble(double d, int scale) {
        try {
            BigDecimal big = new BigDecimal(d);
            double res = big.setScale(scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 解决double 计算的小数点错误
     *
     * @param num
     * @return
     */
    public static BigDecimal getBigDecimal(double num) {
        return new BigDecimal(Double.toString(num));
    }

    /**
     * 加法
     *
     * @param num1
     * @param num2
     * @return
     */
    public static double add(double num1, double num2) {
        BigDecimal big1 = getBigDecimal(num1);
        BigDecimal big2 = getBigDecimal(num2);
        return big1.add(big2).doubleValue();
    }

    /**
     * 减法
     *
     * @param num1
     * @param num2
     * @return
     */
    public static double subtract(double num1, double num2) {
        BigDecimal big1 = getBigDecimal(num1);
        BigDecimal big2 = getBigDecimal(num2);
        return big1.subtract(big2).doubleValue();
    }

    /**
     * 乘法
     *
     * @param num1
     * @param num2
     * @return
     */
    public static double multiply(double num1, double num2) {
        BigDecimal big1 = getBigDecimal(num1);
        BigDecimal big2 = getBigDecimal(num2);
        return big1.multiply(big2).doubleValue();
    }

    /**
     * 除法
     *
     * @param num1
     * @param num2
     * @return
     */
    public static double divide(double num1, double num2) {
        BigDecimal big1 = getBigDecimal(num1);
        BigDecimal big2 = getBigDecimal(num2);
        return big1.divide(big2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static BigDecimal divide(String num1, String num2, int scal) {
        return new BigDecimal(num1).divide(new BigDecimal(num2), scal, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 舍去后面小数
     * @param num1
     * @param num2
     * @param scal
     * @return
     */
    public static BigDecimal divideRoundDown(String num1, String num2, int scal) {
        return new BigDecimal(num1).divide(new BigDecimal(num2), scal, BigDecimal.ROUND_DOWN);
    }


    /**
     * 去掉无效小数点
     * 去掉小数点最后补齐的0
     * 100.01 ＝ 100.01
     * 100.10 ＝ 100.1
     * 100.00 ＝ 100
     * 100 ＝ 100
     * @param d
     * @return
     */
    public static String moveLast0(double d) {
        String str = String.valueOf(d);
        return moveLast0(str);
    }

    public static String moveLast0(String str) {
        try {
            if (str == null)
                return "";
            BigDecimal bigDecimal = new BigDecimal(str);
            str = bigDecimal.toString();
            if (str.contains(".")) {
                String[] ss = str.split("\\.");
                if (Integer.parseInt(ss[1]) > 0) {
                    if (ss[1].endsWith("0")) {
                        int index = ss[1].lastIndexOf("0");
                        str = ss[0] + "."+ss[1].substring(0, index);
                        if (str.endsWith("0")) {
                            return moveLast0(str);
                        }
                    } else {
                        str = ss[0] + "." + ss[1];
                    }
                } else {
                    str = ss[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取小数点之后有几位小数点
     *
     * @param d
     * @return 小数点有效位数
     */
    public static int getPointPow(String str) {
        try {
            //去科学计数发
            BigDecimal bigDecimal = new BigDecimal(str);
            str = bigDecimal.toString();
            if (str.contains(".")) {
                String[] ss = str.split("\\.");
                return ss[1].length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        double d = multiply(0.6, 3);
        System.out.println("dd=" + d);
    }
}
