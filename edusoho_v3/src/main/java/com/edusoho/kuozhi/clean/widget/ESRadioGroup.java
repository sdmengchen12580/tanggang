package com.edusoho.kuozhi.clean.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.widget.component.ESImageGetter;
import com.edusoho.kuozhi.v3.util.html.EduHtml;

import java.util.ArrayList;
import java.util.List;

public class ESRadioGroup extends RadioGroup {
    private Context              mContext;
    private boolean              mIsMultipleChoice;
    private List<ESChoiceOption> mChoices;

    public ESRadioGroup(Context context) {
        super(context);
        init(context);
    }

    public ESRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ESRadioGroup);
        mIsMultipleChoice = ta.getBoolean(R.styleable.ESRadioGroup_isMultiple, false);
        ta.recycle();
    }

    private void init(Context context) {
        mChoices = new ArrayList<>();
        mContext = context;
    }

    public void setMetas(List<String> list, View.OnClickListener clickListener) {
        int index = 1;
        if (list != null) {
            for (String option : list) {
                final ESChoiceOption esChoiceOption = new ESChoiceOption(mContext, mIsMultipleChoice, index++, clickListener);
                TextView content = esChoiceOption.getContentWidget();
                ESImageGetter esImageGetter = new ESImageGetter(mContext, content);
                esImageGetter.setType(ESImageGetter.QUESTION_CHOICE);
                SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(option, esImageGetter, null);
                EduHtml.addImageClickListener(spanned, content, mContext);
                content.setText(spanned);
                this.mChoices.add(esChoiceOption);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                this.addView(esChoiceOption, lp);
            }
        }
    }

    public ESChoiceOption getChoiceAt(int index) {
        return mChoices.get(index);
    }

    public void setCheck(ESChoiceOption esChoiceOption) {
        if (!mIsMultipleChoice) {
            for (ESChoiceOption choice : mChoices) {
                choice.setChecked(false);
            }
            esChoiceOption.setChecked(true);
        }
    }

    public class ESChoiceOption extends LinearLayout {
        private boolean              isMultipleChoice;
        private Context              mContext;
        private RadioButton          mChoice;
        private CheckBox             mMultipleChoice;
        private TextView             mContent;
        private View.OnClickListener mOptionClickListener;
        private int                  mIndex;

        private ESChoiceOption(Context context, boolean multiple, int index, View.OnClickListener clickListener) {
            super(context);
            mContext = context;
            isMultipleChoice = multiple;
            this.mOptionClickListener = clickListener;
            this.mIndex = index;
            init(context);
        }

        private void init(Context context) {
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            int resourceId = mContext.getResources().getIdentifier(
                    "hw_question_choice_" + mIndex, "drawable", mContext.getPackageName());
            LinearLayout.LayoutParams choiceLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            choiceLayoutParams.gravity = Gravity.TOP;
            if (isMultipleChoice) {
                mMultipleChoice = new CheckBox(mContext);
                mMultipleChoice.setButtonDrawable(resourceId);
                mMultipleChoice.setOnClickListener(mOptionClickListener);
                addView(mMultipleChoice, choiceLayoutParams);
            } else {
                mChoice = new RadioButton(context);
                mChoice.setButtonDrawable(resourceId);
                mChoice.setOnClickListener(mOptionClickListener);
                addView(mChoice, choiceLayoutParams);
            }
            mContent = new TextView(context);
            mContent.setTextColor(context.getResources().getColor(R.color.primary_font_color));
            LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            contentLayoutParams.setMargins(AppUtils.dp2px(mContext, 8), 0, 0, 0);
            addView(mContent, contentLayoutParams);
        }

        public TextView getContentWidget() {
            return mContent;
        }

        public void setChecked(boolean checked) {
            if (!isMultipleChoice) {
                mChoice.setChecked(checked);
            } else {
                mMultipleChoice.setChecked(checked);
            }
        }

        public void setClickable(boolean clickable) {
            if (!isMultipleChoice) {
                mChoice.setClickable(clickable);
            } else {
                mMultipleChoice.setClickable(clickable);
            }
        }

        public boolean isChecked() {
            if (!isMultipleChoice) {
                return mChoice.isChecked();
            } else {
                return mMultipleChoice.isChecked();
            }
        }
    }
}
