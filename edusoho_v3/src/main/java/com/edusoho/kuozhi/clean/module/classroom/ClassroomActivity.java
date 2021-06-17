package com.edusoho.kuozhi.clean.module.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmClassOrderActivity;
import com.edusoho.kuozhi.clean.utils.biz.CourseHelper;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.ReviewActivity;
import com.edusoho.kuozhi.v3.ui.ThreadCreateActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by DF on 2017/5/23.
 */
public class ClassroomActivity extends BaseStudyDetailActivity<ClassroomContract.Presenter> implements View.OnClickListener, CourseStateCallback, ClassroomContract.View {
    private int       mClassroomId;
    public  Classroom mClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mClassroomId = intent.getIntExtra(Const.CLASSROOM_ID, 0);
        if (mClassroomId == 0) {
            finish();
            return;
        }
        mMediaViewHeight = AppUtil.px2dp(this, (float) AppUtil.getWidthPx(this) / 4f * 3f);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        getIntent().getExtras().putString("source", mClassroom != null ? mClassroom.title : null);
        super.initView();
        mTvAdd.setText(R.string.txt_add_class);
        mIvGrade.setVisibility(View.GONE);
        mPresenter = new ClassroomPresenter(mClassroomId, this);
    }

    protected String[] getFragmentArray() {
        return new String[]{
                "ClassroomDetailFragment", "ClassCatalogFragment", "CourseDiscussFragment"
        };
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void handlerCourseExpired() {
    }

    @Override
    public void refresh() {
        initData();
    }

    private void saveClassRoomToCache(Classroom classroom) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getBaseContext());
        sqliteUtil.saveLocalCache(Const.CACHE_COURSE_TYPE, "classroom-" + classroom.id, new Gson().toJson(classroom));
    }

    protected void initData() {
        mPresenter.subscribe();
    }

    @Override
    protected void grade() {
        ((EdusohoApp) getApplication()).mEngine.runNormalPlugin("ReviewActivity", this, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ReviewActivity.TYPE, ReviewActivity.TYPE_CLASSROOM);
                startIntent.putExtra(ReviewActivity.ID, mClassroomId);
            }
        });
    }

    @Override
    protected void refreshView() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_classroom)
                .showImageOnFail(R.drawable.default_classroom)
                .showImageOnLoading(R.drawable.default_classroom)
                .build();
        ImageLoader.getInstance().displayImage(
                mClassroom.cover.large,
                mIvBackGraound, imageOptions);
    }

    @Override
    protected void goClass() {
        if (mClassroom == null) {
            return;
        }
        CoreEngine.create(this).runNormalPlugin("ClassroomDiscussActivity", this, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ClassroomDiscussActivity.FROM_ID, mClassroomId);
                startIntent.putExtra(ClassroomDiscussActivity.FROM_NAME, mClassroom.title);
            }
        });
    }

    @Override
    protected void consult() {
        if (((EdusohoApp) getApplication()).loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        CourseDetailModel.getTeacher(mClassroomId, new ResponseCallbackListener<Teacher[]>() {
            @Override
            public void onSuccess(Teacher[] data) {
                if (data.length == 0) {
                    CommonUtil.shortToast(ClassroomActivity.this, "班级目前没有老师");
                } else {
                    startImChat(data[0]);
                }
            }

            @Override
            public void onFailure(String code, String message) {
                CommonUtil.shortToast(ClassroomActivity.this, "获取信息失败");
            }
        });
    }

    private void startImChat(final Teacher teacher) {
        CoreEngine.create(this).runNormalPlugin("ImChatActivity", this, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    @Override
    protected void add() {
        if (TextUtils.isEmpty(EdusohoApp.app.token)) {
            CourseUtil.notLogin();
            return;
        }
        if (CourseHelper.ONLY_VIP_JOIN_WAY.equals(mClassroom.access.code)) {
            mPresenter.showVipInfo(mClassroom.vipLevelId);
            return;
        }
        if (mClassroomId != 0) {
            mPresenter.checkClassInfo(mClassroom);
        }
    }


    @Override
    public void showToast(int resId, String param) {
        Toast.makeText(this, String.format(getString(resId, param)), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void share() {
        if (mClassroom == null) {
            return;
        }
        String shareText = Html.fromHtml(mClassroom.about).toString();
        ShareHelper
                .builder()
                .init(this)
                .setTitle(mClassroom.title)
                .setText(shareText.length() > 20 ? shareText.substring(0, 20) : shareText)
                .setUrl(EdusohoApp.app.host + "/classroom/" + mClassroom.id)
                .setImageUrl(mClassroom.cover != null ? mClassroom.cover.large : "")
                .build()
                .share();
    }

    @Override
    protected void showThreadCreateView(String type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ThreadCreateActivity.TARGET_ID, mClassroomId);
        bundle.putString(ThreadCreateActivity.TARGET_TYPE, "classroom");
        bundle.putString(ThreadCreateActivity.TYPE, "question".equals(type) ? "question" : "discussion");
        bundle.putString(ThreadCreateActivity.THREAD_TYPE, "common");
        ((EdusohoApp) getApplication()).mEngine.runNormalPluginWithBundle("ThreadCreateActivity", this, bundle);
    }

    @Override
    public void showComplete(Classroom classroom) {
        mClassroom = classroom;
        setLoadStatus(View.GONE);
        refreshView();
        if (classroom != null) {
            saveClassRoomToCache(classroom);
        }
    }

    @Override
    public void refreshFragment(boolean isJoin) {
        mIsMemder = isJoin;
        refreshFragmentViews(isJoin);
        mAddLayout.setVisibility(isJoin ? View.GONE : View.VISIBLE);
//        mIvGrade.setVisibility(isJoin ? View.GONE : View.VISIBLE);
//        mShareView.setVisibility(isJoin ? View.VISIBLE : View.GONE);
        mTvInclass.setVisibility(isJoin ? View.VISIBLE : View.GONE);
        setBottomLayoutState(!isJoin);
        if (isJoin) {
            mHandler.sendEmptyMessageDelayed(TAB_PAGE, 300);
        }
    }

    @Override
    public void newFinish() {
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveStickyNessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.PAY_SUCCESS) {
            finish();
            EventBus.getDefault().removeStickyEvent(MessageEvent.PAY_SUCCESS);
        }
    }

    @Override
    public void goToConfirmOrderActivity(int classroomId) {
        if (classroomId != 0) {
            ConfirmClassOrderActivity.launch(this, classroomId);
        }
    }
}
