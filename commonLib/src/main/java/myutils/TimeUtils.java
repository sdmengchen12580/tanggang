package myutils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeUtils {

    public static String getTimeToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getTimeToday_() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getTimeTodayYMD() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static long getTimeStamp(String strTime, String strFormat) {
        strTime = strTime.trim();
        SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
        Date theDate = null;
        try {
            theDate = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        return theDate.getTime();
    }

    public static String getTimeTodayYMDHMS() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String timeToYMDHMS(String time) {
        Date currentTime = new Date(Long.parseLong(time));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getTimeTodayYMDHMS_Split() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static List<String> getDateList(int maxShowDay) {
        //获取今天日期
        String today = getTimeToday_();
        //添加格式  2020-1-截至日期
        List<String> list = new ArrayList<>();
        //本月展示到多少号
        int year = Integer.parseInt(today.split("-")[0]);
        int month = Integer.parseInt(today.split("-")[1]);
        int day = Integer.parseInt(today.split("-")[2]);
        int de_curr_month_day = maxShowDay - day;
        today = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        list.add(today);
        //计算上一个月展示日期的初始时间
        while (de_curr_month_day > 0) {
            month--;
            if (month == 0) {
                month = 12;
                year--;
            }
            String leftMonth = year + "-" + (month < 10 ? "0" + month : month) + "-01";
            int leftMonthDay = getMontyDay(leftMonth);
            int leftDay = de_curr_month_day - leftMonthDay;
            if (leftDay >= 0) {
                de_curr_month_day = leftDay;
                list.add(leftMonth);
            } else {
                de_curr_month_day = -1;
                String StartDay = year + "-" + (month < 10 ? "0" + month : month) + "-" + ((Math.abs(leftDay) + 1) < 10 ? "0" + (Math.abs(leftDay) + 1) : (Math.abs(leftDay) + 1));
                list.add(StartDay);
            }
        }
        return list;
    }

    //获取每月多少天 getMontyDay("2018-06-07")
    public static int getMontyDay(String dateTime) {
        String data[] = dateTime.split("-");
        int day = 0;
        if (data.length >= 2) {
            int year = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1]);
            /*平年的2月是28天,闰年2月是29天.
            4月、6月、9月、11月各是30天..
            1月、3月、5月、7月、8月、10月、12月各是31天*/
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30;
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                day = 31;
            } else {
                if (year % 4 == 0) {
                    day = 29;
                } else {
                    day = 28;
                }
            }
        }
        return day;
    }

    //将集合元素倒置
    public static List<String> listOrder(List<String> list) {
        List<String> list1 = new ArrayList<>();
        if (list.size() > 1) {
            for (int i = list.size() - 1; i >= 0; i--) {
                list1.add(list.get(i));
            }
        } else {
            list1 = list;
        }
        return list1;
    }

    //获取指定日期是星期几  getDayofWeek("2018-06-07")   返回星期日
    public static int getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    //替换当前月的第一天 getMontyDay("2018-06-07")
    public static String getcurFirstDay(String dateTime) {
        String data[] = dateTime.split("-");
        int year = 0;
        int month = 0;
        if (data.length == 3) {
            year = Integer.parseInt(data[0]);
            month = Integer.parseInt(data[1]);
        }
        return year + "-" + month + "-01";
    }

    /**
     * 获取选中月的日期天
     *
     * @param date 起始天
     * @return
     */
    public static List<String> getchooseDays(String date) {
        List<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(date) || !date.contains("-")) {
            return list;
        }
        int week = 0;//空白个数
        week = TimeUtils.getDayofWeek(TimeUtils.getcurFirstDay(date));//获取空白的个数  FIXME 注意：周日=0  周一到周六为：1~6
        for (int i = 0; i < week; i++) {
            list.add("");
        }
        String dateArr[] = date.split("-");
        String year = dateArr[0];
        String month = dateArr[1];
        String day = dateArr[2];
        int monthDay = TimeUtils.getMontyDay(date);//获取本月天数
        int needShowMonthDay = monthDay - Integer.parseInt(day) + 1;
        for (int i = 0; i < needShowMonthDay; i++) {
            list.add((Integer.parseInt(day) + i) + "");
        }
        return list;
    }

    /**
     * 获取选中月的日期天
     *
     * @param lastdate 最后一天
     * @return
     */
    public static List<String> getchooseLastDays(String lastdate) {
        List<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(lastdate) || !lastdate.contains("-")) {
            return list;
        }
        int week = 0;//空白个数
        week = TimeUtils.getDayofWeek(TimeUtils.getcurFirstDay(lastdate));//获取空白的个数  FIXME 注意：周日=0  周一到周六为：1~6
        for (int i = 0; i < week; i++) {
            list.add("");
        }
        String dateArr[] = lastdate.split("-");
        String year = dateArr[0];
        String month = dateArr[1];
        String day = dateArr[2];
        int needShowMonthDay = Integer.parseInt(day);
        for (int i = 0; i < needShowMonthDay; i++) {
            list.add((1 + i) + "");
        }
        return list;
    }

    /***
     * 转long时间戳
     * @param date
     * @return 分钟数
     */
    public static long getLongOfHM(String date) {
        if (TextUtils.isEmpty(date) || !date.contains(":")) {
            return -1;
        }
        String dateArr[] = date.split(":");
        String hour = dateArr[0];
        String min = dateArr[1];
        return Integer.parseInt(hour) * 60 + Integer.parseInt(min);
    }

    /**
     * 获取日期天
     *
     * @param date 2020-07-07
     * @return 07
     */
    public static String getDay(String date) {
        if (TextUtils.isEmpty(date) || !date.contains("-")) {
            return "00";
        }

        String day = "00";
        String[] strings = date.split("-");

        if (strings.length > 2) {
            day = strings[2];
        }
        return day;
    }

}
