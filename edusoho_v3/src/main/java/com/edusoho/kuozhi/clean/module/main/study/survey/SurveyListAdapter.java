package com.edusoho.kuozhi.clean.module.main.study.survey;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;

/**
 * Created by RexXiang on 2018/2/1.
 */

public class SurveyListAdapter extends RecyclerView.Adapter {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_NORMAL = 1;
    protected static final int TYPE_BLANK  = 2;
    protected View mHeaderView;
    protected SurveyModel mSurveyModel;
    protected Context mContex;

    protected SurveyListAdapter(Context context, SurveyModel model) {
        mContex = context;
        mSurveyModel = model;
    }


    protected void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    protected int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mSurveyModel.getQuestionnaireItems().size() : mSurveyModel.getQuestionnaireItems().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return TYPE_HEADER;
        } else {
            SurveyModel.QuestionnaireItemsBean question = mSurveyModel.getQuestionnaireItems().get(position - 1);
            if (question.getType().equals("blankFillingQuestion")) {
                return TYPE_BLANK;
            } else {
                return TYPE_NORMAL;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    protected String getOrderLetter(int order) {
        String letter = "";
        if (order == 0) {
            return "A";
        }
        if (order == 1) {
            return "B";
        }
        if (order == 2) {
            return "C";
        }
        if (order == 3) {
            return "D";
        }
        if (order == 4) {
            return "E";
        }
        if (order == 5) {
            return "F";
        }
        if (order == 6) {
            return "G";
        }
        if (order == 7) {
            return "H";
        }
        if (order == 8) {
            return "I";
        }
        if (order == 9) {
            return "J";
        }
        return letter;
    }

    protected class SurveyDetailHeaderHolder extends RecyclerView.ViewHolder {

        protected TextView mSurveyTitle;
        protected TextView mSurveyAnonymousTip;
        protected TextView mSurveyDescription;

        protected SurveyDetailHeaderHolder(View view) {
            super(view);
            mSurveyTitle = (TextView) view.findViewById(R.id.tv_survey_title);
            mSurveyAnonymousTip = (TextView) view.findViewById(R.id.tv_survey_anonymous_tip);
            mSurveyDescription = (TextView) view.findViewById(R.id.tv_survey_tip);
        }
    }

    protected class SurveyDetailChoiceHolder extends RecyclerView.ViewHolder {

        protected TextView mChoiceStem;
        protected LinearLayout mChoiceList;

        protected SurveyDetailChoiceHolder(View view) {
            super(view);
            mChoiceStem = (TextView) view.findViewById(R.id.tv_choice_stem);
            mChoiceList = (LinearLayout) view.findViewById(R.id.ll_survey_choice_list);
        }
    }

}
