package com.edusoho.kuozhi.clean.module.course.tasks.ppt;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskFinishType;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskLearningRecordHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.photo.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import photoview.PhotoView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.View.GONE;
import static com.edusoho.kuozhi.clean.bean.TaskFinishType.END;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.PPT;

public class PPTLessonFragment extends BaseFragment<PPTLessonContract.Presenter> implements PPTLessonContract.View {

    public static final String COURSE_TASK    = "course_task";
    public static final String IS_MEMBER      = "is_member";
    public static final String COURSE_PROJECT = "course_project";
    public static final String PPT_URLS       = "ppt_urls";
    public static final String TASK_TITLE     = "task_title";
    private TextView           mStartPageView;
    private EduSohoNewIconView mScreenView;
    private HackyViewPager     pptViewPager;
    private TextView           mTitle;
    private TextView           mBack;
    private Toolbar            mToolbar;
    private TextView           mTaskFinish;
    private int                mCurrentPPTPosition;
    private CourseTask         mCourseTask;
    private CourseProject      mCourseProject;
    private String             mTaskTitle;
    private List<String>       mPPTUrls;
    private boolean            mIsMember;
    private TaskFinishHelper   mTaskFinishHelper;
    private boolean            mShowDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.ppt_lesson_layout, container, false);
        mStartPageView = (TextView) view.findViewById(R.id.ppt_page_start);
        mScreenView = (EduSohoNewIconView) view.findViewById(R.id.ppt_page_screen);
        pptViewPager = (HackyViewPager) view.findViewById(R.id.ppt_viewpager);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mTitle = (TextView) view.findViewById(R.id.tv_toolbar_title);
        mBack = (TextView) view.findViewById(R.id.iv_back);
        mTaskFinish = (TextView) view.findViewById(R.id.tv_finish_task);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mPPTUrls = (ArrayList) bundle.getStringArrayList(PPT_URLS);
        mTaskTitle = bundle.getString(TASK_TITLE);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mIsMember = bundle.getBoolean(IS_MEMBER);
        if (mCourseTask != null && mCourseTask.id != 0 && getActivity() != null) {
            mCurrentPPTPosition = TaskLearningRecordHelper.get(new TaskLearningRecordHelper.TaskLearningRecord(
                    EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id, mCourseTask.id), getActivity());
            mTaskTitle = mCourseTask.title;
            mPresenter = new PPTLessonPresenter(mCourseTask.id, this);
            mPresenter.subscribe();
            final TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                    .setCourseId(mCourseProject.id)
                    .setCourseTask(mCourseTask)
                    .setEnableFinish(mCourseProject.enableFinish);
            mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                    .setActionListener(new TaskFinishActionHelper() {
                        @Override
                        public void onFinish(TaskEvent taskEvent) {
                            EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask,
                                    MessageEvent.FINISH_TASK_SUCCESS));
                            mCourseTask.result = taskEvent.result;
                            setTaskFinishButtonBackground(mCourseTask);
                            if (mCourseProject.enableFinish == 0 || mShowDialog) {
                                TaskFinishDialog
                                        .newInstance(taskEvent, mCourseTask, mCourseProject)
                                        .show(getActivity().getSupportFragmentManager(), "TaskFinishDialog");
                            }
                        }
                    });
            if (mIsMember) {
                setTaskFinishButtonBackground(mCourseTask);
                mTaskFinish.setVisibility(View.VISIBLE);
            } else {
                mTaskFinish.setVisibility(View.INVISIBLE);
            }
            mTaskFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsMember && !mCourseTask.isFinish()) {
                        mShowDialog = true;
                        mTaskFinishHelper.finish();
                    }
                }
            });
        }
        if (mPPTUrls != null) {
            showPTT(mPPTUrls);
        } else if (mPresenter != null) {
            mPresenter.subscribe();
        }
        setTitle(mTaskTitle);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        getView().setFocusableInTouchMode(true);
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return true;
                }
                return false;
            }
        });
    }

    private void setTaskFinishButtonBackground(CourseTask courseTask) {
        if (courseTask != null && courseTask.result != null && TaskResultEnum.FINISH.toString().equals(courseTask.result.status)) {
            mTaskFinish.setTextColor(getActivity().getResources().getColor(R.color.disabled2_hint_color));
            mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_finish_left_icon, 0, 0, 0);
            mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_finish_button_bg));
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTaskFinish.setTextColor(getActivity().getResources().getColor(R.color.disabled2_hint_color));
                mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_bg));
            } else {
                mTaskFinish.setTextColor(getActivity().getResources().getColor(R.color.primary_font_color));
                mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_grey_bg));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCourseTask != null && mCourseTask.id != 0 && getActivity() != null) {
            TaskLearningRecordHelper.put(new TaskLearningRecordHelper.TaskLearningRecord(
                    EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id, mCourseTask.id), mCurrentPPTPosition, getActivity());
        }
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void showPTT(List<String> pptUrls) {
        if (pptUrls == null || pptUrls.isEmpty()) {
            ToastUtils.show(getActivity(), "课时暂无PPT!");
            return;
        }
        PPTPageAdapter adapter = new PPTPageAdapter(pptUrls);
        mStartPageView.setText(String.format("%d/%d", mCurrentPPTPosition + 1, pptUrls.size()));
        pptViewPager.setAdapter(adapter);
        pptViewPager.setOnPageChangeListener(adapter);
        pptViewPager.setCurrentItem(mCurrentPPTPosition);
        mScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getActivity().getRequestedOrientation();
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mToolbar.setVisibility(View.GONE);
                    mScreenView.setText(R.string.font_shrink_screen);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mToolbar.setVisibility(View.VISIBLE);
                    mScreenView.setText(R.string.font_full_screen);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            mScreenView.setText(R.string.font_shrink_screen);
        } else {
            mScreenView.setText(R.string.font_full_screen);
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    public class PPTPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private List<String> mImages;

        public PPTPageAdapter(List<String> images) {
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.ppt_lesson_item, container, false);
            final PhotoView photoView = (PhotoView) itemView.findViewById(R.id.ppt_lesson_image);
            photoView.setEnabled(false);
            ImageLoader.getInstance().displayImage(mImages.get(position), photoView, EdusohoApp.app.mOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null) {
                        return;
                    }
                    itemView.findViewById(R.id.ppt_lesson_progress).setVisibility(GONE);
                    photoView.setEnabled(true);
                    photoView.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
            container.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == mImages.size() - 1 && getActivity() != null) {
                if (mIsMember && mCourseTask != null && !mCourseTask.isFinish() && PPT == TaskTypeEnum.fromString(mCourseTask.type)
                        && END == TaskFinishType.fromString(mCourseTask.activity.finishType)) {
                    mTaskFinishHelper.stickyFinish();
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPPTPosition = position;
            mStartPageView.setText((position + 1) + "/" + mImages.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
