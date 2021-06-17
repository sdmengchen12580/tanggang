package com.edusoho.kuozhi.clean.module.main.study.exam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamInfoModel;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import myutils.FastClickUtils;

/**
 * 专项考试页面
 */
public class ExamInfoActivity extends BaseActivity<ExamInfoContract.Presenter> implements ExamInfoContract.View {

    private LoadDialog mProcessDialog;
    private String mExamId;
    private ExamInfoModel mExamInfo;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.TextView tvtoolbartitle;
    private android.support.v7.widget.Toolbar tbtoolbar;
    private android.widget.TextView tvexamscore;
    private android.widget.TextView tvexamtime;
    private android.widget.TextView tvexamdesc;
    private android.widget.TextView tvexamresittimes;
    private android.widget.Button btndoexam;
    private LinearLayout ll_bt_layout;
    private String playUrl = "";
    protected final int PERMISSION_REQUEST_CAMEAR_CODE = 0x1;
    protected int mNoPermissionIndex = 0;
    protected final String[] permissionManifestCamera = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public static void launch(Context context, String examId) {
        Intent intent = new Intent();
        intent.putExtra("exam_id", examId);
        intent.setClass(context, ExamInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_info);
        mExamId = getIntent().getStringExtra("exam_id");
        initView();
        initData();
        if (Build.VERSION.SDK_INT >= 23 && !permissionCheck(1)) {
            ActivityCompat.requestPermissions(ExamInfoActivity.this, permissionManifestCamera, PERMISSION_REQUEST_CAMEAR_CODE);
        }
    }

    private void initView() {
        this.ll_bt_layout = findViewById(R.id.ll_bt_layout);
        this.btndoexam = findViewById(R.id.btn_do_exam);
        this.tvexamresittimes = findViewById(R.id.tv_exam_resit_times);
        this.tvexamdesc = findViewById(R.id.tv_exam_desc);
        this.tvexamtime = findViewById(R.id.tv_exam_time);
        this.tvexamscore = findViewById(R.id.tv_exam_score);
        this.tbtoolbar = findViewById(R.id.tb_toolbar);
        this.tvtoolbartitle = findViewById(R.id.tv_toolbar_title);
        this.ivback = findViewById(R.id.iv_back);
    }

    private void initData() {
        mPresenter = new ExamInfoPresenter(this, mExamId);
        mPresenter.subscribe();
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamInfoActivity.this.finish();
            }
        });
    }

    //检测是否有权限 1：camera
    protected boolean permissionCheck(int type) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        String permission;
        if (type == 1) {
            for (int i = 0; i < permissionManifestCamera.length; i++) {
                permission = permissionManifestCamera[i];
                mNoPermissionIndex = i;
                if (PermissionChecker.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = PackageManager.PERMISSION_DENIED;
                }
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    //--------------------------------业务类接口--------------------------------
    //考试入口
    @Override
    public void refreshView(ExamInfoModel examInfoModel) {
        playUrl = examInfoModel.getFile().getMediaUri();
        this.ll_bt_layout.setVisibility(View.VISIBLE);
        mExamInfo = examInfoModel;
        tvtoolbartitle.setText(mExamInfo.getName());
        tvexamresittimes.setVisibility(View.GONE);
        if (mExamInfo.getType().equals("grade")) {
            tvexamtime.setVisibility(View.VISIBLE);
            if (mExamInfo.getRemainingResitTimes().equals("0")) {
                tvexamresittimes.setVisibility(View.GONE);
            } else {
                tvexamresittimes.setVisibility(View.VISIBLE);
                SpannableString resitString = new SpannableString(String.format("考试机会剩余：%s 次", mExamInfo.getRemainingResitTimes()));
                resitString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_color)), 7, mExamInfo.getRemainingResitTimes().length() + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvexamresittimes.setText(resitString);
            }
            SpannableString scoreString = new SpannableString(String.format("本次考试共%s题，总分%s分，及格分为%s分", mExamInfo.getQuestionsCount(), mExamInfo.getScore(), mExamInfo.getPassScore()));
            int countLength = mExamInfo.getQuestionsCount().length() + 1;
            int scoreLength = mExamInfo.getScore().length() + 1;
            scoreString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), 5, countLength + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            scoreString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), countLength + 8, scoreLength + countLength + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            scoreString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), scoreLength + countLength + 13, scoreString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvexamscore.setText(scoreString);

            String minuteString = Double.toString(Double.parseDouble(mExamInfo.getLength()) / 60);
            SpannableString timeString = new SpannableString(String.format("请在%s分钟内作答", minuteString));
            timeString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), 2, minuteString.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvexamtime.setText(timeString);
            tvexamdesc.setText("考试开始后将持续计时，到期会自动交卷");
            if (mExamInfo.getLength().equals("0")) {
                tvexamtime.setVisibility(View.INVISIBLE);
                tvexamdesc.setVisibility(View.INVISIBLE);
            }
        }
        //todo 区分type=open 开卷，拿到视频播放链接。参数叫istime,如果这个是1，那个每个题目里面会有一个time,这个time就是题目弹出的时间
        else {
            tvexamtime.setVisibility(View.GONE);
            SpannableString scoreString = new SpannableString(String.format("本次考试共%s题，总分%s分", mExamInfo.getQuestionsCount(), mExamInfo.getScore()));
            scoreString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), 5, mExamInfo.getQuestionsCount().length() + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            scoreString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_black_normal)), mExamInfo.getQuestionsCount().length() + 9, mExamInfo.getScore().length() + mExamInfo.getQuestionsCount().length() + 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvexamscore.setText(scoreString);
            tvexamdesc.setText("你必须全部答对，才可完成考试");
        }
        btndoexam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamActivity.launch(ExamInfoActivity.this, mExamId, false, false, false);
                ExamInfoActivity.this.finish();
            }
        });
        /**
         *  视频播放功能：
         *  1.原来看视频界面-CourseProjectActivity
         *  2.mImmediateLearn点击开始学习+配合CourseProjectPresenter：
         *      mPresenter.learnTask(task.id);
         *      先调用此接口https://elearning.jtport.com/api/courses/1044/tasks/2917     fixme "type":"video" 播放视频
         *  3.获取课程信息https://elearning.jtport.com/api/courses/1059
         *      判断课程信息-会员到期等：https://elearning.jtport.com/api/me/course_members/1059
         *  4.发现type=video后，调用接口：https://elearning.jtport.com/api/lessons/2917?hls_encryption=1
         *  5.LessonVideoPlayerFragment视频播放
         *
         *  fixme 最新理解
         *  1.原来老的，只有答题，没视频 （答案可以修改）
         *  2.有视频：上方视频，下方问题，边看边答 （答案可以修改）
         *  3.有视频：播放视频，到指定时间点，弹出问题  （答案不可以修改）
         *
         *  fixme 注意：原各种题型fra为BaseExamQuestionFragment，单个题的item=QuestionWidget
         *  1.先看懂QuestionWidget单个题型 fixme ok
         *  2.看懂如何将答案全统计到答题卡中 fixme ok
         *  3.答题卡点击切换到指定题目 fixme ok
         *  4.视频播放-ExamActivity fixme ok
         *  5.单独写个页面看视频（新增字段istime，开卷播放视频，到时候弹窗回答问题）
         *  6.人脸认证 fixme ok
         *  7.练习模式：当题目给出对错，需要结合第2点，整个集合 <ID,正确答案>
         *
         *  todo 我的审批，状态描述
         *  fixme 1.NewHttpUtils 正式/测试域名
         *  fixme 2.MyAssignmentsFragment 182行测试代码，可以将第一题专项练习，变成可以开始考试
         */
        //测试练习模式
        findViewById(R.id.tv_practise_model).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                ExamActivity.launch(ExamInfoActivity.this, mExamId, false, false, false);
                ExamInfoActivity.this.finish();
            }
        });

        //测试视频问答模式
        findViewById(R.id.tv_video_ques).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                ExamActivity.launch(ExamInfoActivity.this, mExamId, false, false, true, playUrl);
                ExamInfoActivity.this.finish();
            }
        });

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
}
