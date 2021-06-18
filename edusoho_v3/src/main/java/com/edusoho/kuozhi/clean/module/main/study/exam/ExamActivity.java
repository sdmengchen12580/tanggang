package com.edusoho.kuozhi.clean.module.main.study.exam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.PeopleABean;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.BaseExamQuestionFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ChoiceFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.DetermineFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.EssayFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.FillFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.SingleChoiceFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.UncertainChoiceFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.videofra.VideoPlayFragment;
import com.edusoho.kuozhi.clean.utils.DigestUtils;
import com.edusoho.kuozhi.clean.utils.PhotoUtils;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.pop.AnswerQuesPicPop;
import com.edusoho.kuozhi.clean.widget.pop.AuthNoOkPop;
import com.edusoho.kuozhi.clean.widget.pop.AuthOkPop;
import com.edusoho.kuozhi.clean.widget.pop.AuthTakePicPop;
import com.edusoho.kuozhi.utils.PoolDownLoadUtils;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import extensions.PagerSlidingTabStrip;
import myutils.FastClickUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by RexXiang on 2018/2/6.
 */
public class ExamActivity extends BaseFinishActivity<ExamContract.Presenter> implements ExamContract.View {

    private LoadDialog mProcessDialog;
    private ImageView img_dtk;
    private String mExamId;
    private ExamModel mExamModel;
    protected String[] fragmentArrayList;
    protected Fragment[] fragmentList;//fixme 新增存储fragment容器
    protected String[] titles;
    protected String[] types;
    private ExamPagerAdapter fragmentAdapter;
    public static ExamAnswer mAnswer = null;
    private ESIconView ivBack;
    private TextView tvToolbarTitle;
    private PagerSlidingTabStrip pstsExamTabs;
    private ViewPager vpExamViewPager;
    private TextView tvExamResitTimes;
    private LinearLayout ll_exam_bottom;
    private Button btnDoExam;
    private int mSelectTestQuestionIndex = -1;
    private String mSelectFragmentName;
    private int currentVpPage = 0;
    private boolean isProtise = false; //是否是练习模式
    private boolean isHasVideo = false; //是否是有无视频模式
    private boolean isVideoQues = false; //是否是视频问答模式
    private FrameLayout task_container;//视频播放布局
    private static final String FRAGMENT_VIDEO_TAG = "video";
    private String url = "";//视频url
    //人脸识别
    boolean isFirst = true;//测试
    private AuthTakePicPop authTakePicPop;
    private PopupWindow popAuthTakePic;
    private AuthOkPop authOkPop;
    private PopupWindow popAuthOk;
    private AuthNoOkPop authNoOkPop;
    private PopupWindow popAuthNoOk;
    private Uri imageUri;
    private String imgTakeAddress;
    private String oldBase64 = "";
    private boolean useTyy = true;    //是否使用天翼云
    private Bitmap bitmapLeft, bitmapCurrent;
    private CascadeClassifier classifier;
    private Mat mGrayFace1, mGrayFace2;

    public static void launch(Context context, String examId, boolean isProtise, boolean isHasVideo, boolean isVideoQues) {
        Intent intent = new Intent();
        intent.putExtra("exam_id", examId);
        intent.putExtra("isProtise", isProtise);
        intent.putExtra("isHasVideo", isHasVideo);
        intent.putExtra("isVideoQues", isVideoQues);
        intent.setClass(context, ExamActivity.class);
        context.startActivity(intent);
    }

    public static void launch(Context context, String examId, boolean isProtise, boolean isHasVideo, boolean isVideoQues, String url) {
        Intent intent = new Intent();
        intent.putExtra("exam_id", examId);
        intent.putExtra("isProtise", isProtise);
        intent.putExtra("isHasVideo", isHasVideo);
        intent.putExtra("isVideoQues", isVideoQues);
        intent.putExtra("url", url);
        intent.setClass(context, ExamActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        mExamId = getIntent().getStringExtra("exam_id");
        isProtise = getIntent().getBooleanExtra("isProtise", false);
        isHasVideo = getIntent().getBooleanExtra("isHasVideo", false);
        isVideoQues = getIntent().getBooleanExtra("isVideoQues", false);
        if (getIntent().getExtras().containsKey("url")) {
            url = getIntent().getStringExtra("url");
        }
        mAnswer = new ExamAnswer();
        //控件
        this.task_container = findViewById(R.id.task_container);
        this.ll_exam_bottom = findViewById(R.id.ll_exam_bottom);
        this.img_dtk = findViewById(R.id.img_dtk);
        this.btnDoExam = findViewById(R.id.btn_do_exam);
        this.tvExamResitTimes = findViewById(R.id.tv_exam_resit_times);
        this.vpExamViewPager = findViewById(R.id.exam_view_pager);
        this.pstsExamTabs = findViewById(R.id.exam_tabs);
        this.tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        this.ivBack = findViewById(R.id.iv_back);
        //提交试卷
        btnDoExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExamModel.getExam().getType().equals("grade")) {
                    ExamCardActivity.launch(ExamActivity.this, mExamModel.getExamResult().getId(), mAnswer, mExamModel);
                } else {
                    ExamCardActivity.launchFullMark(ExamActivity.this, mExamModel.getExamResult().getId(), mAnswer, mExamModel, mExamId);
                }
            }
        });
        //答题卡
        img_dtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                if (fragmentList == null || fragmentList.length == 0) {
                    Toast.makeText(ExamActivity.this, "抱歉，暂未获取到试题数据~", Toast.LENGTH_SHORT).show();
                    return;
                }
                AnswerQuesPicPop answerQuesPicPop = new AnswerQuesPicPop(ExamActivity.this, fragmentList, mAnswer);
                answerQuesPicPop.showPop(new AnswerQuesPicPop.ClickCallback() {
                    @Override
                    public void selectWhich(int fraPosition, int quesposition, ExamModel.QuestionsBean bean) {
                        currentVpPage = fraPosition;
                        vpExamViewPager.setCurrentItem(currentVpPage);
                        ((BaseExamQuestionFragment) fragmentList[fraPosition]).changeTabIndex(quesposition);
                    }
                });
            }
        });
        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamActivity.this.finish();
            }
        });
        //获取考题
        mPresenter = new ExamPresenter(this, mExamId);
        mPresenter.subscribe();
        //初始化opencv
        initClassifier();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (authOkPop != null) {
                if (popAuthOk != null) {
                    if (popAuthOk.isShowing()) {
                        authOkPop.dismissPop();
                        popAuthOk = null;
                        authOkPop = null;
                        return true;
                    }
                }
            }
            if (authNoOkPop != null) {
                if (popAuthNoOk != null) {
                    if (popAuthNoOk.isShowing()) {
                        authNoOkPop.dismissPop();
                        popAuthNoOk = null;
                        authNoOkPop = null;
                        return true;
                    }
                }
            }
            if (authTakePicPop != null) {
                if (popAuthTakePic != null) {
                    if (popAuthTakePic.isShowing()) {
                        authTakePicPop.dismissPop();
                        popAuthTakePic = null;
                        authTakePicPop = null;
                        return true;
                    }
                }
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    //---------------------------------业务类接口---------------------------------
    @Override
    public void refreshView(ExamModel examModel) {
        mExamModel = examModel;
        //非计时考试模式
        if (mExamModel.getExam().getLength().equals("0")) {
            tvExamResitTimes.setVisibility(View.GONE);//隐藏左侧按钮
        }
        //计时考试模式
        else {
            int totalSecond = Integer.parseInt(mExamModel.getExam().getRemainingTime());
            CountDownTimer timer = new CountDownTimer(totalSecond * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String limitTime = getLimitedTime((int) millisUntilFinished / 1000);
                    SpannableString spannableString = new SpannableString(String.format("剩余时间：%s", limitTime));
                    if (millisUntilFinished <= 60 * 1000) {
                        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_red)), 5, 5 + limitTime.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_grey_text_color)), 5, 5 + limitTime.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    tvExamResitTimes.setText(spannableString);
                }

                @Override
                public void onFinish() {
                    mPresenter.submitExam(mExamModel.getExamResult().getId(), mAnswer);
                }
            };
            timer.start();
        }
        //标题
        tvToolbarTitle.setText(mExamModel.getExam().getName());
        //题目类型区分
        getQuestionType();
        //问答模式,弹窗展示问题
        if (isVideoQues) {
            //fixme 视频播放，暂时使用学习模块的视频播放
            task_container.setVisibility(View.VISIBLE);
            img_dtk.setVisibility(View.GONE);
            pstsExamTabs.setVisibility(View.GONE);
            vpExamViewPager.setVisibility(View.GONE);
            String videoType = "self";
            if (videoType.equals("self")) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment videoFragment = VideoPlayFragment.newInstance(url);
                transaction.replace(R.id.task_container, videoFragment, FRAGMENT_VIDEO_TAG);
                transaction.commitAllowingStateLoss();
            } else if (videoType.equals("youku")) {
            }
            return;
        }
        this.img_dtk.setVisibility(View.VISIBLE);//todo 答题卡显示
        this.ll_exam_bottom.setVisibility(View.VISIBLE);//todo 暂时不知道视频模式  是否需要答题卡 + 提交试卷按钮功能
        //非问答模式：tab展示
        fragmentAdapter = new ExamPagerAdapter(getSupportFragmentManager(), fragmentArrayList, titles, types);
        vpExamViewPager.setAdapter(fragmentAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        vpExamViewPager.setPageMargin(pageMargin);
        pstsExamTabs.setViewPager(vpExamViewPager);
        pstsExamTabs.setIndicatorColor(R.color.primary_color);
        vpExamViewPager.setOffscreenPageLimit(fragmentArrayList.length);
        pstsExamTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentVpPage = position;
                Toast.makeText(ExamActivity.this, "选中位置=" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
    }

    @Override
    public void timeFinishSubmit(JsonObject jsonObject) {
        ExamResultActivity.launch(ExamActivity.this, mExamModel.getExamResult().getId());
        ExamActivity.this.finish();
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

    //--------------------------------视频播放相关----------------------------
    //下载旧的照片 + 开始拍照认证弹窗
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isFirst) {
            return;
        }
        PoolDownLoadUtils.getInstance().downLoadApk(ExamActivity.this, "oldimg",
                "https://img-blog.csdnimg.cn/20210615151752124.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3MzIxMDk4,size_16,color_FFFFFF,t_70",
                new PoolDownLoadUtils.UpdateProgress() {
                    @Override
                    public void updatePro(int pro) {

                    }

                    @Override
                    public void updateOk(String filePath) {
                        bitmapLeft = BitmapFactory.decodeFile(filePath);
                        if (useTyy) {
                            String imgUrl = PhotoUtils.saveBitmapToFile(ExamActivity.this, bitmapLeft, PhotoUtils.getImgName());
                            String path = PhotoUtils.compress(imgUrl, PhotoUtils.getImgName());
                            oldBase64 = PhotoUtils.imageToBase64(path);
                            return;
                        }
                        getImageFaceValue(true);
                    }
                });
        isFirst = false;
        startPicAuth();
    }

    //开始拍照，弹窗
    private void startPicAuth() {
        authTakePicPop = new AuthTakePicPop(ExamActivity.this);
        popAuthTakePic = authTakePicPop.showPop(new AuthTakePicPop.ClickCallback() {
            @Override
            public void takePic() {
                if (PhotoUtils.hasSdcard()) {
                    imgTakeAddress = Environment.getExternalStorageDirectory().getPath() + "/" + PhotoUtils.getNetTimeC() + ".jpg";//网络时间
                    imageUri = Uri.fromFile(new File(imgTakeAddress));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(ExamActivity.this,
                                PhotoUtils.FILE_PATH, new File(imgTakeAddress));//PhotoUtils.TakePic.fileUri
                    }
                    PhotoUtils.takePicture(ExamActivity.this, imageUri, PhotoUtils.TakePic.CODE_CAMERA_REQUEST);
                } else {
                    showToast("设备没有SD卡！");
                }
            }
        });
    }

    //认证成功/否弹窗
    private void authSuccessPop(boolean isSuccess) {
        if (isSuccess) {
            authOkPop = new AuthOkPop(ExamActivity.this);
            popAuthOk = authOkPop.showPop(new AuthOkPop.ClickCallback() {
                @Override
                public void startks() {
                    Toast.makeText(ExamActivity.this, "开始考试", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        authNoOkPop = new AuthNoOkPop(ExamActivity.this);
        popAuthNoOk = authNoOkPop.showPop(new AuthNoOkPop.ClickCallback() {
            @Override
            public void tv_re_auth() {
                Toast.makeText(ExamActivity.this, "重新认证", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照完成回调
        if (requestCode == PhotoUtils.TakePic.CODE_CAMERA_REQUEST) {
            //拍照取消
            if (!PhotoUtils.judgeFielEs(new File(imgTakeAddress))) {
                return;
            }
            bitmapCurrent = BitmapFactory.decodeFile(imgTakeAddress);
            int degree = PhotoUtils.readPictureDegree(imgTakeAddress);
            if (degree != 0) {
                bitmapCurrent = PhotoUtils.rotaingImageView(degree, bitmapCurrent);//小米/三星机型图片角度 旋转回去
            }
            if (useTyy) {
                String imgUrl = PhotoUtils.saveBitmapToFile(ExamActivity.this, bitmapCurrent, PhotoUtils.getImgName());
                String path = PhotoUtils.compress(imgUrl, PhotoUtils.getImgName());
                String base64 = PhotoUtils.imageToBase64(path);
                if (!oldBase64.equals("") && !base64.equals("")) {
//                    showProcessDialog();
//                    String timeS = System.currentTimeMillis() + "";
//                    String sign = DigestUtils.getMd5Value("eb792643147d3026489ce6c9e5384332" +
//                            "8ec2c669d077538ffb39f0a6fc70ec48" +
//                            timeS +
//                            "/v1/aiop/api/2f7awxekgvls/face/compare/PERSON/person/compareFromBase64" +
//                            new Gson().toJson(new PeopleABean(base64, oldBase64))
//                    );
//                    NewHttpUtils.getInstance()
//                            .setBaseUrl("https://ai.ctyun.cn/v1/aiop/api/2f7awxekgvls/face/compare/")
//                            .createApiPeopleAuth(SpStudyHomeApi.class, base64, oldBase64)
//                            .compareFromBase64(base64, oldBase64,
//                                    "eb792643147d3026489ce6c9e5384332",
//                                    timeS,
//                                    "application/json",
//                                    sign)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new SubscriberProcessor<CompareFromBase64>() {
//
//                                @Override
//                                public void onError(String message) {
//                                    hideProcessDialog();
//                                }
//
//                                @Override
//                                public void onNext(CompareFromBase64 compareFromBase64) {
//                                    hideProcessDialog();
//                                }
//                            });
                    //FIXME https://blog.csdn.net/u013620306/article/details/108201072
                    OkHttpClient client = new OkHttpClient();
                    //拦截器
                    HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Log.e("日志拦截:", message);
                        }
                    });
                    loggingInterceptor.setLevel(level);
                    client.newBuilder()
                            .readTimeout(100, TimeUnit.SECONDS)
                            .writeTimeout(100, TimeUnit.SECONDS)
                            .connectTimeout(100, TimeUnit.SECONDS)
                            .addInterceptor(loggingInterceptor)
                            .build();
                    //参数传递
//                    APPKey：eb792643147d3026489ce6c9e5384332
//                    AppSecret：8ec2c669d077538ffb39f0a6fc70ec48
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("img1Base64", oldBase64);
                    builder.add("img2Base64", base64);
                    String timeS = System.currentTimeMillis() + "";
                    String appkey = "eb792643147d3026489ce6c9e5384332";
                    String appSc = "8ec2c669d077538ffb39f0a6fc70ec48";
                    Request request = new Request.Builder()
                            .url("https://ai.ctyun.cn/v1/aiop/api/2f7awxekgvls/face/compare/PERSON/person/compareFromBase64")
                            .post(builder.build())
                            .addHeader("AppKey", appkey)
                            .addHeader("Timestamp", timeS)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Sign", DigestUtils.getMd5Value(appkey + appSc + timeS +
                                    "/v1/aiop/api/2f7awxekgvls/face/compare/PERSON/person/compareFromBase64" +
                                    new Gson().toJson(new PeopleABean(oldBase64, base64))))
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            Log.e("测试", "onFailure=" + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.e("测试", "onResponse=" + response.body().string());
                            if (!response.isSuccessful()) {
                                throw new IOException("服务器端错误: " + response);
                            }
                        }
                    });
                }
                return;
            }
            //使用opencv识别
            getImageFaceValue(false);
        }
    }

    //获取人脸灰度特征值
    private void getImageFaceValue(boolean isOld) {
        Bitmap mBitmap1 = isOld ? bitmapLeft : bitmapCurrent;
        Mat mat1 = new Mat();
        Mat mat11 = new Mat();
        Utils.bitmapToMat(mBitmap1, mat1);
        Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
        Rect[] object = detectObjectImage(mat11);//图片上只有一个人，当然也就只能识别出一张人脸，所以我们直接取第一个就行了
        if (object != null && object.length > 0) {
            Mat mat = mat11.submat(object[0]);
            if (isOld) {
                mGrayFace1 = mat;
            } else {
                mGrayFace2 = mat;
                if (mGrayFace1 == null) {
                    Toast.makeText(this, "抱歉,因网络异常暂未获取到您的初始照片信息，请退出重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProcessDialog();
                mGrayFace1.convertTo(mGrayFace1, CvType.CV_32F);
                Mat mat2 = new Mat();
                Imgproc.resize(mGrayFace2, mat2, new Size(mGrayFace1.cols(), mGrayFace1.rows()));
                mat2.convertTo(mat2, CvType.CV_32F);
                double target = Imgproc.compareHist(mGrayFace1, mat2, Imgproc.CV_COMP_CORREL);
                hideProcessDialog();
                Toast.makeText(getApplicationContext(), "相似度：" + target, Toast.LENGTH_SHORT).show();
            }
//            final Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(mat, bitmap);
        }
    }

    public org.opencv.core.Rect[] detectObjectImage(Mat gray) {
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(gray, faces);
        return faces.toArray();
    }

    //初始化opencv
    private void initClassifier() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------工具类---------------------------------
    //倒计时题目，时间格式化
    private String getLimitedTime(int limitedTime) {
        int hh = limitedTime / 3600;
        int mm = limitedTime % 3600 / 60;
        int ss = limitedTime % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        }
        return strTemp;
    }

    //题目类型区分
    private void getQuestionType() {
        titles = new String[mExamModel.getExamInfo().size()];
        fragmentArrayList = new String[mExamModel.getExamInfo().size()];
        types = new String[mExamModel.getExamInfo().size()];
        Iterator iterator = mExamModel.getExamInfo().keySet().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            QuestionType questionType = (QuestionType) iterator.next();
            titles[count] = getTitle(questionType.toString());
            fragmentArrayList[count] = getFragmentArray(questionType.toString());
            types[count] = questionType.toString();
            count += 1;
        }
    }

    //题目存储到各个题型
    private List<ExamModel.QuestionsBean> getQuestionList(String type) {
        List<ExamModel.QuestionsBean> list = new ArrayList<>();
        for (ExamModel.QuestionsBean questionsBean : mExamModel.getQuestions()) {
            if (questionsBean.getType().equals(type)) {
                list.add(questionsBean);
            }
        }
        return list;
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

    private String getFragmentArray(String type) {
        if (type.equals("single_choice")) {
            return "ExamSingleChoiceFragment";
        }
        if (type.equals("choice")) {
            return "ExamChoiceFragment";
        }
        if (type.equals("essay")) {
            return "ExamEssayFragment";
        }
        if (type.equals("uncertain_choice")) {
            return "ExamUncertainChoiceFragment";
        }
        if (type.equals("determine")) {
            return "ExamDetermineFragment";
        }
        if (type.equals("fill")) {
            return "ExamFillFragment";
        }
        return "";
    }

    public int getSelectTestQuestionIndex() {
        return mSelectTestQuestionIndex;
    }

    public void setSelectTestQuestionIndex(int currentTestQuestionIndex) {
        this.mSelectTestQuestionIndex = currentTestQuestionIndex;
    }

    public String getSelectFragmentName() {
        return mSelectFragmentName;
    }

    public ExamAnswer getAnswer() {
        return mAnswer;
    }

    public class ExamPagerAdapter extends FragmentPagerAdapter {

        private String[] fragments;
        private String[] titles;
        private String[] types;

        public ExamPagerAdapter(FragmentManager fm, String[] fragments, String[] titles, String[] types) {
            super(fm);
            this.titles = titles;
            this.fragments = fragments;
            this.types = types;
            //fixme 新增存储fragment容器
            fragmentList = new Fragment[this.fragments.length];
            for (int position = 0; position < this.fragments.length; position++) {
                ExamQuestions examQuestions = new ExamQuestions(getQuestionList(types[position]), types[position]);
                //单选题
                if (fragments[position].equals("ExamSingleChoiceFragment")) {
                    fragmentList[position] = SingleChoiceFragment.getInstance(examQuestions);
                }
                //多选题
                else if (fragments[position].equals("ExamChoiceFragment")) {
                    fragmentList[position] = ChoiceFragment.getInstance(examQuestions);
                }
                //问答题
                else if (fragments[position].equals("ExamEssayFragment")) {
                    fragmentList[position] = EssayFragment.getInstance(examQuestions);
                }
                //不定项选择题
                else if (fragments[position].equals("ExamUncertainChoiceFragment")) {
                    fragmentList[position] = UncertainChoiceFragment.getInstance(examQuestions);
                }
                //判断题
                else if (fragments[position].equals("ExamDetermineFragment")) {
                    fragmentList[position] = DetermineFragment.getInstance(examQuestions);
                }
                //填空题
                else if (fragments[position].equals("ExamFillFragment")) {
                    fragmentList[position] = FillFragment.getInstance(examQuestions);
                }
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(final int position) {
            /*final ExamQuestions examQuestions = new ExamQuestions(getQuestionList(types[position]), types[position]);
            Fragment fragment = EdusohoApp.app.mEngine.runPluginWithFragment(
                    fragments[position], ExamActivity.this, new PluginFragmentCallback() {
                        @Override
                        public void setArguments(Bundle bundle) {
                            bundle.putSerializable("question_list", examQuestions);
                        }
                    });
            return fragment;*/
            return fragmentList[position];
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveStickyNessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.CHANGE_EXAM_ANSWER) {
            EventBus.getDefault().removeAllStickyEvents();
            ExamAnswer.AnswersBean answersBean = (ExamAnswer.AnswersBean) messageEvent.getMessageBody();
            ExamAnswer newAnswers = mAnswer;
            for (ExamAnswer.AnswersBean oldAnswer : mAnswer.getAnswers()) {
                if (oldAnswer.getQuestionId().equals(answersBean.getQuestionId())) {
                    newAnswers.getAnswers().remove(oldAnswer);
                    break;
                }
            }
            newAnswers.getAnswers().add(answersBean);
            mAnswer = newAnswers;
            for (int i = 0; i < mAnswer.getAnswers().size(); i++) {
                Log.e("测试已答题目数据", "答题id=" + mAnswer.getAnswers().get(i).getQuestionId() + "   答案=" + mAnswer.getAnswers().get(i).getAnswer());
            }
        } else if (messageEvent.getType() == MessageEvent.CLICK_QUESTION_CARD_ITEM) {
            EventBus.getDefault().removeAllStickyEvents();
            int[] selectItems = (int[]) messageEvent.getMessageBody();
            if (selectItems.length >= 2) {
                vpExamViewPager.setCurrentItem(selectItems[0]);
                mSelectFragmentName = fragmentAdapter.getItem(selectItems[0]).getClass().getSimpleName();
                setSelectTestQuestionIndex(selectItems[1]);
                Log.e("测试已答题目数据", "mSelectFragmentName=" + mSelectFragmentName + "   mSelectTestQuestionIndex=" + mSelectTestQuestionIndex);
            }
        }
    }
}
