package com.edusoho.kuozhi.clean.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DiscretePathEffect;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.BaseExamQuestionFragment;

import java.lang.ref.WeakReference;

import myutils.DensityUtil;
import myutils.FastClickUtils;

public class AnswerQuesPicPop {

    private View popView;
    private View shareView = null;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private Fragment[] fragments;
    private ExamAnswer examAnswer;

    public AnswerQuesPicPop(Context baseActivity, Fragment[] fragments, ExamAnswer examAnswer) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        this.examAnswer = examAnswer;
        this.fragments = fragments;
        weakReference = new WeakReference(baseActivity);
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            if (fragments == null || fragments.length == 0) {
                return null;
            }
            popView = LayoutInflater.from(baseActivity).inflate(R.layout.pop_layout_answerques, null, false);

            //-----------------------------------------填充
            LinearLayout ll_conitaner = popView.findViewById(R.id.ll_conitaner);
            ViewGroup.LayoutParams tvTitleVp1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(baseActivity, 40f));
            ViewGroup.LayoutParams tvTitleVp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(baseActivity, 31f));
            for (int a = 0; a < this.fragments.length; a++) {
                HorizontalViewGroup viewgroup = new HorizontalViewGroup(baseActivity, null);
                viewgroup.setValue(baseActivity, 8);//设置行数
                String typeName = ((BaseExamQuestionFragment) this.fragments[a]).getQuesType();
                //添加标题
                ll_conitaner.addView(addTextTitle(baseActivity, typeName, a, viewgroup.getChildHorizontalSpace()), a == 0 ? tvTitleVp1 : tvTitleVp2);
                //添加题目
                ll_conitaner.addView(viewgroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                int sizeView = DensityUtil.dip2px(baseActivity, 32f);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(sizeView, sizeView);
                Log.e("测试个数: ", "个数=" + ((BaseExamQuestionFragment) this.fragments[a]).getQuesDataList().getQuestionList().size());
                for (int i = 0; i < ((BaseExamQuestionFragment) this.fragments[a]).getQuesDataList().getQuestionList().size(); i++) {
                    //fixme 可要可不要，判断题目对错
                    boolean quesHasFnished = false;
                    if (examAnswer != null) {
                        for (int b = 0; b < examAnswer.getAnswers().size(); b++) {
                            if (examAnswer.getAnswers().get(b).getQuestionId().equals(
                                    ((BaseExamQuestionFragment) this.fragments[a]).getQuesDataList().getQuestionList().get(i).getId())) {
                                quesHasFnished = true;
                            }
                        }
                    }
                    TextView tvSingle = addQuesItemTex(baseActivity, "" + (i + 1), quesHasFnished);
                    viewgroup.addView(tvSingle, lp);
                    final int finalI = i;
                    final int finalA = a;
                    tvSingle.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (FastClickUtils.isFastClick()) {
                                        return;
                                    }
                                    if (AnswerQuesPicPop.this.clickCallback != null) {
                                        dismissPop();
                                        AnswerQuesPicPop.this.clickCallback.selectWhich(
                                                finalA,
                                                finalI,
                                                ((BaseExamQuestionFragment) fragments[finalA]).getQuesDataList().getQuestionList().get(finalI));
                                    }
                                }
                            }
                    );
                }
            }

            //-----------------------------------------取消
            LinearLayout ll_cancel = popView.findViewById(R.id.ll_cancel);
            ll_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AnswerQuesPicPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });
            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AnswerQuesPicPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });

            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            //点击外部，popupwindow会消失
            popWindow.setFocusable(false);
            popWindow.setOutsideTouchable(false);
//            popWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            popWindow.showAtLocation(popView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            showPopBlackBg(baseActivity);
        }
        return popWindow;
    }

    //添加title
    private TextView addTextTitle(Context context, String content, int po, int leftPadding) {
        TextView textView = new TextView(context);
        String title = getTitle(content);
        textView.setText(title);
        textView.setTextColor(Color.parseColor("#161616"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        if (po == 0) {
            textView.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            textView.setGravity(Gravity.TOP);
        }
        textView.setPadding(leftPadding, 0, 0, 0);
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
        return textView;
    }

    private String getTitle(String type) {
        if (type.equals("single_choice")) {
            return "单选题";
        }
        if (type.equals("choice")) {
            return "多选题";
        }
        if (type.equals("essay")) {
            return "问答题";
        }
        if (type.equals("uncertain_choice")) {
            return "不定项选择题";
        }
        if (type.equals("determine")) {
            return "判断题";
        }
        if (type.equals("fill")) {
            return "填空题";
        }
        return "";
    }

    //添加题目item
    private TextView addQuesItemTex(Context context, String content, boolean hasFinished) {
        TextView tvSingle = new TextView(context);
        tvSingle.setText(content);
        tvSingle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvSingle.setGravity(Gravity.CENTER);
        tvSingle.setTextColor(Color.parseColor(hasFinished ? "#ffffff" : "#2C82C5"));
        tvSingle.setBackgroundResource(hasFinished ? R.drawable.shape_16_solid_2c82c5 : R.drawable.shape_16_stroke_d7d7d7);
        return tvSingle;
    }

    //----------------------------------------------背景颜色的改变----------------------------------
    //初始化弹窗的背景色
    public void showPopBlackBg(Context baseActivity) {
        if (baseActivity != null) {
            changePopBlackBg(0.8F);//越大越浅
        }
    }

    //恢复弹窗的背景色
    public void backNormalPopBg() {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = 1f;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    //弹窗背景颜色
    public void changePopBlackBg(float blackBg) {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = blackBg;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    public void dismissPop() {
        if (popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
            backNormalPopBg();
        }
    }


    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void selectWhich(int fraPosition, int Quesposition, ExamModel.QuestionsBean bean);
    }
}
