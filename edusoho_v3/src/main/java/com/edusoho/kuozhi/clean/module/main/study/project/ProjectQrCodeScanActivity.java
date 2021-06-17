package com.edusoho.kuozhi.clean.module.main.study.project;


import android.graphics.Bitmap;

import com.edusoho.kuozhi.clean.api.CommonApi;
import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.main.study.survey.EvaluationSurveyActivity;
import com.edusoho.kuozhi.clean.module.main.study.survey.SurveyDetailActivity;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.zxing.Result;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectQrCodeScanActivity extends CaptureActivity {

    private static final String OFFLINE_ACTIVITY_KEY        = "offline_activity";
    private static final String OFFLINE_COURSE_KEY          = "offline_course";
    private static final String SURVEY_KEY                  = "survey";
    private static final String COURSE_KEY                  = "course";
    private static final String QUESTIONNAIRE               = "questionnaire";
    private static final String OFFLINECOURSE_QUESTIONNAIRE = "offlineCourseQuestionnaire";
    private static final String COURSE                      = "course";

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        String qrResult = rawResult.getText();
        if (qrResult.contains(SURVEY_KEY)) {
            String[] urlSplit = qrResult.split("/");
            final String surveyId = urlSplit[urlSplit.length - 2];
            HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(MyStudyApi.class)
                    .doSurvey(surveyId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberProcessor<SurveyModel>() {
                        @Override
                        public void onCompleted() {
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            ToastUtils.show(ProjectQrCodeScanActivity.this, message);
                            finish();
                        }

                        @Override
                        public void onNext(SurveyModel surveyModel) {
                            if (QUESTIONNAIRE.equals(surveyModel.getSurvey().getType())) {
                                SurveyDetailActivity.launch(ProjectQrCodeScanActivity.this,
                                        surveyId);
                            } else if (OFFLINECOURSE_QUESTIONNAIRE.equals(surveyModel.getSurvey().getType()) ||
                                    COURSE.equals(surveyModel.getSurvey().getType())) {
                                EvaluationSurveyActivity.launch(ProjectQrCodeScanActivity.this, surveyId, 0, 0);
                            }
                        }
                    });
        } else {
            HttpUtils.getInstance()
                    .createApi(CommonApi.class)
                    .requestUrl(qrResult)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberProcessor<ResponseBody>() {

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            finish();
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            super.onNext(responseBody);
                        }

                        @Override
                        public void onError(String url) {
                            if (url.contains(OFFLINE_ACTIVITY_KEY)) {
                                ProjectSignInActivity.launch(ProjectQrCodeScanActivity.this, url);
                            } else if (url.contains(OFFLINE_COURSE_KEY)) {
                                ProjectSignInActivity.launch(ProjectQrCodeScanActivity.this, url);
                            } else if (url.contains(COURSE_KEY)) {
                                String[] urlSplit = url.split("/");
                                CourseProjectActivity.launch(ProjectQrCodeScanActivity.this,
                                        Integer.parseInt(urlSplit[urlSplit.length - 1]));
                            }
                            finish();
                        }
                    });
        }
    }
}
