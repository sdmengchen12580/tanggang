package com.edusoho.kuozhi.clean.module.main.study.exam.base.Widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ExamBaseQuestionWidget;
import com.edusoho.kuozhi.v3.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.v3.util.html.EduTagHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by howzhi on 14-9-29.
 */
public class ExamFillQuestionWidget extends ExamBaseQuestionWidget {

    private   int          blankCout;
    protected LinearLayout fillLayout;
    private   String[]     mFillAnswers;

    public ExamFillQuestionWidget(Context context) {
        super(context);
    }

    public ExamFillQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(AttributeSet attrs) {

    }

    @Override
    protected void refreshView() {
        stemView = this.findViewById(R.id.question_stem);
        String stem = String.format(
                "%d, (<font color='#ffca4a'>%.2f分</font>) %s",
                mIndex,
                Double.parseDouble(mQuestion.getScore()),
                parseStem(mQuestion.getStem()));
        stemView.setText(Html.fromHtml(
                stem,
                new EduImageGetterHandler(mContext, stemView),
                new EduTagHandler()
        ));
        fillLayout = this.findViewById(R.id.question_fill_layout);
        Resources resources = mContext.getResources();
        mFillAnswers = new String[blankCout];
        for (int index = 0; index < blankCout; index++) {
            final EditText editText = new EditText(mContext);
            editText.setSingleLine();
            editText.setPadding(10, 5, 5, 0);
            editText.setHint("答案" + (index + 1));
            editText.setTextColor(resources.getColor(R.color.question_fill_text));
            editText.setHintTextColor(resources.getColor(R.color.question_fill_hit));
            editText.setBackgroundDrawable(resources.getDrawable(R.drawable.login_edt_bg_sel));
            editText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.question_fill));
            editText.setTag(index);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    sendChangeAnswerEvent((int) editText.getTag(), charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            fillLayout.addView(
                    editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private StringBuffer parseStem(String stem) {
        Pattern stemPattern = Pattern.compile("(\\[\\[[^\\[\\]]+\\]\\])", Pattern.DOTALL);
        Matcher matcher = stemPattern.matcher(stem);
        StringBuffer stringBuilder = new StringBuffer();
        blankCout = 0;
        while (matcher.find()) {
            Log.d(null, "find-->" + matcher);
            blankCout++;
            matcher.appendReplacement(stringBuilder, "(" + blankCout + ")");
        }
        matcher.appendTail(stringBuilder);

        return stringBuilder;
    }

    private void sendChangeAnswerEvent(int i, String content) {
        mFillAnswers[i] = content;
        ExamAnswer.AnswersBean answersBean = new ExamAnswer.AnswersBean(mQuestion.getId(), Arrays.asList(mFillAnswers));
        EventBus.getDefault().postSticky(new MessageEvent<>(answersBean, MessageEvent.CHANGE_EXAM_ANSWER));
    }
}
