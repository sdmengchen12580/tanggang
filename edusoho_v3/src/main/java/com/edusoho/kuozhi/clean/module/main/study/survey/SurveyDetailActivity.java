package com.edusoho.kuozhi.clean.module.main.study.survey;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/30.
 */

public class SurveyDetailActivity extends BaseFinishActivity<SurveyDetailContract.Presenter> implements SurveyDetailContract.View {

    protected String                    mSurveyId;
    protected String                    mSurveyResultId;
    private   LoadDialog                mProcessDialog;
    protected SurveyModel               mSurveyModel;
    protected SurveyDetailAdapter       mAdapter;
    protected ESIconView                ivback;
    protected TextView                  tvtoolbartitle;
    private   Toolbar                   tbtoolbar;
    protected ESPullAndLoadRecyclerView rvsurveydetail;
    private   android.widget.Button     btnsubmitsurvey;
    private SurveyAnswer mSurveyAnswer = new SurveyAnswer();

    public static void launch(Context context, String surveyId) {
        Intent intent = new Intent();
        intent.putExtra("survey_id", surveyId);
        intent.setClass(context, SurveyDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_detail);
        mSurveyId = getIntent().getStringExtra("survey_id");
        initView();
        initData();
    }

    protected void initView() {
        this.btnsubmitsurvey = findViewById(R.id.btn_submit_survey);
        this.rvsurveydetail = findViewById(R.id.rv_survey_detail);
        this.tbtoolbar = findViewById(R.id.tb_toolbar);
        this.tvtoolbartitle = findViewById(R.id.tv_toolbar_title);
        this.ivback = findViewById(R.id.iv_back);
    }

    protected void initData() {
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurveyDetailActivity.this.finish();
            }
        });
        rvsurveydetail.setLinearLayout();
        rvsurveydetail.setPullRefreshEnable(false);
        rvsurveydetail.setPushRefreshEnable(false);
        btnsubmitsurvey.setOnClickListener(submitSurveyClick());
        mPresenter = new SurveyDetailPresenter(this, mSurveyId);
        mPresenter.subscribe();
    }

    @Override
    public void refreshView(SurveyModel surveyModel) {
        mSurveyModel = surveyModel;
        mSurveyResultId = surveyModel.getSurveyResult().getId();
        mAdapter = new SurveyDetailAdapter(this, mSurveyModel);
        mAdapter.setHeaderView(LayoutInflater.from(this).inflate(R.layout.item_survey_header, rvsurveydetail, false));
        rvsurveydetail.setAdapter(mAdapter);
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    @Override
    public void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
        SurveyResultActivity.launch(this, mSurveyId, mSurveyModel.getSurvey().getIsResultVisible().equals("1"));
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

    private void changeAnswer(SurveyModel.QuestionnaireItemsBean question) {
        SurveyAnswer newAnswer = mSurveyAnswer;
        for (SurveyAnswer.AnswersBean answersBean : mSurveyAnswer.getAnswers()) {
            if (answersBean.getQuestionnaireItemId().equals(question.getId())) {
                newAnswer.getAnswers().remove(answersBean);
                break;
            }
        }
        if (question.getType().equals("singleChoice")) {
            for (SurveyModel.QuestionnaireItemsBean.MetasBean metasBean : question.getMetas()) {
                if (metasBean.isSelected()) {
                    newAnswer.getAnswers().add(new SurveyAnswer.AnswersBean(question.getId(), Integer.toString(metasBean.getOrder())));
                    break;
                }
            }
        }
        if (question.getType().equals("multipleChoice")) {
            StringBuffer answer = new StringBuffer("");
            for (SurveyModel.QuestionnaireItemsBean.MetasBean metasBean : question.getMetas()) {
                if (metasBean.isSelected()) {
                    answer.append(metasBean.getOrder());
                    answer.append(",");
                }
            }
            if (answer.length() > 1) {
                answer.deleteCharAt(answer.length() - 1);
            }
            newAnswer.getAnswers().add(new SurveyAnswer.AnswersBean(question.getId(), answer.toString()));
        }
        if (question.getType().equals("blankFillingQuestion")) {
            if (TextUtils.isEmpty(question.getBlankContent())) {
                return;
            }
            newAnswer.getAnswers().add(new SurveyAnswer.AnswersBean(question.getId(), question.getBlankContent()));
        }
        mSurveyAnswer = newAnswer;
    }

    protected View.OnClickListener submitSurveyClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkAnswer()) {
                    return;
                }
                mPresenter.submitSurvey(mSurveyResultId, mSurveyAnswer);
            }
        };
    }

    private boolean checkAnswer() {
        List<String> unFinished = new ArrayList<>();
        List<String> finished = new ArrayList<>();
        int compulsoryCount = 0;
        for (int index = 0; index < mSurveyModel.getQuestionnaireItems().size(); index++) {
            SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(index);
            if (question.getIsOptional().equals("0")) {
                compulsoryCount += 1;
                if (mSurveyAnswer.getAnswers().size() == 0) {
                    showToast(String.format("请填写第%s题", Integer.toString(index + 1)));
                    textAnimation(index + 1);
                    rvsurveydetail.moveToPosition(index);
                    return false;
                }
                for (int i = 0; i < mSurveyAnswer.getAnswers().size(); i++) {
                    SurveyAnswer.AnswersBean answers = mSurveyAnswer.getAnswers().get(i);
                    if (answers.getQuestionnaireItemId().equals(question.getId())) {
                        if (question.getType().equals("blankFillingQuestion")) {
                            finished.add(Integer.toString(index + 1));
                        } else {
                            if (TextUtils.isEmpty(answers.getAnswer())) {
                                unFinished.add(Integer.toString(index + 1));
                            } else {
                                finished.add(Integer.toString(index + 1));
                            }
                        }
                        break;
                    }
                    if (i == mSurveyAnswer.getAnswers().size() - 1) {
                        unFinished.add(Integer.toString(index + 1));
                    }
                }
            }
        }
        if (finished.size() == compulsoryCount) {
            return true;
        } else {
            showToast(String.format("请填写第%s题", unFinished.get(0)));
            rvsurveydetail.moveToPosition(Integer.parseInt(unFinished.get(0)));
            textAnimation(Integer.parseInt(unFinished.get(0)));
            return false;
        }
    }

    private void textAnimation(int position) {
        View view = rvsurveydetail.getLayoutManager().findViewByPosition(position);
        LinearLayout linearLayout = (LinearLayout) view;
        if (linearLayout == null) {
            return;
        }
        for (int index = 0; index < linearLayout.getChildCount(); index++) {
            if (linearLayout.getChildAt(index) instanceof TextView) {
                final TextView textView = (TextView) linearLayout.getChildAt(index);
                textView.setTextColor(Color.RED);
                Integer colorFrom = Color.RED;
                Integer colorTo = getResources().getColor(R.color.primary_font_color);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(500);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        textView.setTextColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
                break;
            }
        }
    }


    protected class SurveyDetailAdapter extends SurveyListAdapter {

        protected SurveyDetailAdapter(Context context, SurveyModel model) {
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
                    return new SurveyDetailBlankFillHolder(view, new MyCustomEditTextListener());
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
                if (TextUtils.isEmpty(mSurveyModel.getSurvey().getDescription())) {
                    headerHolder.mSurveyDescription.setVisibility(View.GONE);
                } else {
                    headerHolder.mSurveyDescription.setVisibility(View.VISIBLE);
                    headerHolder.mSurveyDescription.setText(mSurveyModel.getSurvey().getDescription());
                }
            } else {
                final int realPosition = getRealPosition(holder);
                SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(realPosition);
                if (question.getType().equals("blankFillingQuestion")) {
                    SurveyDetailBlankFillHolder blankFillHolder = (SurveyDetailBlankFillHolder) holder;
                    if (question.getIsOptional().equals("1")) {
                        blankFillHolder.mBlankStem.setText(String.format("Q%d：[%s] %s （选填）", position, "问答题", question.getStem()));
                    } else {
                        blankFillHolder.mBlankStem.setText(String.format("Q%d：[%s] %s", position, "问答题", question.getStem()));
                    }
                    blankFillHolder.myCustomEditTextListener.updatePosition(realPosition);
                    blankFillHolder.mBlankEdit.setText(question.getBlankContent());
                } else {
                    SurveyDetailChoiceHolder choiceHolder = (SurveyDetailChoiceHolder) holder;
                    if (question.getType().equals("singleChoice")) {
                        if (question.getIsOptional().equals("1")) {
                            choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s （选填）", position, "单选题", question.getStem()));
                        } else {
                            choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s", position, "单选题", question.getStem()));
                        }
                    } else {
                        if (question.getIsOptional().equals("1")) {
                            choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s （选填）", position, "单选题", question.getStem()));
                        } else {
                            choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s", position, "多选题", question.getStem()));
                        }
                    }
                    showChoices(choiceHolder, question, realPosition);
                }
            }
        }

        protected void showChoices(SurveyDetailChoiceHolder holder, SurveyModel.QuestionnaireItemsBean question, int position) {
            holder.mChoiceList.removeAllViews();
            for (int i = 0; i < question.getMetas().size(); i++) {
                View view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_choice_singal, holder.mChoiceList, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 0);
                params.weight = 1;
                view.setLayoutParams(params);
                TextView choiceLetter = (TextView) view.findViewById(R.id.tv_choice_letter);
                TextView choiceContent = (TextView) view.findViewById(R.id.tv_choice_content);
                SurveyModel.QuestionnaireItemsBean.MetasBean meta = question.getMetas().get(i);
                if (meta.isSelected()) {
                    choiceLetter.setTextColor(getResources().getColor(R.color.white));
                    choiceLetter.setBackground(getResources().getDrawable(R.drawable.shape_survey_selected));
                    view.setBackgroundColor(getResources().getColor(R.color.my_sch_bg_noraml_gradient));
                } else {
                    choiceLetter.setTextColor(getResources().getColor(R.color.primary));
                    choiceLetter.setBackground(getResources().getDrawable(R.drawable.shape_survey_unselected));
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                choiceLetter.setText(getOrderLetter(meta.getOrder()));
                choiceContent.setText(meta.getName());
                view.setOnClickListener(clickChoice(question, position, i));
                holder.mChoiceList.addView(view);
            }
        }

        private View.OnClickListener clickChoice(final SurveyModel.QuestionnaireItemsBean question, final int position, final int index) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SurveyModel.QuestionnaireItemsBean.MetasBean meta = question.getMetas().get(index);
                    meta.setSelected(!meta.isSelected());
                    if (question.getType().equals("singleChoice")) {
                        for (SurveyModel.QuestionnaireItemsBean.MetasBean metasBean : question.getMetas()) {
                            if (meta.getOrder() != metasBean.getOrder()) {
                                metasBean.setSelected(false);
                            }
                        }
                    }
                    changeAnswer(question);
                    notifyDataSetChanged();
                }
            };
        }


        protected class SurveyDetailBlankFillHolder extends RecyclerView.ViewHolder {

            protected TextView                 mBlankStem;
            protected EditText                 mBlankEdit;
            protected MyCustomEditTextListener myCustomEditTextListener;

            protected SurveyDetailBlankFillHolder(View view, MyCustomEditTextListener customEditTextListener) {
                super(view);
                mBlankStem = (TextView) view.findViewById(R.id.tv_blank_stem);
                mBlankEdit = (EditText) view.findViewById(R.id.edit_survey_blank);
                myCustomEditTextListener = customEditTextListener;
                mBlankEdit.addTextChangedListener(myCustomEditTextListener);
            }
        }

        protected class MyCustomEditTextListener implements TextWatcher {


            protected int position;

            protected void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // no op
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mSurveyModel.getQuestionnaireItems().get(position).setBlankContent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeAnswer(mSurveyModel.getQuestionnaireItems().get(position));
            }
        }
    }

}
