package com.edusoho.kuozhi.clean.module.main.mine.prestudyhome;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.adapter.PreSchoolHomeAdapter;
import com.edusoho.kuozhi.clean.api.SpStudyHomeApi;
import com.edusoho.kuozhi.clean.bean.ScheduleBean;
import com.edusoho.kuozhi.clean.bean.studyhome.TrainRoom;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.http.newutils.NewHttpUtils;
import com.edusoho.kuozhi.clean.widget.pop.TimePickPop;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import myutils.FastClickUtils;
import myutils.MyUtils;
import myutils.StatusBarUtil;
import myutils.TimeUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PreStudyActivity extends AppCompatActivity {

    private ImageView img_close;
    //起始时间
    private RelativeLayout rl_select_start_time;
    private TextView tv_start_time;
    //结束时间
    private RelativeLayout rl_select_end_time;
    private TextView tv_end_time;
    //取消/保存
    private TextView tv_cancel, tv_save;
    //信息
    private TextView tv_name;
    private TextView tv_home_address;
    private TextView tv_email;
    //数据
    private String id;
    private String name;
    private String address;
    private String email;
    //弹窗
    private TimePickPop timePickPopStart;
    private PopupWindow popTimePickPopStart;
    private TimePickPop timePickPopEnd;
    private PopupWindow popTimePickPopEnd;
    private LoadDialog mProcessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_pre_study);
        img_close = findViewById(R.id.img_close);
        tv_save = findViewById(R.id.tv_save);
        tv_cancel = findViewById(R.id.tv_cancel);
        rl_select_start_time = findViewById(R.id.rl_select_start_time);
        tv_start_time = findViewById(R.id.tv_start_time);
        rl_select_end_time = findViewById(R.id.rl_select_end_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");
        tv_name = findViewById(R.id.tv_name);
        tv_home_address = findViewById(R.id.tv_home_address);
        tv_email = findViewById(R.id.tv_email);
        tv_name.setText(name);
        tv_home_address.setText(address);
        tv_email.setText(email);
        initClick();
    }

    //入口
    public static void launch(Context context, String name, String address, String email, String id) {
        Intent intent = new Intent(context, PreStudyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        bundle.putString("address", address);
        bundle.putString("email", email);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (timePickPopStart != null) {
                if (popTimePickPopStart != null) {
                    if (popTimePickPopStart.isShowing()) {
                        timePickPopStart.dismissPop();
                        popTimePickPopStart = null;
                        timePickPopStart = null;
                        return true;
                    }
                }
            }
            if (timePickPopEnd != null) {
                if (popTimePickPopEnd != null) {
                    if (popTimePickPopEnd.isShowing()) {
                        timePickPopEnd.dismissPop();
                        popTimePickPopEnd = null;
                        timePickPopEnd = null;
                        return true;
                    }
                }
            }
            return super.onKeyDown(keyCode, event);//super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event);
    }

    //----------------------------------工具类----------------------------------
    private void initClick() {
        //返回
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                finish();
            }
        });

        //起始时间
        rl_select_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    Toast.makeText(PreStudyActivity.this, "选择起始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                timePickPopStart = new TimePickPop(PreStudyActivity.this);
                popTimePickPopStart = timePickPopStart.showPop(new TimePickPop.ClickCallback() {
                    @Override
                    public void selectTime(String time) {
                        tv_start_time.setText(time);
                    }
                }, true, tv_start_time.getText().toString(), tv_end_time.getText().toString());
//                MyUtils.showDealDatePickerDialogTex(PreStudyActivity.this,
//                        0,
//                        tv_start_time,
//                        Calendar.getInstance(Locale.CHINA),
//                        true,
//                        tv_start_time.getText().toString(),
//                        tv_end_time.getText().toString());
            }
        });

        //结束时间
        rl_select_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    Toast.makeText(PreStudyActivity.this, "选择起始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                timePickPopEnd = new TimePickPop(PreStudyActivity.this);
                popTimePickPopEnd = timePickPopEnd.showPop(new TimePickPop.ClickCallback() {
                    @Override
                    public void selectTime(String time) {
                        tv_end_time.setText(time);
                    }
                }, false, tv_start_time.getText().toString(), tv_end_time.getText().toString());
//                MyUtils.showDealDatePickerDialogTex(PreStudyActivity.this,
//                        0,
//                        tv_end_time,
//                        Calendar.getInstance(Locale.CHINA),
//                        false,
//                        tv_start_time.getText().toString(),
//                        tv_end_time.getText().toString());
            }
        });

        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                Toast.makeText(PreStudyActivity.this, "取消", Toast.LENGTH_SHORT).show();
            }
        });

        //保存
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                if (tv_start_time.getText().toString().contains("请选择") || tv_end_time.getText().toString().contains("请选择")) {
                    Toast.makeText(PreStudyActivity.this, "请选择预定教室的时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                schedule();
            }
        });
    }

    private void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    private void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    //----------------------------------接口----------------------------------
    private void schedule() {
        String format = "yyyy-MM-dd HH:mm:ss";
        String longStartTime = (TimeUtils.getTimeStamp(tv_start_time.getText().toString().trim(), format) + "").substring(0, 10);
        String longEndTime = (TimeUtils.getTimeStamp(tv_end_time.getText().toString().trim(), format) + "").substring(0, 10);
        showProcessDialog();
        NewHttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .setBaseUrl(NewHttpUtils.BASE_NEW_API_URL)
                .createApi(SpStudyHomeApi.class)
                .Schedule(id, longStartTime, longEndTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<ScheduleBean>() {

                    @Override
                    public void onError(String message) {
                        hideProcessDialog();
                        Toast.makeText(PreStudyActivity.this, "message", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ScheduleBean bean) {
                        hideProcessDialog();
                        if (bean.getCode() == 0) {
                            Toast.makeText(PreStudyActivity.this, "预定教室成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(PreStudyActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
