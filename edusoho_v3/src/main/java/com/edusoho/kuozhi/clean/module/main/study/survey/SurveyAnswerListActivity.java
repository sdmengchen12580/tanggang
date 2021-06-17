package com.edusoho.kuozhi.clean.module.main.study.survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswersModel;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.List;

/**
 * Created by RexXiang on 2018/2/1.
 */

public class SurveyAnswerListActivity extends BaseActivity<SurveyAnswerListContract.Presenter> implements SurveyAnswerListContract.View {

    private static int LIMIT = 20;

    private String mSurveyId;
    private String mQuestionId;
    private String mQuestionStem;
    private LoadDialog mProcessDialog;
    private int mOffset = 0;
    private SurveyAnswerListAdapter mAdapter;
    private SurveyAnswersModel mAnswersModel;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.TextView tvquestionstem;
    private com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView rvsurveyanswerlist;

    public static void launch(Context context, String surveyId, String questionId, String questionStem) {
        Intent intent = new Intent();
        intent.putExtra("survey_id", surveyId);
        intent.putExtra("question_id", questionId);
        intent.putExtra("question_stem", questionStem);
        intent.setClass(context, SurveyAnswerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_answer_list);
        mSurveyId = getIntent().getStringExtra("survey_id");
        mQuestionId = getIntent().getStringExtra("question_id");
        mQuestionStem = getIntent().getStringExtra("question_stem");
        initView();
        initData();
    }

    private void initView() {
        this.rvsurveyanswerlist = (ESPullAndLoadRecyclerView) findViewById(R.id.rv_survey_answer_list);
        this.tvquestionstem = (TextView) findViewById(R.id.tv_question_stem);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
        tvquestionstem.setText(mQuestionStem);
    }

    private void initData() {
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurveyAnswerListActivity.this.finish();
            }
        });
        mPresenter = new SurveyAnswerListPresenter(this, mSurveyId, mQuestionId);
        mPresenter.subscribe();
        rvsurveyanswerlist.setLinearLayout();
        rvsurveyanswerlist.setPullRefreshEnable(false);
        rvsurveyanswerlist.setPushRefreshEnable(true);
        rvsurveyanswerlist.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPresenter.getMoreQuestionnaireAnswers(LIMIT, mOffset);
            }
        });
    }

    @Override
    public void refreshView(SurveyAnswersModel answersModel) {
        mAnswersModel = answersModel;
        mAdapter = new SurveyAnswerListAdapter(SurveyAnswerListActivity.this, mAnswersModel.getData());
        rvsurveyanswerlist.setAdapter(mAdapter);
    }

    @Override
    public void loadMoreData(SurveyAnswersModel answersModel) {
        mAdapter.addNewData(answersModel.getData());
    }

    @Override
    public void refreshCompleted() {
        rvsurveyanswerlist.setPullLoadMoreCompleted();
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

    private class SurveyAnswerListAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<SurveyAnswersModel.DataBean> mList;

        private SurveyAnswerListAdapter(Context context, List<SurveyAnswersModel.DataBean> list) {
            mContext = context;
            mList = list;
        }

        private void addNewData(List<SurveyAnswersModel.DataBean> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_survey_result_blank, parent, false);
            return new SurveyAnswerListHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SurveyAnswersModel.DataBean dataBean = mList.get(position);
            holder = (SurveyAnswerListHolder) holder;
            ((SurveyAnswerListHolder) holder).mAnswerContent.setText(dataBean.getAnswer().get(0));
        }

        private class SurveyAnswerListHolder extends RecyclerView.ViewHolder {


            TextView mAnswerContent;

            private SurveyAnswerListHolder(View view) {
                super(view);
                mAnswerContent = (TextView) view.findViewById(R.id.tv_survey_result_blank);
            }
        }
    }
}
