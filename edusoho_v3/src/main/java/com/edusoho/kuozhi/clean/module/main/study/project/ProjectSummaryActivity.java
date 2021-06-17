package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.component.ESImageGetter;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectSummaryActivity extends BaseActivity {


    private android.widget.TextView tvprojectsummary;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;

    public static void launch(Context context, String summary) {
        Intent intent = new Intent();
        intent.putExtra("summary", summary);
        intent.setClass(context, ProjectSummaryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_summary);
        initView();
    }

    private void initView() {
        this.tvprojectsummary = (TextView) findViewById(R.id.tv_project_summary);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectSummaryActivity.this.finish();
            }
        });
        String summary = getIntent().getStringExtra("summary");
        tvprojectsummary.setText(Html.fromHtml(summary, new ESImageGetter(this, tvprojectsummary), null));
    }
}
