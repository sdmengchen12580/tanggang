package com.edusoho.kuozhi.clean.module.main.study.survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/1.
 */

public class SurveyResultActivity extends BaseFinishActivity<SurveyResultContract.Presenter> implements SurveyResultContract.View {

    private String mSurveyId;
    private LoadDialog mProcessDialog;
    private boolean isResultVisible;
    private boolean isSkipCheck;
    private SurveyModel mSurveyModel;
    private SurveyResultAdapter mAdapter;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.TextView tvtoolbartitle;
    private android.support.v7.widget.Toolbar tbtoolbar;
    private com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView rvsurveyresult;
    private android.widget.RelativeLayout rlsuccesstip;
    private android.widget.Button btngetsurveyresult;


    public static void launch(Context context, String surveyId, boolean resultVisible) {
        Intent intent = new Intent();
        intent.putExtra("survey_id", surveyId);
        intent.putExtra("result_visible", resultVisible);
        intent.setClass(context, SurveyResultActivity.class);
        context.startActivity(intent);
    }


    public static void launchWithResult(Context context, String surveyId, boolean skipCheck) {
        Intent intent = new Intent();
        intent.putExtra("survey_id", surveyId);
        intent.putExtra("skip_check", skipCheck);
        intent.setClass(context, SurveyResultActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);
        mSurveyId = getIntent().getStringExtra("survey_id");
        isResultVisible = getIntent().getBooleanExtra("result_visible", false);
        isSkipCheck = getIntent().getBooleanExtra("skip_check", false);
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().postSticky(new MessageEvent<>(MessageEvent.FINISH_SURVEY));
    }

    private void initView() {
        this.btngetsurveyresult = (Button) findViewById(R.id.btn_get_survey_result);
        this.rlsuccesstip = (RelativeLayout) findViewById(R.id.rl_success_tip);
        this.rvsurveyresult = (ESPullAndLoadRecyclerView) findViewById(R.id.rv_survey_result);
        this.tbtoolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        this.tvtoolbartitle = (TextView) findViewById(R.id.tv_toolbar_title);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
    }

    private void initData() {
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurveyResultActivity.this.finish();
            }
        });
        mPresenter = new SurveyResultPresenter(this, mSurveyId);
        if (isResultVisible) {
            btngetsurveyresult.setVisibility(View.VISIBLE);
            btngetsurveyresult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getResult();
                }
            });
        } else {
            btngetsurveyresult.setVisibility(View.GONE);
        }
        if (isSkipCheck) {
            getResult();
        }
        rvsurveyresult.setLinearLayout();
        rvsurveyresult.setPullRefreshEnable(false);
        rvsurveyresult.setPushRefreshEnable(false);
    }

    private void getResult() {
        tvtoolbartitle.setText("调查结果");
        rlsuccesstip.setVisibility(View.GONE);
        btngetsurveyresult.setVisibility(View.GONE);
        mPresenter.subscribe();
    }


    @Override
    public void refreshView(SurveyModel surveyModel) {
        mSurveyModel = surveyModel;
        mAdapter = new SurveyResultAdapter(SurveyResultActivity.this, mSurveyModel);
        mAdapter.setHeaderView(LayoutInflater.from(this).inflate(R.layout.item_survey_header, rvsurveyresult, false));
        rvsurveyresult.setAdapter(mAdapter);
    }

    @Override
    public void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
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

    private class SurveyResultAdapter extends SurveyListAdapter {

        private SurveyResultAdapter(Context context, SurveyModel model) {
            super(context, model);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER) {
                return new SurveyDetailHeaderHolder(mHeaderView);
            } else {
                View view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_choice, parent, false);
                return new SurveyDetailChoiceHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                SurveyDetailHeaderHolder headerHolder = (SurveyDetailHeaderHolder) holder;
                headerHolder.mSurveyTitle.setText(mSurveyModel.getSurvey().getTitle());
                headerHolder.mSurveyAnonymousTip.setVisibility(View.GONE);
                if (mSurveyModel.getSurvey().getStartTime() != null && mSurveyModel.getSurvey().getEndTime() != null) {
                    String startDate = AppUtil.timeStampToDate(mSurveyModel.getSurvey().getStartTime(), "yyyy-MM-dd");
                    String endDate = AppUtil.timeStampToDate(mSurveyModel.getSurvey().getEndTime(), "yyyy-MM-dd");
                    headerHolder.mSurveyDescription.setText(String.format("调查时间：%s 至 %s  ｜  调查推送人数：%d  ｜ 问卷回收份数：%d", startDate, endDate, mSurveyModel.getSurvey().getAllCount(), mSurveyModel.getSurvey().getFinishedCount()));
                } else {
                    headerHolder.mSurveyDescription.setText(String.format("调查推送人数：%d  ｜ 问卷回收份数：%d", mSurveyModel.getSurvey().getAllCount(), mSurveyModel.getSurvey().getFinishedCount()));
                }
            } else {
                final int realPosition = getRealPosition(holder);
                SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(realPosition);
                SurveyDetailChoiceHolder choiceHolder = (SurveyDetailChoiceHolder) holder;
                if (question.getType().equals("singleChoice")) {
                    choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s", position, "单选题", question.getStem()));
                } else if (question.getType().equals("blankFillingQuestion")){
                    choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s", position, "问答题", question.getStem()));
                } else {
                    choiceHolder.mChoiceStem.setText(String.format("Q%d：[%s] %s", position, "多选题", question.getStem()));
                }
                showStatistics(choiceHolder, question, realPosition);
            }
        }

        private void showStatistics(SurveyDetailChoiceHolder holder, final SurveyModel.QuestionnaireItemsBean question, final int position) {
            holder.mChoiceList.removeAllViews();
            LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(-1, 0);
            params.weight = 1;
            if (question.getType().equals("blankFillingQuestion")) {
                List<List<String>> answerList = question.getStatistics();
                int total;
                if (answerList.size() > 5) {
                    total = 6;
                } else {
                    total = answerList.size() + 1;
                }
                for (int i = 0; i < total; i++) {
                    View view;
                    view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_result_blank, holder.mChoiceList, false);
                    view.setLayoutParams(params);
                    TextView answerContent = (TextView) view.findViewById(R.id.tv_survey_result_blank);
                    if (i == 5) {
                        view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_result_blank_bottom_button, holder.mChoiceList, false);
                        Button moreButton = (Button) view.findViewById(R.id.btn_survey_result_blank);
                        moreButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SurveyAnswerListActivity.launch(mContex, mSurveyId, question.getId(), String.format("Q%d：[问答题] %s", position - 1, question.getStem()));
                            }
                        });
                    } else if (i == 0) {
                        answerContent.setText("回答列表");
                        answerContent.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                    } else {
                        answerContent.setText(answerList.get(i - 1).get(0));
                        answerContent.setTextColor(getResources().getColor(R.color.primary_font_color));
                    }
                    holder.mChoiceList.addView(view);
                }
            } else {
                for (int i = 0; i < question.getMetas().size(); i++) {
                    SurveyModel.QuestionnaireItemsBean.MetasBean metasBean = question.getMetas().get(i);
                    View view = LayoutInflater.from(mContex).inflate(R.layout.item_survey_result_choice, holder.mChoiceList, false);
                    String letter = getOrderLetter(metasBean.getStatistics().getOrder());
                    float percent = metasBean.getStatistics().getPercent();
                    int peopleCount = metasBean.getStatistics().getCount();
                    view.setLayoutParams(params);
                    TextView choiceStem = (TextView) view.findViewById(R.id.tv_survey_result_answer);
                    TextView choicePeople = (TextView) view.findViewById(R.id.tv_survey_result_people);
                    TextView choicePercent = (TextView) view.findViewById(R.id.tv_survey_result_percent);
                    ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.item_survey_result_progressbar);
                    choiceStem.setText(String.format("%s %s", letter, metasBean.getName()));
                    choicePeople.setText(String.format("%d人", peopleCount));
                    progressBar.setProgress((int)percent);
                    BigDecimal bigDecimal = new BigDecimal(percent);
                    choicePercent.setText(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "%");
                    holder.mChoiceList.addView(view);
                }
            }
        }
    }
}
