package com.edusoho.kuozhi.clean.module.classroom.catalog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.course.ICourseStateListener;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;

import java.util.List;

/**
 * Created by DF on 2017/05/22.
 */

public class ClassCatalogFragment extends BaseFragment<ClassCatalogContract.Presenter> implements ICourseStateListener, ClassCatalogContract.View {

    public static final String CLASSROOM_ID = "Classroom_id";

    private View                  mLoadView;
    private RecyclerView          mRvClass;
    private TextView              mLessonEmpytView;
    private ClassCatalogueAdapter mClassAdapter;
    private AlertDialog           mDialog;

    private boolean isJoin = false;
    private List<CourseProject> mCourseList;
    private int mClassRoomId = 0;
    private String mCode;

    public ClassCatalogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_class_catalog, container, false);
        mRvClass = (RecyclerView) view.findViewById(R.id.lv_catalog);
        mLoadView = view.findViewById(R.id.il_class_catalog_load);
        mLessonEmpytView = (TextView) view.findViewById(R.id.ll_course_catalog_empty);

        mClassRoomId = getArguments().getInt(CLASSROOM_ID, 0);
        mPresenter = new ClassCatalogPresenter(this, mClassRoomId);
        initView();
        initData();
        return view;
    }

    private void initData() {
        setLoadStatus(View.VISIBLE);
        mPresenter.subscribe();
    }

    private void saveCourseListToCache(List<CourseProject> courseProjects) {
        StringBuilder sb = new StringBuilder();
        for (CourseProject courseProject : courseProjects) {
            sb.append(courseProject.id).append(",");
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        sqliteUtil.saveLocalCache(
                Const.CACHE_CLASSROOM_COURSE_IDS_TYPE,
                String.format("classroom-%s", mClassRoomId),
                sb.toString()
        );
    }

    private void initView() {
        mClassAdapter = new ClassCatalogueAdapter(mCourseList, isJoin);
        mRvClass.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvClass.setAdapter(mClassAdapter);
        mClassAdapter.setOnItemClickListener(new ClassCatalogueAdapter.OnItemClickListener() {
            @Override
            public void click(int position) {
                if (mCode != null && !"success".equals(mCode)) {
                    ESAlertDialog.newInstance(null, setDialogMessage(mCode), getString(R.string.course_exit_confirm), getString(R.string.course_exit_cancel))
                            .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                                @Override
                                public void onClick(DialogFragment dialog) {
                                    confirmExit();
                                    dialog.dismiss();
                                }
                            })
                            .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                                @Override
                                public void onClick(DialogFragment dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show(getFragmentManager(), "ESAlertDialog");
                    return;
                }
                CourseProjectActivity.launch(getContext(), mCourseList.get(position).id);
            }
        });
    }

    private String setDialogMessage(String mCode) {
        String message;
        switch (mCode) {
            case "member.expired":
                message = "班级已到期，无法继续学习，是否重新加入";
                break;
            case "classroom.expired":
                message = "班级已到期，无法继续学习，是否退出";
                break;
            case "vip.member_expired":
                message = "会员已到期，无法继续学习，是否续费会员";
                break;
            default:
                message = "无法查看，请联系管理员";
                break;
        }
        return message;
    }

    private void confirmExit() {
        switch (mCode) {
            case "member.expired":
            case "classroom.expired":
                exit();
                break;
            case "vip.member_expired":
                buyVip();
                break;
        }
    }

    private void buyVip() {
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                "main#/viplist"
        );
        CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                , getContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    private void exit() {
        CourseUtil.deleteClassroom(mClassRoomId, new CourseUtil.CallBack() {
            @Override
            public void onSuccess(String response) {
                CommonUtil.shortToast(getContext(), "退出成功");
                initData();
                if (getActivity() != null) {
                    ((CourseStateCallback) getActivity()).refresh();
                }
            }

            @Override
            public void onError(String response) {
            }
        });
    }

    @Override
    public void reFreshView(boolean mJoin) {
        isJoin = mJoin;
        if (getActivity() != null) {
            initData();
        }
    }

    @Override
    public void setLoadStatus(int visibility) {
        if (mLoadView != null) {
            mLoadView.setVisibility(visibility);
        }
    }

    @Override
    public void setClassStatus(String code) {
        this.mCode = code;
    }

    @Override
    public void showComplete(List<CourseProject> courseProjects) {
        mCourseList = courseProjects;
        saveCourseListToCache(mCourseList);
        mClassAdapter.setData(courseProjects, isJoin);
    }

    @Override
    public void setLessonEmptyViewVisibility(int visibility) {
        mLessonEmpytView.setVisibility(visibility);
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }
}
