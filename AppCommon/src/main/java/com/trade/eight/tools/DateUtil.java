package com.trade.eight.tools;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final long millisInDay = 86400000;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("EEE, MMM d, yyyyy hh:mm:ss aa z");
    public static final DateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS z");
    private static final SimpleDateFormat mFormat8chars =
            new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat mFormatWeeks = new SimpleDateFormat("ww");
    private static final SimpleDateFormat mFormatDayInWeek = new SimpleDateFormat("EE");
    private static final SimpleDateFormat mFormatYear = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat mFormatNoYear = new SimpleDateFormat("MM-dd HH:mm");

    private static SimpleDateFormat formatter;

    public static int getWeek(Date date) {
        int week = Integer.parseInt(mFormatWeeks.format(date));
        String dayInWeek = mFormatDayInWeek.format(date);
        if (dayInWeek.toUpperCase().equals("������") || dayInWeek.toUpperCase().equals("SUNDAY")) {
            week = week - 1;
        }
        return week;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 星期日  0
     * 星期一  1
     *
     * @param dt
     * @return
     */
    public static int getDayOfWeek(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return w;
    }

    public static int getYear(Date date) {
        return Integer.parseInt(mFormatYear.format(date));
    }

    public static String shortDate(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(aDate);
    }

    public static Date getEndOfDay(Date day) {
        return getEndOfDay(day, Calendar.getInstance());
    }

    public static Date getEndOfDay(Date day, Calendar cal) {
        if (day == null) day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public static Date getNoonOfDay(Date day, Calendar cal) {
        if (day == null) day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public static SimpleDateFormat get8charDateFormat() {
        return DateUtil.mFormat8chars;
    }

    public static String mailDate(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyyMMddHHmm");
        return formatter.format(aDate);
    }

    public static String longDate(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(aDate);
    }

    public static String shortDateGB(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyy'��'MM'��'dd'��'");
        return formatter.format(aDate);
    }

    /**
     * Returns a Date set to the first possible millisecond of the day, just
     * after midnight. If a null day is passed in, a new Date is created.
     * midnight (00m 00h 00s)
     */
    public static Date getStartOfDay(Date day) {
        return getStartOfDay(day, Calendar.getInstance());
    }

    /**
     * Returns a Date set to the first possible millisecond of the day, just
     * after midnight. If a null day is passed in, a new Date is created.
     * midnight (00m 00h 00s)
     */
    public static Date getStartOfDay(Date day, Calendar cal) {
        if (day == null) day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public static String longDateGB(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(aDate);
    }

    public static String longDateLog(Date aDate) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(aDate);
    }

    public static String formatDate(Date aDate, String formatStr) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat(formatStr);
        return formatter.format(aDate);

    }

    public static String LDAPDate(Date aDate) {
        if (aDate == null)
            return "";
        return formatDate(aDate, "yyyyMMddHHmm'Z'");
    }

    public static Date getDate(String yyyymmdd) {
        if (yyyymmdd == null) return null;
        int year = Integer.parseInt(yyyymmdd.substring(0, yyyymmdd.indexOf("-")));
        int month = Integer.parseInt(yyyymmdd.substring(yyyymmdd.indexOf("-") + 1, yyyymmdd.lastIndexOf("-")));
        int day = Integer.parseInt(yyyymmdd.substring(yyyymmdd.lastIndexOf("-") + 1));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();

    }

    public static Date getDateByMillisecond(String millisecond) {
        if (millisecond == null || millisecond.trim().length() == 0 || millisecond.equals("undefined")) {
            return null;
        }
        try {
            Date date = new Date(Long.parseLong(millisecond));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parser(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parser(String strDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parser24(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parser24NoSecond(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }


    public static String getTime(Date aDate, String dateformat) {
        if (aDate == null)
            return "";
        formatter = new SimpleDateFormat(dateformat);
        return formatter.format(aDate);
    }

    public static String formatedate(String befTime, String befformat, String targetformat) {
        if (befTime == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(befformat);
        try {
            Date time = sdf.parse(befTime);
            return formatDate(time, targetformat);
        } catch (Exception e) {
            return "";
        }


    }


    public static Date getShortDate(String date) {
        Date shortDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            shortDate = formatter.parse(date);
        } catch (Exception e) {
            shortDate = null;
        }
        return shortDate;
    }

    public static boolean equals(Date date1, Date date2) {
        if (date1 == null && date2 == null)
            return true;
        if (date1 == null && date2 != null)
            return false;
        if (date1 != null && date2 == null)
            return false;
        return date1.equals(date2);
    }

    /**
     * @return java.util.Date
     */
    public static Date tomorrow() {
        Calendar calender = Calendar.getInstance();
        calender.roll(Calendar.DAY_OF_YEAR, true);
        return calender.getTime();
    }

    /**
     * @param date
     * @return java.util.Date
     */
    public static Date nextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.roll(Calendar.DAY_OF_YEAR, 1);
        if (isEndOfYear(date, cal.getTime())) {
            cal.roll(Calendar.YEAR, true);
            cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    /**
     * @param curDate
     * @param rollUpDate
     * @return boolean
     */
    private static boolean isEndOfYear(Date curDate, Date rollUpDate) {
        return (curDate.compareTo(rollUpDate) >= 0);
    }

    /**
     * @return java.util.Date
     */
    public static Date yesterday() {
        Calendar calender = Calendar.getInstance();
        calender.roll(Calendar.DAY_OF_YEAR, false);
        return calender.getTime();
    }

    /**
     * @param aDate
     * @return String
     */
    private static final String getDateFormat(Date aDate) {
        if (aDate == null)
            return null;
        SimpleDateFormat formatter
                = new SimpleDateFormat("M/d/yyyy");
        return formatter.format(aDate);
    }

    /**
     * @param date
     * @return String
     */
    public static String NVL(Date date) {
        if (date == null)
            return "";
        else
            return getDateFormat(date);
    }

    /**
     * @param baseDate
     * @param type
     * @param num
     * @return Date
     */
    public static Date addDate(Date baseDate, int type, int num) {
        Date lastDate = null;
        try {
            Calendar cale = Calendar.getInstance();
            cale.setTime(baseDate);
            if (type == 1) {
                cale.add(Calendar.YEAR, num);
            } else if (type == 2) {
                cale.add(Calendar.MONTH, num);
            } else if (type == 3) {
                cale.add(Calendar.DATE, num);
            }
            lastDate = cale.getTime();
            return lastDate;
        } catch (Exception e) {
            return null;
        }

    }

    public static Date getDate(String strDate, String formatter) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getSysDate() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getTheFirstDayOfCurMonth(Date date) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.set(Calendar.DAY_OF_MONTH, 1);
        return calender.getTime();
    }

    public static Date getTheFirstDayOfCurMonth(String date) {
        return getTheFirstDayOfCurMonth(getShortDate(date));
    }

    public static String getTheFirstDayOfCurMonthStr(Date date) {
        return shortDate(getTheFirstDayOfCurMonth(date));
    }

    public static String getTheFirstDayOfCurMonthStr(String date) {
        return shortDate(getTheFirstDayOfCurMonth(date));
    }

    public static Date getTheEndDayOfCurMonth(Date date) {
        Date firstDay = getTheFirstDayOfCurMonth(date);
        Calendar calender = Calendar.getInstance();
        calender.setTime(firstDay);
        calender.roll(Calendar.MONTH, 1);
        calender.roll(Calendar.DAY_OF_YEAR, -1);
        return calender.getTime();
    }

    public static Date getTheEndDayOfCurMonth(String date) {
        return getTheEndDayOfCurMonth(getShortDate(date));
    }

    public static String getTheEndDayOfCurMonthStr(Date date) {
        return shortDate(getTheEndDayOfCurMonth(date));
    }

    public static String getTheEndDayOfCurMonthStr(String date) {
        return shortDate(getTheEndDayOfCurMonth(date));
    }

    public static int getDateSpan(Date beginDate, Date endDate, int calType) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(beginDate);
        int[] p1 = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)};
        cal.clear();
        cal.setTime(endDate);
        int[] p2 = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)};
        int[] s = {p2[0] - p1[0], p2[0] * 12 + p2[1] - p1[0] * 12 - p1[1], (int) ((endDate.getTime() - beginDate.getTime()) / (24 * 3600 * 1000))};
        if (calType <= 3 || calType >= 1) {
            return s[calType - 1];
        } else {
            return 0;
        }
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String formatDate(String content_createtime, String format) {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String systemTime = sdf.format(new Date()).toString();
        try {
            Date begin = sdf.parse(content_createtime);
            Date end = sdf.parse(systemTime);
            long second = (end.getTime() - begin.getTime()) / 1000L;
            long l = 60L * 1000L;
            long l1 = (end.getTime() - begin.getTime()) / l; // 转换成分钟
            long hl = 60L * 60L * 1000L;
            long h1 = (end.getTime() - begin.getTime()) / hl;   //转换成小时
            if (second < 60) {
                if (second < 3) {
                    date = ("刚刚");
                } else {
                    date = (second + "秒前");
                }
            } else if (l1 < 60 && l1 > 0) {
                date = (l1 + "分钟前");
            } else if (h1 < 24 && h1 > 0) {               //24小时前
                date = (h1 + "小时前");
            } else if (h1 < 48 && h1 >= 24) {               //48小时前
                date = "昨天";
            } else {
                String month = String.format("%tm", begin);
                String day = String.format("%td", begin);
                String hour = String.format("%tH", begin);
                String minutes = String.format("%tM", begin);
                date = month + "-" + day + " " + hour + ":"
                        + minutes;
            }
        } catch (ParseException e) {
            return date;
        }
        return date;

    }


    public static String formatDate(long content_createtime) {
        String date = "";
        try {
            long begin = content_createtime;
            long end = System.currentTimeMillis();
            long second = (end - begin) / 1000L;
            long l = 60L * 1000L;
            long l1 = (end - begin) / l; // 转换成分钟
            long hl = 60L * 60L * 1000L;
            long h1 = (end - begin) / hl;   //转换成小时
            long dl = 24 * 60L * 60L * 1000L;
            long d1 = (end - begin) / dl;   //转换成天
            long ml = 30 * 24 * 60L * 60L * 1000L;
            long m1 = (end - begin) / ml;   //转换成月
            long yl = 12 * 30 * 24 * 60L * 60L * 1000L;
            long y1 = (end - begin) / yl;   //转换成年
            if (m1 < 12) {
                if (second < 60) {
                    if (second < 3) {
                        date = ("刚刚");
                    } else {
                        date = (second + "秒前");
                    }
                } else if (l1 < 60 && l1 > 0) {
                    date = (l1 + "分钟前");
                } else if (h1 < 24 && h1 > 0) {               //24小时前
                    date = (h1 + "小时前");
                } else if (d1 < 31 && d1 > 0) {               //30天前
                    date = (d1 + "天前");
                } else if (m1 < 13 && m1 > 0) {               //12小时前
                    date = (m1 + "个月前");
                } else {
                    date = y1 + "年前";
                }
            } else {
                date = y1 + "年前";
            }
        } catch (Exception e) {
            return date;
        }
        return date;

    }

    public static String formatDate(String time1, String format, String targetFormat) {
        String targettime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date1 = sdf.parse(time1);
            SimpleDateFormat targetsdf = new SimpleDateFormat(targetFormat);
            targettime = targetsdf.format(date1);
        } catch (ParseException e) {
            return null;
        }
        return targettime;
    }

    /**
     * 观看时间转换
     *
     * @param playedTime
     * @return
     */
    public static String parsePlayedTime(int playedTime) {
        String parsePt = "";
        int second = playedTime / 1000;
        int minute = 0;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        String seconds = String.valueOf(second);
        if (second < 10) {
            seconds = "0" + second;
        }
        String minutes = String.valueOf(minute);
        if (minute < 10) {
            minutes = "0" + minute;
        } else if (minute == 0) {
            minutes = "00";
        }
        parsePt = minutes + ":" + seconds;
        return parsePt;
    }

    /**
     * 与当前时间比较，如果晚于当前时间则返回true
     *
     * @param time1
     * @param currenttime
     * @return
     */
    public static boolean compareTime(String time1, String currenttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = sdf.parse(time1);
            Date currentDate = sdf.parse(currenttime);
            if (date1.before(currentDate) || date1.equals(currentDate)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取日期中的月份
     *
     * @param strDate
     * @return
     */
    public static String getMonthFromStr(String strDate) {
        String month = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = sdf.parse(strDate);
            month = String.valueOf(dt.getMonth() + 1);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return month;
    }

    /**
     * 获取日期中的年份
     *
     * @param strDate
     * @return
     */
    public static String getYearFromStr(String strDate) {
        String year = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = sdf.parse(strDate);
            year = String.valueOf(getYear(dt));
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return year;
    }

    /**
     * 获取两个日期之间的月份间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getMonthBetweenDate(String date1, String date2) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        try {
            d1 = sd.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = null;
        try {
            d2 = sd.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int months = 0;//相差月份
        int y1 = d1.getYear();
        int y2 = d2.getYear();
        if (d1.getTime() < d2.getTime()) {
            months = d2.getMonth() - d1.getMonth() + (y2 - y1) * 12 + 1;
        }
        return months;
    }

    public static long parseTime4Long(String time) {
        long datetime = -1;
        Date date = parser24(time);
        if (date != null) {
            datetime = date.getTime();
        }
        return datetime;
    }


    public static long dateToLong(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * 时分秒转成字符串
     *
     * @param time
     * @return
     */
    public static String getHms(long time) {
        String hms = "";
        long hour = 0l, minutes = 0l, seconds = 0l;
        hour = time / 3600;
        minutes = (time / 60) % 60;
        seconds = time % 60;
        hms = String.format("%02d:%02d:%02d", hour, minutes, seconds);
        return hms;
    }

    public static String[] getDateTime(String time) {
        String[] day = new String[2];

        Date date2 = parser24NoSecond(time);

        int month = date2.getMonth();
        int date = date2.getDate();
        int hour = date2.getHours();
        int minute = date2.getMinutes();
        day[0] = String.format("%02d-%02d", (month + 1), date);
        day[1] = String.format("%02d:%02d", hour, minute);

        return day;
    }

    public static int compareDay(String time) {
        int diff = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date begindate = formatter.parse(time);
            Date end = new Date();
            int ey = end.getYear();
            int by = begindate.getYear();
            int ed = end.getDate();
            int bd = begindate.getDate();
            if (ey == by) {
                diff = ed - bd;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /**
     * 获取时间戳的时分
     *
     * @param millis
     * @return
     */
    public static String parseMillisSecondToHHmm(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date(millis);
        return formatter.format(date);
    }

    /**
     * 获取当前日期的MM月dd日
     *
     * @return
     */
    public static String getCurrentMMdd() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * 获取首页资讯的时间 今天返回 HH:mm  今天以前的返回MM-dd HH:mm
     *
     * @return
     */
    public static String getHomeInformationTime(long millisTime) {

        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = simpleDateFormat.format(date);
            if (simpleDateFormat.parse(dateStr).getTime() > millisTime) {// 今天的时间戳大于创建时间
                return mFormatNoYear.format(new Date(millisTime));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseMillisSecondToHHmm(millisTime);
    }


    /**
     * 获取时间戳比当前时间多x天x小时x分钟
     *
     * @param content_createtime
     * @return
     */
    public static String[] formatDateDecent(long content_createtime) {
        String[] date = new String[]{"00", "00", "00"};
        long dl = 24 * 60L * 60L;
        long dayDecent = content_createtime / dl;// 时间戳的天数差距

        long hl = 60L * 60L;
        long hourDecent = (content_createtime - (dayDecent * dl)) / hl;// 时间戳的小时差距

        long minute = 60L;
        long minuteDecent = (content_createtime - (dayDecent * dl) - (hourDecent * hl)) / minute;// 时间戳的分钟差距


        if (dayDecent > 0) {
            if (dayDecent > 9) {
                date[0] = dayDecent + "";
            } else {
                date[0] = "0" + dayDecent;
            }
        }

        if (hourDecent > 0) {
            if (hourDecent > 9) {
                date[1] = hourDecent + "";
            } else {
                date[1] = "0" + hourDecent;
            }
        }

        if (minuteDecent > 0) {
            if (minuteDecent > 9) {
                date[2] = minuteDecent + "";
            } else {
                date[2] = "0" + minuteDecent;
            }
        }
        return date;
    }

    /**
     * 获取时间戳比当前时间多x时x分x秒
     *
     * @param content_createtime
     * @return
     */
    public static String[] formatDateDecentHMS(long content_createtime) {
        String[] date = new String[]{"00", "00", "00"};

        long hl = 60L * 60L;
        long hourDecent = content_createtime / hl;// 时间戳的小时差距

        long minute = 60L;
        long minuteDecent = (content_createtime - (hourDecent * hl)) / minute;// 时间戳的分钟差距

        long secondDecent = content_createtime - (hourDecent * hl) - (minuteDecent * minute);// 时间戳的秒差距

        if (hourDecent > 0) {
            if (hourDecent > 9) {
                date[0] = hourDecent + "";
            } else {
                date[0] = "0" + hourDecent;
            }
        }

        if (minuteDecent > 0) {
            if (minuteDecent > 9) {
                date[1] = minuteDecent + "";
            } else {
                date[1] = "0" + minuteDecent;
            }
        }

        if (secondDecent > 0) {
            if (secondDecent > 9) {
                date[2] = secondDecent + "";
            } else {
                date[2] = "0" + secondDecent;
            }
        }
        return date;
    }

    /**
     * 将时间年月日替换成当天
     *
     * @param millis
     * @return
     */
    public static long changeDateYMD(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ss_old = sdf.format(new Date(millis));
        String ss_current = sdf.format(new Date(System.currentTimeMillis()));

        String ss_new = ss_old.replace(ss_old.substring(0, 10), ss_current.substring(0, 10));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(ss_new));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTimeInMillis();
    }

    /**
     * 和当前时间做比较
     *
     * @param content_createtime
     * @return
     */
    public static boolean compareDateWithNow(String content_createtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(content_createtime);
            Date currentDate = new Date();
            if (date1.before(currentDate) || date1.equals(currentDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

    }

    public static void main(String[] args) {
        Log.d("test", compareDateWithNow("") + "");
        Log.d("test", compareDateWithNow("-") + "");
        Log.d("test", compareDateWithNow("_") + "");
        Log.d("test", compareDateWithNow("长期") + "");
        Log.d("test", compareDateWithNow("2017-06-20") + "");

        Log.d("test", compareDateWithNow("2017-06-21") + "");
        Log.d("test", compareDateWithNow("2017-06-22") + "");


    }
}

