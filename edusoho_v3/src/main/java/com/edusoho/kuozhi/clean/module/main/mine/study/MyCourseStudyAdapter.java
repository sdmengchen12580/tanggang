package com.edusoho.kuozhi.clean.module.main.mine.study;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;
import com.edusoho.kuozhi.clean.module.main.mine.MineFragment;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyCourseStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private             int mCourseType        = 1;
    public static final int COURSE_TYPE_NORMAL = 2;
    public static final int COURSE_TYPE_LIVE   = 3;

    private static final int EMPTY     = 0;
    private static final int NOT_EMPTY = 1;
    private int mCurrentDataStatus;

    private List<StudyCourse> mNormalCourses;
    private List<Study>       mLiveCourses;


    MyCourseStudyAdapter(Context context) {
        this.mContext = context;
        mNormalCourses = new ArrayList<>();
        mLiveCourses = new ArrayList<>();
    }

    void setNormalCourses(List<StudyCourse> list) {
        mCourseType = COURSE_TYPE_NORMAL;
        mNormalCourses = list;
    }

    void setLiveCourses(List<Study> list) {
        mCourseType = COURSE_TYPE_LIVE;
        mLiveCourses = list;
    }

    public void clear() {
        mNormalCourses.clear();
        mLiveCourses.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentDataStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, parent, false);
            return new MyStudyFragment.CourseStudyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
            return new MineFragment.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            MyStudyFragment.CourseStudyViewHolder courseStudyViewHolder = (MyStudyFragment.CourseStudyViewHolder) viewHolder;
            courseStudyViewHolder.layoutFrom.setVisibility(View.GONE);
            courseStudyViewHolder.tvStudyState.setText("");
            switch (mCourseType) {
                case COURSE_TYPE_NORMAL:
                    final StudyCourse studyCourse = mNormalCourses.get(position);
                    ImageLoader.getInstance().displayImage(studyCourse.courseSet.cover.large, courseStudyViewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    courseStudyViewHolder.tvTitle.setText(String.valueOf(studyCourse.title));
                    setProgressStr(studyCourse.learnedCompulsoryTaskNum, studyCourse.compulsoryTaskNum, courseStudyViewHolder.tvStudyState);
                    courseStudyViewHolder.rLayoutItem.setTag(studyCourse);
                    courseStudyViewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
                    courseStudyViewHolder.tvMore.setTag(studyCourse);
                    courseStudyViewHolder.tvMore.setOnClickListener(getMoreClickListener());
                    courseStudyViewHolder.layoutFrom.setVisibility(View.GONE);
                    courseStudyViewHolder.courseSetName.setText(studyCourse.courseSet.title);
                    courseStudyViewHolder.layoutLive.setVisibility("normal".equals(studyCourse.courseSet.type) ? View.GONE : View.VISIBLE);
                    break;
                case COURSE_TYPE_LIVE:
                    final Study study = mLiveCourses.get(position);
                    ImageLoader.getInstance().displayImage(study.cover.large, courseStudyViewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    courseStudyViewHolder.courseSetName.setText(String.valueOf(study.title));
                    courseStudyViewHolder.rLayoutItem.setTag(study);
                    courseStudyViewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
                    courseStudyViewHolder.layoutLive.setVisibility(View.VISIBLE);
                    courseStudyViewHolder.tvMore.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (mCourseType) {
            case COURSE_TYPE_NORMAL:
                if (mNormalCourses != null && mNormalCourses.size() != 0) {
                    mCurrentDataStatus = NOT_EMPTY;
                    return mNormalCourses.size();
                }
                break;
            case COURSE_TYPE_LIVE:
                if (mLiveCourses != null && mLiveCourses.size() != 0) {
                    mCurrentDataStatus = NOT_EMPTY;
                    return mLiveCourses.size();
                }
                break;
        }
        mCurrentDataStatus = EMPTY;
        return 1;
    }

    private View.OnClickListener getCourseViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getTag() instanceof StudyCourse ? ((StudyCourse) v.getTag()).id : ((Study) v.getTag()).id;
                if (v.getTag() instanceof StudyCourse) {
                    CourseProjectActivity.launch(mContext, id);
                } else {
                    CourseUnLearnActivity.launch(mContext, id);
                }
            }
        };
    }

    private View.OnClickListener getMoreClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "i_learn_threePoints");
                final Object data = v.getTag();
                MoreDialog dialog = new MoreDialog(mContext);
                dialog.init("退出课程", new MoreDialog.MoreCallBack() {
                    @Override
                    public void onMoveClick(View v, final Dialog outerDialog) {
                        final int id = data instanceof StudyCourse ? ((StudyCourse) data).id : ((Study) data).id;
                        ESAlertDialog.newInstance("确认退出课程",
                                mContext.getString(R.string.delete_course),
                                mContext.getString(R.string.course_exit_confirm),
                                mContext.getString(R.string.course_exit_cancel))
                                .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                                    @Override
                                    public void onClick(DialogFragment dialog) {
                                        exitCourse(id, outerDialog, data);
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                                    @Override
                                    public void onClick(DialogFragment dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show(((BaseActivity) mContext).getSupportFragmentManager(), "ESAlertDialog");
                    }

                    @Override
                    public void onShareClick(View v, Dialog dialog) {
                        final ShareHelper.Builder shareBuilder = ShareHelper.builder();
                        try {
                            if (data instanceof StudyCourse) {
                                StudyCourse studyCourse = (StudyCourse) data;
                                shareBuilder
                                        .init(mContext)
                                        .setTitle(studyCourse.title)
                                        .setText(studyCourse.courseSet.summary)
                                        .setUrl(EdusohoApp.app.host + "/course/" + studyCourse.id)
                                        .setImageUrl(studyCourse.courseSet.cover.middle)
                                        .build()
                                        .share();
                            } else {
                                Study study = (Study) data;
                                shareBuilder
                                        .init(mContext)
                                        .setTitle(study.title)
                                        .setText(study.summary)
                                        .setUrl(EdusohoApp.app.host + "/course/" + study.id)
                                        .setImageUrl(study.cover.middle)
                                        .build()
                                        .share();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelClick(View v, Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
            }
        };
    }

    private void exitCourse(int id, final Dialog dialog, final Object data) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .exitCourse(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String e) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get("success").getAsBoolean()) {
                            CommonUtil.shortToast(mContext, "退出成功");
                            if (data instanceof StudyCourse) {
                                StudyCourse studyCourse = (StudyCourse) data;
                                if (mNormalCourses.contains(studyCourse)) {
                                    mNormalCourses.remove(studyCourse);
                                    clearCoursesCache(studyCourse.id);
                                }
                            } else {
                                Study study = (Study) data;
                                if (mLiveCourses.contains(study)) {
                                    mLiveCourses.remove(study);
                                }
                            }
                            dialog.dismiss();
                            notifyDataSetChanged();
                        } else {
                            CommonUtil.shortToast(mContext, "退出失败");
                        }
                    }
                });
    }


    private void clearCoursesCache(int... courseIds) {
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        new CourseCacheHelper(mContext, school.getDomain(), user.id).clearLocalCacheByCourseId(courseIds);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private void setProgressStr(int now, int total, TextView view) {
        String str;
        if (now == 0) {
            str = "未开始学习";
            view.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
        } else if (now == total) {
            str = "已学完";
            view.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        } else {
            str = String.format("已学习%s/%s课", now, total);
            view.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        }
        view.setText(str);
    }
}
