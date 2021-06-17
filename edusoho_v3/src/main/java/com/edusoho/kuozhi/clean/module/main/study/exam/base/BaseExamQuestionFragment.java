package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;
import com.edusoho.kuozhi.clean.module.main.study.exam.ExamActivity;
import com.edusoho.kuozhi.v3.view.photo.HackyViewPager;
import com.edusoho.kuozhi.v3.view.test.QuestionWidget;
import com.tencent.stat.StatService;
import com.trello.navi.component.support.NaviFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 题型考试页面
 */
public class BaseExamQuestionFragment extends NaviFragment {

    protected ExamQuestions mQuestionsList;
    protected TextView mQuestionType;
    protected TextView mQuestionScore;
    protected TextView mQuestionNumber;
    protected RelativeLayout questionlayout;
    protected HackyViewPager questionpager;
    protected int mCurrentIndex;
    protected int mQuestionCount;
    private ExamQuestionViewPagerAdapter mExamAdapter;
    private Class<BaseExamQuestionFragment> clazz;
    private String quesType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.choice_fragment_layout, container, false);
        this.questionpager = (HackyViewPager) view.findViewById(R.id.question_pager);
        this.questionlayout = (RelativeLayout) view.findViewById(R.id.question_layout);
        mQuestionNumber = (TextView) view.findViewById(R.id.question_number);
        mQuestionType = (TextView) view.findViewById(R.id.question_type);
        mQuestionScore = (TextView) view.findViewById(R.id.question_score);
        initView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().removeAllStickyEvents();
        if (getArguments() != null) {
            mQuestionsList = (ExamQuestions) getArguments().getSerializable("question_list");
            quesType = mQuestionsList.getQuestionType();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            StatService.onPause(getActivity());
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            StatService.onResume(getActivity());
        }
        if (getActivity() != null && this.getClass().getSimpleName().equals(((ExamActivity) getActivity()).getSelectFragmentName()) &&
                questionpager != null && mExamAdapter != null) {
            questionpager.setCurrentItem(((ExamActivity) getActivity()).getSelectTestQuestionIndex());
            ((ExamActivity) getActivity()).setSelectTestQuestionIndex(-1);
        }
    }

    @Subscribe
    public void onReceiveMessage(MessageEvent messageEvent) {

    }

    //---------------------------------工具类---------------------------------
    private void initView() {
        questionpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setQuestionNumber(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCurrentIndex = 1;
        mQuestionCount = mQuestionsList.getQuestionList().size();
        setQuestionTitle();
        mExamAdapter = new ExamQuestionViewPagerAdapter(getActivity(), mQuestionsList.getQuestionList());
        questionpager.setAdapter(mExamAdapter);
        questionpager.setOffscreenPageLimit(mExamAdapter.getCount());
        setQuestionNumber(1);
    }

    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    //页面切换tan的方法
    public void changeTabIndex(int index){
        if(questionpager!=null){
            questionpager.setCurrentItem(index,false);
            setQuestionNumber(index + 1);
        }
    }

    //设置题目类型，此题型总分
    protected void setQuestionTitle() {
        mQuestionType.setText(mQuestionsList.getTypeName());
        mQuestionScore.setText(
                String.format("共%.1f分",
                        getQuestionScore()
                )
        );
    }

    //计算题型总分
    private double getQuestionScore() {
        double total = 0;
        for (ExamModel.QuestionsBean questionsBean : mQuestionsList.getQuestionList()) {
            total += Double.parseDouble(questionsBean.getScore());
        }
        return total;
    }

    //左后切换题目，页面修改
    protected void setQuestionNumber(int position) {
        String text = String.format("%d/%d", position, mQuestionCount);
        SpannableString spannableString = new SpannableString(text);
        int color = getResources().getColor(R.color.primary_color);
        int length = getNumberLength(position);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(2.0f), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mQuestionNumber.setText(spannableString);
    }

    //计算题总数
    private int getNumberLength(int number) {
        int length = 1;
        while (number >= 10) {
            length++;
            number = number / 10;
        }
        return length;
    }

    //获取题型名称
    public String getQuesType() {
        return quesType;
    }

    //获取此题型数据
    public ExamQuestions getQuesDataList(){
        return mQuestionsList;
    }

    protected class ExamQuestionViewPagerAdapter extends PagerAdapter {

        private List<ExamModel.QuestionsBean> mList;
        private Context mContext;

        protected ExamQuestionViewPagerAdapter(Context context, List<ExamModel.QuestionsBean> list) {
            super();
            mContext = context;
            mList = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ExamModel.QuestionsBean questionsBean = mList.get(position);
            View view = switchQuestionWidget(questionsBean, position + 1);
            ScrollView scrollView = new ScrollView(mContext);
            scrollView.setFillViewport(true);
            scrollView.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(scrollView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return scrollView;
        }

        protected View switchQuestionWidget(ExamModel.QuestionsBean questionsBean, int index) {
            QuestionWidget widget = new QuestionWidget(mContext, questionsBean, index);
            return widget.getExamQuestionView();
        }
    }
}
