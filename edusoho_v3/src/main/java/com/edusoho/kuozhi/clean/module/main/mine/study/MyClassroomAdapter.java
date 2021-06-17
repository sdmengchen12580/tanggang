package com.edusoho.kuozhi.clean.module.main.mine.study;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.module.main.mine.MineFragment;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyClassroomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY     = 0;
    private static final int NOT_EMPTY = 1;
    private int             mCurrentDataStatus;
    private Context         mContext;
    private List<Classroom> mClassroomList;

    public MyClassroomAdapter(Context context) {
        this.mContext = context;
        mClassroomList = new ArrayList<>();
    }

    public void setClassrooms(List<Classroom> list) {
        mClassroomList = list;
        notifyDataSetChanged();
    }

    public void clear() {
        mClassroomList.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentDataStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_classroom, parent, false);
            return new MyStudyFragment.ClassroomViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
            return new MineFragment.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            MyStudyFragment.ClassroomViewHolder classroomViewHolder = (MyStudyFragment.ClassroomViewHolder) viewHolder;
            final Classroom classroom = mClassroomList.get(position);
            classroomViewHolder.tvTitle.setText(String.valueOf(classroom.title));
            ImageLoader.getInstance().displayImage(classroom.cover.large, classroomViewHolder.ivPic, EdusohoApp.app.mOptions);
            classroomViewHolder.rLayoutItem.setTag(classroom.id);
            classroomViewHolder.rLayoutItem.setOnClickListener(getClassroomViewClickListener());
            classroomViewHolder.tvMore.setTag(classroom);
            classroomViewHolder.tvMore.setOnClickListener(getMoreClickListener());
        }
    }

    @Override
    public int getItemCount() {
        if (mClassroomList != null && mClassroomList.size() != 0) {
            mCurrentDataStatus = NOT_EMPTY;
            return mClassroomList.size();
        }
        mCurrentDataStatus = EMPTY;
        return 1;
    }

    private View.OnClickListener getClassroomViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int classroomId = (int) v.getTag();
                CoreEngine.create(mContext).runNormalPlugin("ClassroomActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.CLASSROOM_ID, classroomId);
                    }
                });
            }
        };
    }

    private View.OnClickListener getMoreClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Classroom study = (Classroom) v.getTag();
                final Classroom classroom = (Classroom) v.getTag();
                final MoreDialog dialog = new MoreDialog(mContext);
                dialog.init("退出班级", new MoreDialog.MoreCallBack() {
                    @Override
                    public void onMoveClick(View v, final Dialog outerDialog) {
                        ESAlertDialog.newInstance("确认退出班级",
                                mContext.getString(R.string.delete_classroom),
                                mContext.getString(R.string.course_exit_confirm),
                                mContext.getString(R.string.course_exit_cancel))
                                .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                                    @Override
                                    public void onClick(final DialogFragment dialog) {
                                        CourseUtil.deleteClassroom(study.id, new CourseUtil.CallBack() {
                                            @Override
                                            public void onSuccess(String response) {
                                                CommonUtil.shortToast(mContext, "退出成功");
                                                mClassroomList.remove(classroom);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                                outerDialog.dismiss();
                                                clearClassRoomCoursesCache(study.id);
                                            }

                                            @Override
                                            public void onError(String response) {
                                                dialog.dismiss();
                                            }
                                        });
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
                        ShareHelper.builder()
                                .init(mContext)
                                .setTitle(classroom.title)
                                .setText(classroom.about)
                                .setUrl(EdusohoApp.app.host + "/classroom/" + classroom.id + "/introduction")
                                .setImageUrl(classroom.cover.large)
                                .build()
                                .share();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelClick(View v, Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
            }
        };
    }

    private void clearClassRoomCoursesCache(int classRoomId) {
        Cache cache = SqliteUtil.getUtil(mContext).query(
                "select * from data_cache where key=? and type=?",
                "classroom-" + classRoomId,
                Const.CACHE_CLASSROOM_COURSE_IDS_TYPE
        );
        if (cache != null && cache.get() != null) {
            int[] ids = splitIntArrayByString(cache.get());
            if (ids.length <= 0) {
                return;
            }

            clearCoursesCache(ids);
        }
    }

    private int[] splitIntArrayByString(String idsString) {
        List<Integer> ids = new ArrayList<>();
        String[] splitArray = idsString.split(",");
        for (String item : splitArray) {
            int id = AppUtil.parseInt(item);
            if (id > 0) {
                ids.add(id);
            }
        }
        int[] idArray = new int[ids.size()];
        for (int i = 0; i < idArray.length; i++) {
            idArray[i] = ids.get(i);
        }
        return idArray;
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private void clearCoursesCache(int... courseIds) {
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        new CourseCacheHelper(mContext, school.getDomain(), user.id).clearLocalCacheByCourseId(courseIds);
    }
}
