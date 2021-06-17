package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.tasks.ppt.PPTLessonFragment;
import com.edusoho.kuozhi.clean.utils.biz.CourseHelper;
import com.edusoho.kuozhi.clean.utils.view.LoadingDialog;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.adapter.LessonDownloadingAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.CourseLessonType;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.base.IDownloadFragmenntListener;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 15/6/22.
 */
public class DownloadedFragment extends BaseFragment implements IDownloadFragmenntListener {

    private int                      mCourseId;
    private ListView                 mListView;
    private View                     mToolsLayout;
    private TextView                 mSelectAllBtn;
    private View                     mDelBtn;
    private View                     mEmptyView;
    private LessonDownloadingAdapter mDownloadedAdapter;
    private DownloadManagerActivity  mActivityContainer;
    public static final String FINISH = "finish";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_downloaded);
        mCourseId = getArguments().getInt(Const.COURSE_ID, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSelected(boolean isSelected) {
        if (!isSelected) {
            mDownloadedAdapter.setSelectShow(false);
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    protected void initView(View view) {
        mEmptyView = view.findViewById(R.id.ll_downloading_empty);
        mToolsLayout = view.findViewById(R.id.download_tools_layout);
        mSelectAllBtn = (TextView) view.findViewById(R.id.tv_select_all);
        mDelBtn = view.findViewById(R.id.tv_delete);
        mListView = (ListView) view.findViewById(R.id.el_downloaded);
        mActivityContainer = (DownloadManagerActivity) getActivity();
        DownloadManagerActivity.LocalCourseModel finishModel = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);

        mDownloadedAdapter = new LessonDownloadingAdapter(mContext, finishModel.m3U8DbModels, finishModel.mLocalLessons.get(mCourseId),
                DownloadingAdapter.DownloadType.DOWNLOADED, R.layout.item_downloaded_manager_lesson_child);
        mListView.setAdapter(mDownloadedAdapter);
        mSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().equals("全选")) {
                    MobclickAgent.onEvent(mContext, "i_cache_seleceAll");
                    tv.setText("取消");
                    mDownloadedAdapter.isSelectAll(true);
                } else {
                    tv.setText("全选");
                    mDownloadedAdapter.isSelectAll(false);
                }
            }
        });

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityContainer != null) {
                    MobclickAgent.onEvent(mContext, "i_cache_edit_delete");
                    ArrayList<Integer> selectedList = mDownloadedAdapter.getSelectLessonId();
                    if (selectedList == null || selectedList.isEmpty()) {
                        return;
                    }
                    ESAlertDialog.newInstance(getString(R.string.info), getString(R.string.confirm_delete_cache), getString(R.string.confirm), getString(R.string.cancel))
                            .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                                @Override
                                public void onClick(DialogFragment dialog) {
                                    dialog.dismiss();
                                    delLocalLesson(mDownloadedAdapter.getSelectLessonId());
                                }
                            })
                            .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                                @Override
                                public void onClick(DialogFragment dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show(getFragmentManager(), "ESAlertDialog");
                }
            }
        });

        final LoadingDialog loadingDialog = LoadingDialog.newInstance();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mToolsLayout.getVisibility() == View.GONE) {
                    final LessonItem lessonItem = mDownloadedAdapter.getItem(position);
                    HttpUtils.getInstance()
                            .addTokenHeader(EdusohoApp.app.token)
                            .createApi(UserApi.class)
                            .getCourseMember(lessonItem.courseId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SubscriberProcessor<CourseMember>() {

                                @Override
                                public void onStart() {
                                    loadingDialog.show(getActivity().getSupportFragmentManager(), "tag");
                                }

                                @Override
                                public void onCompleted() {
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onError(String message) {
                                    loadingDialog.dismiss();
                                    watchCacheVideo(lessonItem);
                                }

                                @Override
                                public void onNext(final CourseMember courseMember) {
                                    if (courseMember.user == null) {
                                        showDialog(R.string.course_exit_and_rejoin_dialog, R.string.rejoin, new ESAlertDialog.DialogButtonClickListener() {
                                            @Override
                                            public void onClick(DialogFragment dialog) {
                                                dialog.dismiss();
                                                CourseProjectActivity.launch(getActivity(), mCourseId);
                                            }
                                        });
                                    } else {
                                        int messageResId;
                                        int positiveResId;
                                        ESAlertDialog.DialogButtonClickListener positiveClickListener;
                                        switch (courseMember.access.code) {
                                            case CourseHelper.MEMBER_VIP_EXPIRED:
                                                messageResId = R.string.course_member_vip_expired_dialog;
                                                positiveResId = R.string.vip_renewal_fee;
                                                positiveClickListener = new ESAlertDialog.DialogButtonClickListener() {
                                                    @Override
                                                    public void onClick(DialogFragment dialog) {
                                                        dialog.dismiss();
                                                        final String url = String.format(
                                                                Const.MOBILE_APP_URL,
                                                                EdusohoApp.app.schoolHost,
                                                                Const.VIP_LIST
                                                        );
                                                        CoreEngine.create(mActivity).runNormalPlugin("WebViewActivity"
                                                                , mContext, new PluginRunCallback() {
                                                                    @Override
                                                                    public void setIntentDate(Intent startIntent) {
                                                                        startIntent.putExtra(Const.WEB_URL, url);
                                                                    }
                                                                });
                                                    }
                                                };
                                                showDialog(messageResId, positiveResId, positiveClickListener);
                                                break;
                                            case CourseHelper.MEMBER_EXPIRED:
                                                messageResId = R.string.course_date_limit_and_rejoin;
                                                positiveResId = R.string.rejoin;
                                                positiveClickListener = new ESAlertDialog.DialogButtonClickListener() {
                                                    @Override
                                                    public void onClick(DialogFragment dialog) {
                                                        dialog.dismiss();
                                                        CourseProjectActivity.launch(getActivity(), mCourseId);
                                                    }
                                                };
                                                showDialog(messageResId, positiveResId, positiveClickListener);
                                                break;
                                            case CourseHelper.COURSE_EXPIRED:
                                                messageResId = R.string.course_date_limit;
                                                positiveResId = R.string.rejoin;
                                                positiveClickListener = new ESAlertDialog.DialogButtonClickListener() {
                                                    @Override
                                                    public void onClick(DialogFragment dialog) {
                                                        dialog.dismiss();
                                                        CourseProjectActivity.launch(getActivity(), mCourseId);
                                                    }
                                                };
                                                showDialog(messageResId, positiveResId, positiveClickListener);
                                                break;
                                            default:
                                                watchCacheVideo(lessonItem);
                                        }
                                    }
                                }
                            });
                } else {
                    mDownloadedAdapter.setItemDownloadStatus(position);
                }
            }
        });
        setEmptyState(mDownloadedAdapter.getCount() == 0);
    }

    private void showDialog(int messageResId, int positiveButtonResId,
                            ESAlertDialog.DialogButtonClickListener positiveClickListener) {
        ESAlertDialog.newInstance(null, getString(messageResId), getString(positiveButtonResId), getString(R.string.cancel))
                .setConfirmListener(positiveClickListener)
                .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                })
                .show(getFragmentManager(), "ESAlertDialog");
    }

    private void watchCacheVideo(final LessonItem lessonItem) {
        CourseLessonType type = CourseLessonType.value(lessonItem.type);
        switch (type) {
            case PPT:
                Bundle bundle = new Bundle();
                ArrayList<String> pptUrls = SqliteUtil.getUtil(getActivity()).getPPTUrls(lessonItem.id);
                bundle.putSerializable(PPTLessonFragment.TASK_TITLE, lessonItem.title);
                bundle.putStringArrayList(PPTLessonFragment.PPT_URLS, pptUrls);
                FragmentPageActivity.launch(getActivity(), PPTLessonFragment.class.getName(), bundle);
                break;
            case LIVE:
            case VIDEO:
                app.mEngine.runNormalPlugin(
                        LessonActivity.TAG, mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.COURSE_ID, lessonItem.courseId);
                                startIntent.putExtra(LessonActivity.FROM_CACHE, true);
                                startIntent.putExtra(Const.FREE, lessonItem.free);
                                startIntent.putExtra(Const.LESSON_ID, lessonItem.id);
                                startIntent.putExtra(Const.LESSON_TYPE, lessonItem.type);
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, lessonItem.title);
                            }
                        }
                );
                break;
        }
    }

    private void setEmptyState(boolean isEmpty) {
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void delLocalLesson(ArrayList<Integer> ids) {
        new CourseCacheHelper(getContext(), app.domain, app.loginUser.id).clearLocalCache(ids);
        DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
        mDownloadedAdapter.updateLocalData(model.mLocalLessons.get(mCourseId));
        setEmptyState(mDownloadedAdapter.getCount() == 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offline_menu_edit, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mDownloadedAdapter.isSelectedShow()) {
            showBtnLayout();
        } else {
            hideBtnLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.offline_menu_edit) {
            if ("编辑".equals(item.getTitle())) {
                item.setTitle("取消");
                mDownloadedAdapter.setSelectShow(true);
                showBtnLayout();
            } else if ("取消".equals(item.getTitle())) {
                item.setTitle("编辑");
                mDownloadedAdapter.setSelectShow(false);
                hideBtnLayout();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBtnLayout() {
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void hideBtnLayout() {
        mToolsLayout.setVisibility(View.GONE);
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(FINISH)
        };
        return messageTypes;
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        String type = message.type.type;
        if (FINISH.equals(type)) {
            if (mActivityContainer != null) {
                DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
                if (model.mLocalCourses.isEmpty()) {
                } else {
                    mDownloadedAdapter.updateLocalData(model.mLocalLessons.get(mCourseId));
                    setEmptyState(mDownloadedAdapter.getCount() == 0);
                }
            }
        }
    }
}

