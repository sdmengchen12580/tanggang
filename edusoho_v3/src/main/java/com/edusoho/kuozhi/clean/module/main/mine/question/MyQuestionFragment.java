package com.edusoho.kuozhi.clean.module.main.mine.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;

import java.util.Arrays;

/**
 * Created by JesseHuang on 2017/2/8.
 */

public class MyQuestionFragment extends BaseFragment<MyQuestionContract.Presenter> implements MyQuestionContract.View {

    private static int ASK    = 1;
    private static int ANSWER = 2;

    private SwipeRefreshLayout srlContent;
    private RecyclerView       rvContent;
    private TextView           tvAsk;
    private TextView           tvAnswer;
    private ESIconView         esicBack;
    private int mCurrentStatus = ASK;

    private MyAskQuestionAdapter    mMyAskQuestionAdapter;
    private MyAnswerQuestionAdapter mMyAnswerQuestionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_question, container, false);
        srlContent = view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        tvAsk = view.findViewById(R.id.tv_question_post);
        tvAnswer = view.findViewById(R.id.tv_question_answer);
        esicBack = view.findViewById(R.id.iv_back);
        tvAsk.setOnClickListener(getClickTypeClickListener());
        tvAnswer.setOnClickListener(getClickTypeClickListener());
        esicBack.setOnClickListener(getBackClickListener());
        initData();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switchFilterType(mCurrentStatus);
            }
        });
        return view;
    }

    private void initData() {
        Log.d("flag--", "initData: ");
        mMyAskQuestionAdapter = new MyAskQuestionAdapter(getActivity());
        mMyAnswerQuestionAdapter = new MyAnswerQuestionAdapter(getActivity());
        rvContent.setAdapter(mMyAskQuestionAdapter);
        mPresenter = new MyQuestionPresenter(this);
        switchFilterType(mCurrentStatus);
    }

    private void switchFilterType(int type) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (type == ASK) {
            loadAskedQuestionData();
            tvAsk.setTextColor(getResources().getColor(R.color.primary_color));
            tvAnswer.setTextColor(getResources().getColor(R.color.primary_font_color));
        } else if (type == ANSWER) {
            loadAnsweredQuestionData();
            tvAsk.setTextColor(getResources().getColor(R.color.primary_font_color));
            tvAnswer.setTextColor(getResources().getColor(R.color.primary_color));
        }
        mCurrentStatus = type;
    }

    private void loadAskedQuestionData() {
        srlContent.setRefreshing(true);
        mPresenter.requestAskData();
    }

    private void loadAnsweredQuestionData() {
        srlContent.setRefreshing(true);
        mPresenter.requestAnswerData();
    }

    private View.OnClickListener getBackClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        };
    }

    private View.OnClickListener getClickTypeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_question_post) {
                    switchFilterType(ASK);
                } else if (v.getId() == R.id.tv_question_answer) {
                    switchFilterType(ANSWER);
                }
            }
        };
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showAskComplete(MyThreadEntity[] myThreadEntities) {
        mMyAskQuestionAdapter.setData(Arrays.asList(myThreadEntities));
        rvContent.setAdapter(mMyAskQuestionAdapter);
    }

    @Override
    public void showAnswerComplete(MyThreadEntity[] entities) {
        mMyAnswerQuestionAdapter.setData(Arrays.asList(entities));
        rvContent.setAdapter(mMyAnswerQuestionAdapter);
    }

    @Override
    public void hideSwp() {
        srlContent.setRefreshing(false);
    }

    public static class ViewHolderAsk extends RecyclerView.ViewHolder {
        TextView tvType;
        TextView tvContent;
        TextView tvTime;
        TextView tvReviewNum;
        TextView tvOrder;
        public View layout;
        View     vLine;
        TextView tvElite;

        ViewHolderAsk(View view) {
            super(view);
            tvType = (TextView) view.findViewById(R.id.tv_type);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvReviewNum = (TextView) view.findViewById(R.id.tv_review_num);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            layout = view.findViewById(R.id.rlayout_ask_question_item_layout);
            vLine = view.findViewById(R.id.v_line);
            tvElite = (TextView) view.findViewById(R.id.tv_is_elite);
        }
    }

    public static class ViewHolderAnswer extends RecyclerView.ViewHolder {
        View     vLine;
        TextView tvTime;
        TextView tvContentAnswer;
        TextView tvContentAsk;
        TextView tvOrder;
        View     layout;

        ViewHolderAnswer(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvContentAnswer = (TextView) view.findViewById(R.id.tv_content_answer);
            tvContentAsk = (TextView) view.findViewById(R.id.tv_content_ask);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            layout = view.findViewById(R.id.rlayout_answer_question_item_layout);
            vLine = view.findViewById(R.id.v_line);
        }
    }
}
