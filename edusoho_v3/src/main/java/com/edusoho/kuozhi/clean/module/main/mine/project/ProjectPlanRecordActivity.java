package com.edusoho.kuozhi.clean.module.main.mine.project;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.TrainingRecordItem;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.main.mine.project.item.ProjectPlanRecordItemActivity;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProjectPlanRecordItem;

import java.util.List;

public class ProjectPlanRecordActivity extends BaseActivity<ProjectPlanRecordContract.Presenter> implements ProjectPlanRecordContract.View {

    private ESIconView              esivBack;
    private ESProjectPlanRecordItem esppriProjectItem;
    private ESProjectPlanRecordItem esppriLearn;
    private ESProjectPlanRecordItem esppriTest;
    private ESProjectPlanRecordItem esppriActivity;
    private TrainingRecordItem      mTrainingRecordItem;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ProjectPlanRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_plan_record);
        initView();
        mPresenter = new ProjectPlanRecordPresenter(this);
        mPresenter.subscribe();
    }

    private void initView() {
        esivBack = findViewById(R.id.iv_back);
        esppriProjectItem = findViewById(R.id.esppri_item);
        esppriLearn = findViewById(R.id.esppri_learn);
        esppriTest = findViewById(R.id.esppri_test);
        esppriActivity = findViewById(R.id.esppri_activity);
        esivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showTrainingRecords(List<TrainingRecordItem> trainingRecordItems) {
        for (final TrainingRecordItem trainingRecordItem : trainingRecordItems) {
            switch (trainingRecordItem.getType()) {
                case "projectPlan":
                    esppriProjectItem.setNum(String.format(getString(R.string.project_plan_num),
                            trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum()));
                    esppriProjectItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProjectPlanRecordItemActivity.launch(ProjectPlanRecordActivity.this,
                                    Constants.ProjectPlanItemType.TYPE_PROJECT_PLAN, trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum());
                        }
                    });
                    break;
                case "postCourse":
                    esppriLearn.setNum(String.format(getString(R.string.project_plan_num),
                            trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum()));
                    esppriLearn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProjectPlanRecordItemActivity.launch(ProjectPlanRecordActivity.this,
                                    Constants.ProjectPlanItemType.TYPE_POST_COURSE, trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum());
                        }
                    });
                    break;
                case "exam":
                    esppriTest.setNum(String.format(getString(R.string.project_plan_num),
                            trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum()));
                    esppriTest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProjectPlanRecordItemActivity.launch(ProjectPlanRecordActivity.this,
                                    Constants.ProjectPlanItemType.TYPE_EXAM, trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum());
                        }
                    });
                    break;
                case "offlineActivity":
                    esppriActivity.setNum(String.format(getString(R.string.project_plan_num),
                            trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum()));
                    esppriActivity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProjectPlanRecordItemActivity.launch(ProjectPlanRecordActivity.this,
                                    Constants.ProjectPlanItemType.TYPE_OFFLINEACTIVITY, trainingRecordItem.getFinishedNum(), trainingRecordItem.getAssignNum());
                        }
                    });
                    break;
            }
        }
    }
}
