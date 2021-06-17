package com.edusoho.kuozhi.clean.module.main.study.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamResultModel;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by RexXiang on 2018/2/9.
 */

public class ExamResultActivity extends BaseActivity<ExamResultContract.Presenter> implements ExamResultContract.View {

    private LoadDialog mProcessDialog;
    private String mResultId;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.TextView tvexamresultscore;


    public static void launch(Context context, String resultId) {
        Intent intent = new Intent();
        intent.putExtra("result_id", resultId);
        intent.setClass(context, ExamResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        mResultId = getIntent().getStringExtra("result_id");
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().postSticky(new MessageEvent<>(MessageEvent.FINISH_SURVEY));
    }

    private void initView() {
        this.tvexamresultscore = (TextView) findViewById(R.id.tv_exam_result_score);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
    }

    private void initData() {
        mPresenter = new ExamResultPresenter(this, mResultId);
        mPresenter.subscribe();
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamResultActivity.this.finish();
            }
        });
    }

    @Override
    public void refreshView(ExamResultModel resultModel) {
        if (resultModel.getExamType().equals("grade")) {
            tvexamresultscore.setText(String.format("本次得分为：%s", resultModel.getScore()));
        }
    }


    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
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
}
