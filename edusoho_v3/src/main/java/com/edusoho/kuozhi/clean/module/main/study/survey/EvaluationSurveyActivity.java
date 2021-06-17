package com.edusoho.kuozhi.clean.module.main.study.survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.EvaluationAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/5.
 */

public class EvaluationSurveyActivity extends SurveyDetailActivity {

    private EvaluationAnswer mEvaluationAnswer = new EvaluationAnswer();
    private int mCourseId;
    private int mTaskId;

    public static void launch(Context context, String surveyId, int courseId, int taskId) {
        Intent intent = new Intent();
        intent.putExtra("survey_id", surveyId);
        intent.putExtra("course_id", courseId);
        intent.putExtra("task_id", taskId);
        intent.setClass(context, EvaluationSurveyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getIntent().getIntExtra("course_id", 0);
        mTaskId = getIntent().getIntExtra("task_id", 0);
    }

    @Override
    protected void initView() {
        super.initView();
        tvtoolbartitle.setText("教学评价");
    }

    @Override
    protected void initData() {
        super.initData();
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EvaluationSurveyActivity.this.finish();
            }
        });
    }

    @Override
    public void refreshView(SurveyModel surveyModel) {
        mSurveyModel = surveyModel;
        mSurveyResultId = surveyModel.getSurveyResult().getId();
        for (SurveyModel.QuestionnaireItemsBean question : mSurveyModel.getQuestionnaireItems()) {
            if (question.getType().equals("evaluationQuestion")) {
                List<String> initList = new ArrayList<>();
                for (SurveyModel.QuestionnaireItemsBean.MetasBean metasBean : question.getMetas()) {
                    initList.add("0");
                }
                mEvaluationAnswer.getAnswers().add(new EvaluationAnswer.AnswersBean(question.getId(), initList));
            }
        }
        mAdapter = new EvaluationAdapter(this, mSurveyModel);
        mAdapter.setHeaderView(LayoutInflater.from(this).inflate(R.layout.item_survey_header, rvsurveydetail, false));
        rvsurveydetail.setAdapter(mAdapter);
    }

    @Override
    protected View.OnClickListener submitSurveyClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkAnswer()) {
                    return;
                }
                mPresenter.submitEvaluation(mSurveyResultId, mEvaluationAnswer, mCourseId, mTaskId);
            }
        };
    }

    private boolean checkAnswer() {
        int compulsoryCount = 0;
        List<String> unFinished = new ArrayList<>();
        List<String> finished = new ArrayList<>();
        for (int index = 0; index < mSurveyModel.getQuestionnaireItems().size(); index ++) {
            SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(index);
            if (question.getIsOptional().equals("0")) {
                compulsoryCount += 1;
                for (int i = 0; i < mEvaluationAnswer.getAnswers().size(); i++) {
                    EvaluationAnswer.AnswersBean answer = mEvaluationAnswer.getAnswers().get(i);
                    if (answer.getQuestionnaireItemId().equals(question.getId())) {
                        if (question.getType().equals("blankFillingQuestion")) {
                            finished.add(Integer.toString(index + 1));
                            break;
                        }
                        if (question.getType().equals("evaluationQuestion")) {
                            for (int count = 0; count < answer.getAnswer().size(); count ++) {
                                String answerString = answer.getAnswer().get(count);
                                if (answerString.equals("0")) {
                                    unFinished.add(Integer.toString(index + 1));
                                    break;
                                }
                                if (count == answer.getAnswer().size() - 1) {
                                    finished.add(Integer.toString(index + 1));
                                }
                            }
                        }
                    } else {
                        if (i == mEvaluationAnswer.getAnswers().size() - 1) {
                            if (question.getType().equals("blankFillingQuestion")) {
                                unFinished.add(Integer.toString(index + 1));
                            }
                        }
                    }
                }
            }
        }
        if (finished.size() == compulsoryCount) {
            return true;
        } else {
            showToast(String.format("请填写第%s题", unFinished.get(0)));
            rvsurveydetail.moveToPosition(Integer.parseInt(unFinished.get(0)) - 1);
            return false;
        }
    }

    private void changeAnswer(SurveyModel.QuestionnaireItemsBean question, int index, int i) {
        EvaluationAnswer newAnswer = mEvaluationAnswer;
        if (question.getType().equals("evaluationQuestion")) {
            for (EvaluationAnswer.AnswersBean answersBean : newAnswer.getAnswers()) {
                if (answersBean.getQuestionnaireItemId().equals(question.getId())) {
                    answersBean.getAnswer().set(index, Integer.toString(i + 1));
                }
            }
        }
        if (question.getType().equals("blankFillingQuestion")) {
            for (EvaluationAnswer.AnswersBean answersBean : mEvaluationAnswer.getAnswers()) {
                if (answersBean.getQuestionnaireItemId().equals(question.getId())) {
                    newAnswer.getAnswers().remove(answersBean);
                    break;
                }
            }
            if (TextUtils.isEmpty(question.getBlankContent())) {
                return;
            }
            newAnswer.getAnswers().add(new EvaluationAnswer.AnswersBean(question.getId(), Arrays.asList(question.getBlankContent())));
        }
        mEvaluationAnswer = newAnswer;
    }



    private class EvaluationAdapter extends SurveyDetailAdapter {

        private EvaluationAdapter(Context context, SurveyModel model) {
            super(context, model);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER) {
                return new SurveyDetailHeaderHolder(mHeaderView);
            } else {
                if (viewType == TYPE_NORMAL) {
                    View view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_choice, parent, false);
                    return new SurveyDetailChoiceHolder(view);
                } else {
                    View view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_blank_fill, parent, false);
                    return new SurveyDetailBlankFillHolder(view, new EvaluationEditTextListener());
                }
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                SurveyDetailHeaderHolder headerHolder = (SurveyDetailHeaderHolder) holder;
                headerHolder.mSurveyTitle.setText(mSurveyModel.getSurvey().getTitle());
                if (mSurveyModel.getSurvey().getIsAnonymous().equals("0")) {
                    headerHolder.mSurveyAnonymousTip.setVisibility(View.GONE);
                } else {
                    headerHolder.mSurveyAnonymousTip.setVisibility(View.VISIBLE);
                }
                headerHolder.mSurveyDescription.setText(mSurveyModel.getSurvey().getDescription());
            } else {
                final int realPosition = getRealPosition(holder);
                SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(realPosition);
                if (question.getType().equals("blankFillingQuestion")) {
                    SurveyDetailBlankFillHolder blankFillHolder = (SurveyDetailBlankFillHolder) holder;
                    blankFillHolder.mBlankStem.setText(String.format("Q%d： %s", position, question.getStem()));
                    blankFillHolder.myCustomEditTextListener.updatePosition(realPosition);
                    blankFillHolder.mBlankEdit.setText(question.getBlankContent());
                } else if (question.getType().equals("evaluationQuestion")){
                    SurveyDetailChoiceHolder choiceHolder = (SurveyDetailChoiceHolder) holder;
                    choiceHolder.mChoiceStem.setText(String.format("Q%d：%s", position, question.getStem()));
                    showChoices(choiceHolder, question, realPosition);
                }
            }
        }

        @Override
        protected void showChoices(SurveyDetailChoiceHolder holder, SurveyModel.QuestionnaireItemsBean question, int position) {
            holder.mChoiceList.removeAllViews();
            for (int index = 0; index < question.getMetas().size(); index++) {
                View view = LayoutInflater.from(mContex).inflate(R.layout.item_evaluation_choice_singal, holder.mChoiceList, false);
                LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(-1, 0);
                params.weight = 1;
                view.setLayoutParams(params);
                TextView choiceTitle = (TextView) view.findViewById(R.id.tv_evaluation_meta_title);
                LinearLayout innerList = (LinearLayout) view.findViewById(R.id.ll_evaluation_inner_list);
                SurveyModel.QuestionnaireItemsBean.MetasBean meta = question.getMetas().get(index);
                choiceTitle.setText(String.format("* %s", meta.getName()));
                for (int i = 0; i < 5; i++) {
                    final View innerView = LayoutInflater.from(mContex).inflate(R.layout.item_evaluation_choice_inner_singal, innerList, false);
                    LinearLayout.LayoutParams innerParams =  new LinearLayout.LayoutParams(0, -1);
                    innerParams.weight = 1;
                    innerView.setLayoutParams(innerParams);
                    TextView innerOrder = (TextView) innerView.findViewById(R.id.tv_evaluation_choice_order);
                    TextView innerDesc = (TextView) innerView.findViewById(R.id.tv_evaluation_choice_desc);
                    ESIconView innerIcon = (ESIconView) innerView.findViewById(R.id.icon_check_view);
                    innerIcon.setVisibility(View.GONE);
                    innerOrder.setVisibility(View.VISIBLE);
                    innerDesc.setVisibility(View.VISIBLE);
                    switch (i) {
                        case 0: {
                            innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_grey_30));
                            innerOrder.setText("1");
                            innerDesc.setText("差");
                            break;
                        }
                        case 1: {
                            innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_grey_40));
                            innerOrder.setText("2");
                            innerDesc.setText("较差");
                            break;
                        }
                        case 2: {
                            innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_grey_50));
                            innerOrder.setText("3");
                            innerDesc.setText("一般");
                            break;
                        }
                        case 3: {
                            innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_grey_60));
                            innerOrder.setText("4");
                            innerDesc.setText("好");
                            break;
                        }
                        case 4: {
                            innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_grey_70));
                            innerOrder.setText("5");
                            innerDesc.setText("很好");
                            break;
                        }
                    }
                    if (meta.getSelectOrder() == i + 1) {
                        innerView.setBackground(getResources().getDrawable(R.drawable.shape_item_evaluation_blue));
                        innerOrder.setVisibility(View.GONE);
                        innerDesc.setVisibility(View.GONE);
                        innerIcon.setVisibility(View.VISIBLE);
                    }
                    innerView.setOnClickListener(clickChoice(question, index, i));
                    innerList.addView(innerView);
                }
                holder.mChoiceList.addView(view);
            }
        }

        private View.OnClickListener clickChoice(final SurveyModel.QuestionnaireItemsBean question, final int index, final int i) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SurveyModel.QuestionnaireItemsBean.MetasBean meta = question.getMetas().get(index);
                    meta.setSelectOrder(i + 1);
                    changeAnswer(question, index, i);
                    notifyDataSetChanged();
                }
            };
        }

        private class EvaluationEditTextListener extends MyCustomEditTextListener {

            @Override
            public void afterTextChanged(Editable editable) {
                changeAnswer(mSurveyModel.getQuestionnaireItems().get(position), 0 ,0);
            }
        }
    }

}
