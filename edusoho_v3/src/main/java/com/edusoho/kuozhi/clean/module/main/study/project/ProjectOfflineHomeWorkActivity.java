package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by RexXiang on 2018/3/20.
 */

public class ProjectOfflineHomeWorkActivity extends BaseActivity {


    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.support.v7.widget.Toolbar tbtoolbar;
    private android.widget.TextView tvhomeworkdemand;
    private android.widget.TextView tvhomeworkdeadline;

    public static void launch(Context context, String homeworkTitle, String homeworkDemand, String homeworkDeadline) {
        Intent intent = new Intent();
        intent.putExtra("title", homeworkTitle);
        intent.putExtra("demand", homeworkDemand);
        intent.putExtra("deadline", homeworkDeadline);
        intent.setClass(context, ProjectOfflineHomeWorkActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_offline_home_work);
        initView();
    }

    private void initView() {
        this.tvhomeworkdeadline = (TextView) findViewById(R.id.tv_homework_deadline);
        this.tvhomeworkdemand = (TextView) findViewById(R.id.tv_homework_demand);
        this.tbtoolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectOfflineHomeWorkActivity.this.finish();
            }
        });
        tvhomeworkdemand.setText(getIntent().getStringExtra("demand"));
        tvhomeworkdeadline.setText(String.format("提交截止时间：%s", AppUtil.timeStampToDate(getIntent().getStringExtra("deadline"), "yyyy-MM-dd HH:mm")));
    }
}
