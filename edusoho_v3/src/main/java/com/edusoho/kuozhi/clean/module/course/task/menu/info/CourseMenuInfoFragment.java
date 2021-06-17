package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.dialog.ServicesDialog;
import com.edusoho.kuozhi.clean.module.course.info.RelativeCourseAdapter;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.util.List;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoFragment extends BaseFragment<CourseMenuInfoContract.Presenter> implements CourseMenuInfoContract.View {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    public static final String COURSE_PROGRESS      = "CourseProgress";

    private android.view.View      mBack;
    private android.view.View      mShare;
    private TextView               mCourseTitle;
    private TextView               mMyCourseProgress;
    private TextView               mCourseProgress;
    private TextView               mDeadline;
    private ProgressBar            mMyCourseProgressRate;
    private ProgressBar            mCourseProgressRate;
    private ImageView              mCourseScheduleBackground;
    private View                   mServicesLayout;
    private FlowLayout             mPromise;
    private View                   mPromiseLine;
    private TextView               mTeacherName;
    private TextView               mTeacherTitle;
    private CircularImageView      mTeacherAvatar;
    private RecyclerView           mRelativeCourses;
    private CourseProject          mCourseProject;
    private CourseLearningProgress mCourseLearningProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mCourseLearningProgress = (CourseLearningProgress) bundle.getSerializable(COURSE_PROGRESS);
    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.menu_course_info_layout, container, false);
    }

    @Override
    public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mBack = view.findViewById(R.id.iv_back);
        mShare = view.findViewById(R.id.icon_share);
        mCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
        mDeadline = (TextView) view.findViewById(R.id.tv_deadline);
        mMyCourseProgress = (TextView) view.findViewById(R.id.tv_my_course_progress);
        mCourseProgress = (TextView) view.findViewById(R.id.tv_course_progress);
        mMyCourseProgressRate = (ProgressBar) view.findViewById(R.id.my_course_progress_rate);
        mCourseProgressRate = (ProgressBar) view.findViewById(R.id.course_progress_rate);
        mCourseScheduleBackground = (ImageView) view.findViewById(R.id.tv_course_schedule_bg);
        mPromise = (FlowLayout) view.findViewById(R.id.fl_promise_layout);
        mPromiseLine = view.findViewById(R.id.promise_line);
        mServicesLayout = view.findViewById(R.id.ll_services_layout);
        mTeacherName = (TextView) view.findViewById(R.id.tv_teacher_name);
        mTeacherTitle = (TextView) view.findViewById(R.id.tv_teacher_title);
        mTeacherAvatar = (CircularImageView) view.findViewById(R.id.civ_teacher_avatar);
        mRelativeCourses = (RecyclerView) view.findViewById(R.id.rv_relative_courses);

        if (CourseProject.ExpiryMode.DATE.toString().equals(mCourseProject.learningExpiryDate.expiryMode)) {
            mCourseProgress.setVisibility(android.view.View.VISIBLE);
            mCourseProgressRate.setVisibility(android.view.View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , AppUtil.dp2px(getActivity(), 204));
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mCourseScheduleBackground.setLayoutParams(lp);
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                getActivity().finish();

            }
        });

        mShare.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                share();
            }
        });

        mCourseTitle.setText(mCourseProject.getTitle());
        showCourseProgress(mCourseLearningProgress);
        mPresenter = new CourseMenuInfoPresenter(this, mCourseProject, mCourseLearningProgress);
        mPresenter.subscribe();
    }

    private void share() {
        try {
            ShareHelper
                    .builder()
                    .init(getActivity())
                    .setTitle(mCourseProject.getTitle())
                    .setText(mCourseProject.summary)
                    .setUrl(EdusohoApp.app.host + "/course/" + mCourseProject.id)
                    .setImageUrl(mCourseProject.courseSet.cover.middle)
                    .build()
                    .share();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCourseProgress(CourseLearningProgress progress) {
        try {
            mMyCourseProgress.setText(String.format(getString(R.string.task_finish_progress), progress.taskResultCount, progress.taskCount));
            mMyCourseProgressRate.setProgress(progress.taskCount == 0 ? 0 : progress.taskResultCount * 100 / progress.taskCount);
            mCourseProgress.setText(String.format(getString(R.string.course_plan_progress), progress.planStudyTaskCount, progress.taskCount));
            mCourseProgressRate.setProgress(progress.taskCount == 0 ? 0 : progress.planStudyTaskCount * 100 / progress.taskCount);
            mDeadline.setText(String.format(getString(R.string.course_progress_deadline),
                    "0".equals(progress.member.deadline) ? getString(R.string.permnent_expired) : TimeUtils.getStringTime(progress.member.deadline, "yyyy.MM.dd")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showServices(final CourseProject.Service[] services) {
        if (services == null || services.length == 0) {
            mPromiseLine.setVisibility(View.GONE);
            mServicesLayout.setVisibility(View.GONE);
            return;
        }
        mServicesLayout.setVisibility(View.VISIBLE);
        for (CourseProject.Service service : services) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_course_project_promise, null);
            ((TextView) view.findViewById(R.id.tv_promise)).setText(service.fullName);
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            mPromise.addView(view);
        }

        mServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServicesDialog.newInstance(services).show(getFragmentManager(), "ServicesDialog");
            }
        });
    }

    @Override
    public void showTeacher(final Teacher teacher) {
        mTeacherName.setText(teacher.nickname);
        mTeacherTitle.setText(teacher.title);
        ImageLoader.getInstance().displayImage(teacher.avatar.middle, mTeacherAvatar, EdusohoApp.app.mAvatarOptions);
        mTeacherAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.USER_PROFILE, teacher.id));
                CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                        , getContext(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });
    }

    @Override
    public void showRelativeCourseProjects(List<CourseProject> courseList, List<VipInfo> vipInfos) {
        mRelativeCourses.setHasFixedSize(true);
        mRelativeCourses.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRelativeCourses.setItemAnimator(new DefaultItemAnimator());
        mRelativeCourses.setNestedScrollingEnabled(false);
        final RelativeCourseAdapter relativeCourseAdapter = new RelativeCourseAdapter(getActivity(), courseList, vipInfos);
        mRelativeCourses.setAdapter(relativeCourseAdapter);
        ItemClickSupport.addTo(mRelativeCourses).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                CourseProjectActivity.launch(getActivity(), relativeCourseAdapter.getItem(position).id);
            }
        });
    }
}
