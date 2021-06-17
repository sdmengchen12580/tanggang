package com.edusoho.kuozhi.clean.module.main.study.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamFullMarkResult;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.bean.mystudy.FullMarkAnswer;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/2/9.
 */

public class ExamCardActivity extends BaseActivity {

    private ExamAnswer                                 mAnswer;
    private ExamModel                                  mExamModel;
    private String                                     mExamId;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.LinearLayout                examcardlayout;
    private LoadDialog                                 mProcessDialog;
    private String                                     mResultId;
    private android.widget.Button                      btnexamsubmit;
    private boolean mIsFullMark = false;

    public static void launch(Context context, String resultId, ExamAnswer answer, ExamModel model) {
        Intent intent = new Intent();
        intent.putExtra("exam_result_id", resultId);
        Bundle bundle = new Bundle();
        bundle.putSerializable("exam_answer", answer);
        bundle.putSerializable("exam_model", model);
        intent.putExtras(bundle);
        intent.setClass(context, ExamCardActivity.class);
        context.startActivity(intent);
    }

    public static void launchFullMark(Context context, String resultId, ExamAnswer answer, ExamModel model, String examId) {
        Intent intent = new Intent();
        intent.putExtra("exam_result_id", resultId);
        intent.putExtra("exam_id", examId);
        intent.putExtra("is_full_mark", true);
        Bundle bundle = new Bundle();
        bundle.putSerializable("exam_answer", answer);
        bundle.putSerializable("exam_model", model);
        intent.putExtras(bundle);
        intent.setClass(context, ExamCardActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_card);
        mExamModel = (ExamModel) getIntent().getSerializableExtra("exam_model");
        mAnswer = (ExamAnswer) getIntent().getSerializableExtra("exam_answer");
        mResultId = getIntent().getStringExtra("exam_result_id");
        mIsFullMark = getIntent().getBooleanExtra("is_full_mark", false);
        mExamId = getIntent().getStringExtra("exam_id");
        initView();
        initData();
    }

    private void initView() {
        this.btnexamsubmit = findViewById(R.id.btn_exam_submit);
        this.examcardlayout = findViewById(R.id.exam_card_layout);
        this.ivback = findViewById(R.id.iv_back);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        int index = 0;
        for (QuestionType type : mExamModel.getExamInfo().keySet()) {
            View cardView = layoutInflater.inflate(R.layout.testpaper_card_layout, null);
            TextView label = cardView.findViewById(R.id.testpaper_card_label);
            GridView cardGridView = cardView.findViewById(R.id.testpaper_card_gridview);
            List<ExamModel.QuestionsBean> questions = new ArrayList<>();
            List<ExamAnswer.AnswersBean> answers = new ArrayList<>();
            for (ExamModel.QuestionsBean questionsBean : mExamModel.getQuestions()) {
                if (questionsBean.getType().equals(type.toString())) {
                    questions.add(questionsBean);
                }
            }
            for (ExamAnswer.AnswersBean answersBean : mAnswer.getAnswers()) {
                for (ExamModel.QuestionsBean question : questions) {
                    if (question.getId().equals(answersBean.getQuestionId())) {
                        answers.add(answersBean);
                        break;
                    }
                }
            }
            ExamCardAdapter cardAdapter = new ExamCardAdapter(this, R.layout.testpaper_card_gridview_item, questions, answers);
            cardGridView.setAdapter(cardAdapter);
            cardGridView.setOnItemClickListener(new CardItemClickListener(index++));
            label.setText(type.title());
            examcardlayout.addView(cardView);
        }
    }

    private class CardItemClickListener implements AdapterView.OnItemClickListener {
        private int mIndex;

        CardItemClickListener(int index) {
            this.mIndex = index;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EventBus.getDefault().postSticky(new MessageEvent<>(
                    new int[]{mIndex, position}, MessageEvent.CLICK_QUESTION_CARD_ITEM));
            finish();
        }
    }


    private void initData() {
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamCardActivity.this.finish();
            }
        });
        btnexamsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsFullMark) {
                    checkFullMark();
                } else {
                    Intent intent = new Intent();
                    intent.setAction("Finish");
                    submitExam();
                }
            }
        });

    }

    private void submitExam() {
        showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .submitExam(mResultId, mAnswer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        showProcessDialog(false);
                        showToast("提交失败");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        showProcessDialog(false);
                        showToast("提交成功");
                        Intent intent = new Intent();
                        intent.setAction("Finish");
                        sendBroadcast(intent);
                        ExamResultActivity.launch(ExamCardActivity.this, mResultId);
                        ExamCardActivity.this.finish();
                    }
                });
    }

    private void checkFullMark() {
        FullMarkAnswer fullMarkAnswer = new FullMarkAnswer();
        fullMarkAnswer.setAnswers(mAnswer.getAnswers());
        fullMarkAnswer.setExamResultId(mExamModel.getExamResult().getId());
        showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getFullMarkResult(fullMarkAnswer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<ExamFullMarkResult>() {
                    @Override
                    public void onError(String message) {
                        showProcessDialog(false);
                        showToast("提交失败");
                    }

                    @Override
                    public void onNext(ExamFullMarkResult examFullMarkResult) {
                        showProcessDialog(false);
                        reloadCardView(examFullMarkResult);
                    }
                });
    }

    private void reloadCardView(ExamFullMarkResult result) {
        if (result.getStatus().equals("wrong")) {
            showToast("题目回答有误，请根据提示更正后提交！");
            examcardlayout.removeAllViews();
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            for (QuestionType type : mExamModel.getExamInfo().keySet()) {
                View cardView = layoutInflater.inflate(R.layout.testpaper_card_layout, null);
                TextView label = (TextView) cardView.findViewById(R.id.testpaper_card_label);
                GridView cardGridView = (GridView) cardView.findViewById(R.id.testpaper_card_gridview);
                List<ExamModel.QuestionsBean> questions = new ArrayList<>();
                List<ExamAnswer.AnswersBean> answers = new ArrayList<>();
                for (ExamModel.QuestionsBean questionsBean : mExamModel.getQuestions()) {
                    if (questionsBean.getType().equals(type.toString())) {
                        questions.add(questionsBean);
                    }
                }
                for (ExamAnswer.AnswersBean answersBean : mAnswer.getAnswers()) {
                    for (ExamModel.QuestionsBean question : questions) {
                        if (question.getId().equals(answersBean.getQuestionId())) {
                            answers.add(answersBean);
                            break;
                        }
                    }
                }
                List<ExamFullMarkResult.CheckAnswersBean> checkList = new ArrayList<>();
                if (result.getCheckAnswers() != null) {
                    for (ExamFullMarkResult.CheckAnswersBean checkAnswersBean : result.getCheckAnswers()) {
                        if (checkAnswersBean.getType().equals(type.toString())) {
                            checkList.add(checkAnswersBean);
                        }
                    }
                }
                ExamCardAdapter cardAdapter = new ExamCardAdapter(this, R.layout.testpaper_card_gridview_item, questions, answers, checkList);
                cardGridView.setAdapter(cardAdapter);
                label.setText(type.title());
                examcardlayout.addView(cardView);
            }
        } else {
            submitExam();
        }
    }

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

    public class ExamCardAdapter extends BaseAdapter {

        private LayoutInflater                            inflater;
        private int                                       mResouce;
        private Context                                   mContext;
        private List<ExamModel.QuestionsBean>             mQuestionList;
        private List<ExamAnswer.AnswersBean>              mAnswerList;
        private List<ExamFullMarkResult.CheckAnswersBean> mCheckList;

        public ExamCardAdapter(Context context, int resource, List<ExamModel.QuestionsBean> questionList, List<ExamAnswer.AnswersBean> answerList) {
            mContext = context;
            mResouce = resource;
            mQuestionList = questionList;
            mAnswerList = answerList;
            inflater = LayoutInflater.from(mContext);
        }

        public ExamCardAdapter(Context context, int resource, List<ExamModel.QuestionsBean> questionList, List<ExamAnswer.AnswersBean> answerList, List<ExamFullMarkResult.CheckAnswersBean> checkList) {
            mContext = context;
            mResouce = resource;
            mQuestionList = questionList;
            mAnswerList = answerList;
            mCheckList = checkList;
            inflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getItemViewType(int position) {
            if (mCheckList == null) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return mQuestionList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getCount() {
            return mQuestionList.size();
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(mResouce, null);
            }
            if (getItemViewType(i) == 0) {
                view.setBackground(getResources().getDrawable(R.drawable.testpaper_card_gridview_item_bg_wrong));

                boolean isWrong = true;
                ExamModel.QuestionsBean question = mQuestionList.get(i);
                for (ExamFullMarkResult.CheckAnswersBean checkResult : mCheckList) {
                    if (question.getId().equals(checkResult.getQuestionId())) {
                        if (checkResult.getCheckStatus().equals("right")) {
                            isWrong = false;
                        } else {
                            isWrong = true;
                        }
                        break;
                    }
                }
                TextView mText = (TextView) view;
                mText.setTextColor(getResources().getColor(R.color.white));
                mText.setEnabled(!isWrong);
                mText.setText((i + 1) + "");
            }
            if (getItemViewType(i) == 1) {
                boolean isAnswerd = false;
                ExamModel.QuestionsBean question = mQuestionList.get(i);
                for (ExamAnswer.AnswersBean answers : mAnswerList) {
                    if (question.getId().equals(answers.getQuestionId())) {
                        isAnswerd = true;
                        break;
                    }
                }
                TextView mText = (TextView) view;
                mText.setEnabled(!isAnswerd);
                mText.setText((i + 1) + "");
            }
            return view;
        }
    }

}