package com.edusoho.kuozhi.clean.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import myutils.FastClickUtils;
import myutils.TimeUtils;
import top.defaults.view.DateTimePickerView;

public class TimePickPop {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private String timeSelect = "";

    public TimePickPop(Context baseActivity) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback, final boolean isLeft, final String startTime, final String endTime) {
        this.clickCallback = clickCallback;
        final Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.pop_layout_time_pic, null, false);

            //确定
            TextView tv_take_pic = popView.findViewById(R.id.tv_take_pic);
            tv_take_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    if (TimePickPop.this.clickCallback != null) {
                        if (timeSelect.equals("")) {
                            Toast.makeText(baseActivity, "请选择您需要预定的时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String format = "yyyy-MM-dd HH:mm:ss";
                        long longTodayTime = getTimeStamp(TimeUtils.getTimeTodayYMDHMS(), format);
                        long longStartTime = (!startTime.contains("请选择")) ? getTimeStamp(startTime, format) : -1;
                        long longEndTime = (!endTime.contains("请选择")) ? getTimeStamp(endTime, format) : -1;
                        long currnetSelectTime = getTimeStamp(timeSelect, format);
                        Log.e("时间查看: ", "longTodayTime=" + longTodayTime);
                        Log.e("时间查看: ", "longStartTime=" + longStartTime);
                        Log.e("时间查看: ", "longEndTime=" + longEndTime);
                        Log.e("时间查看: ", "currnetSelectTime=" + currnetSelectTime);
                        //左侧不能低于今天，不能大于右边时间
                        if (isLeft) {
                            if (currnetSelectTime < longTodayTime) {
                                Toast.makeText(baseActivity, "起始时间不能小于当天时间哦~", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (longEndTime != -1 && currnetSelectTime > longEndTime) {
                                Toast.makeText(baseActivity, "起始时间不能大于结束时间哦~", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        //右侧不能低于今天，不能小于左侧时间
                        else {
                            if (currnetSelectTime < longTodayTime) {
                                Toast.makeText(baseActivity, "结束时间不能小于当天时间哦~", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (longStartTime != -1 && currnetSelectTime < longStartTime) {
                                Toast.makeText(baseActivity, "结束时间不能小于起始时间哦~", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        dismissPop();
                        TimePickPop.this.clickCallback.selectTime(timeSelect);
                    }
                }
            });

            //时间选择
            final TextView tv_time = popView.findViewById(R.id.tv_time);
            DateTimePickerView datePickerView = popView.findViewById(R.id.datePickerView);
            datePickerView.setStartDate(Calendar.getInstance());
            String timeNow = TimeUtils.getTimeTodayYMDHMS_Split();
            String timeNowArr[] = timeNow.split("-");
            //yyyy - MM - dd - HH - mm - ss
            datePickerView.setSelectedDate(new GregorianCalendar(Integer.parseInt(timeNowArr[0]),
                    Integer.parseInt(timeNowArr[1])-1,
                    Integer.parseInt(timeNowArr[2]),
                    Integer.parseInt(timeNowArr[3]),
                    Integer.parseInt(timeNowArr[4])));// 注意：月份是从0开始计数的
            datePickerView.setOnSelectedDateChangedListener(new DateTimePickerView.OnSelectedDateChangedListener() {
                @Override
                public void onSelectedDateChanged(Calendar date) {
                    int year = date.get(Calendar.YEAR);
                    int month = date.get(Calendar.MONTH);
                    int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
                    int hour = date.get(Calendar.HOUR_OF_DAY);
                    int minute = date.get(Calendar.MINUTE);
                    timeSelect = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", year, month + 1, dayOfMonth, hour, minute);
                    tv_time.setText("已选择:" + timeSelect);
                }
            });

            //-----------------------------------------取消
            ImageView img_cancel = popView.findViewById(R.id.img_cancel);
            img_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TimePickPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });
            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TimePickPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });

            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            //点击外部，popupwindow会消失
            popWindow.setFocusable(false);
            popWindow.setOutsideTouchable(false);
//            popWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            popWindow.showAtLocation(popView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            showPopBlackBg(baseActivity);
        }
        return popWindow;
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

    //----------------------------------------------背景颜色的改变----------------------------------
    //初始化弹窗的背景色
    public void showPopBlackBg(Context baseActivity) {
        if (baseActivity != null) {
            changePopBlackBg(0.8F);//越大越浅
        }
    }

    //恢复弹窗的背景色
    public void backNormalPopBg() {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = 1f;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    //弹窗背景颜色
    public void changePopBlackBg(float blackBg) {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = blackBg;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    public void dismissPop() {
        if (popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
            backNormalPopBg();
        }
    }


    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void selectTime(String time);
    }
}
