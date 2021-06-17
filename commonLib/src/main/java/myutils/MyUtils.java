package myutils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyUtils {

    //安全地显示短时吐司
    public static void showShort(Context context, final CharSequence text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    //安全地显示短时吐司
    public static void showShort(Context context, final int resId) {
        Toast.makeText(context.getApplicationContext(), context.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void showDealDatePickerDialogTex(final Activity activity, int themeResId, final TextView tv, Calendar calendar,
                                                   final boolean isLeft, final String startTime, final String endTime) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long longTodayTime = getTimeStamp(TimeUtils.getTimeTodayYMD(), "yyyy年MM月dd日");
                long longStartTime = (!startTime.equals("请选择")) ? getTimeStamp(startTime, "yyyy年MM月dd日") : -1;
                long longEndTime = (!endTime.equals("请选择")) ? getTimeStamp(endTime, "yyyy年MM月dd日") : -1;
                long currnetSelectTime = getTimeStamp(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日", "yyyy年MM月dd日");
                Log.e("时间查看: ", "longTodayTime=" + longTodayTime + "     TimeUtils.getTimeTodayYMD()=" + TimeUtils.getTimeTodayYMD());
                Log.e("时间查看: ", "longStartTime=" + longStartTime + "     startTime=" + startTime);
                Log.e("时间查看: ", "longEndTime=" + longEndTime + "     endTime=" + endTime);
                Log.e("时间查看: ", "currnetSelectTime=" + currnetSelectTime + "     ==" + (year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日"));
                //左侧不能低于今天，不能大于右边时间
                if (isLeft) {
                    if (currnetSelectTime < longTodayTime) {
                        showShort(activity, "起始时间不能小于当天时间哦~");
                        return;
                    } else if (longEndTime != -1 && currnetSelectTime > longEndTime) {
                        showShort(activity, "起始时间不能大于结束时间哦~");
                        return;
                    }
                }
                //右侧不能低于今天，不能小于左侧时间
                else {
                    if (currnetSelectTime < longTodayTime) {
                        showShort(activity, "结束时间不能小于当天时间哦~");
                        return;
                    } else if (longStartTime != -1 && currnetSelectTime < longStartTime) {
                        showShort(activity, "结束时间不能小于起始时间哦~");
                        return;
                    }
                }
                // 此处得到选择的时间，可以进行你想要的操作
                tv.setText(year + "年" + changeadd0((monthOfYear + 1)) + "月" + changeadd0(dayOfMonth) + "日");
//                tv.setTextColor(Color.parseColor("#333333"));
                if ((isLeft && !endTime.equals("请选择")) || (!isLeft) && !startTime.equals("请选择")) {
//                    Log.e("时间查看",
//                            "isLeft=" + isLeft +
//                                    "     currnetSelectTime=" + currnetSelectTime +
//                                    "     longStartTime=" + longStartTime +
//                                    "     longEndTime=" + longEndTime
//                    );
                    //计算相隔天数
//                    tv_time_num.setText(isLeft ?
//                            getGapCount(
//                                    new Date(currnetSelectTime),
//                                    new Date(longEndTime))
//                            :
//                            getGapCount(
//                                    new Date(longStartTime),
//                                    new Date(currnetSelectTime))
//                    );
                }
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
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

    //获取两个日期之间的间隔天数
    public static String getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        int time = (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
        return (time + 1) + "天";
    }

    //0-9加0
    public static String changeadd0(int num) {
        String str = "";
        if (num <= 9) {
            str = "0" + num;
        } else {
            str = num + "";
        }
        return str;
    }
}
