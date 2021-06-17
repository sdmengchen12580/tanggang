package com.edusoho.kuozhi.clean.module.course.task.catalog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.TaskResult;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.List;

import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.AUDIO;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.LIVE;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.VIDEO;

/**
 * Created by JesseHuang on 2017/3/28.
 */

public class CourseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CourseItem>        mTaskItems;
    private CourseProject.LearnMode mLearnMode;
    private boolean                 mIsJoin;
    private CourseProject           mCourseProject;
    private Context                 mContext;
    private Integer mCurrentClickPosition = null;

    public CourseTaskAdapter(Context context, CourseProject courseProject, List<CourseItem> taskItems, boolean isJoin) {
        this.mCourseProject = courseProject;
        this.mTaskItems = taskItems;
        this.mContext = context;
        this.mLearnMode = CourseProject.LearnMode.getMode(courseProject.learnMode);
        this.mIsJoin = isJoin;
    }

    public void setData(List<CourseItem> taskItems) {
        this.mTaskItems = taskItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position > (mTaskItems.size() - 1)) {
            return CourseItemEnum.TASK.getIndex();
        }
        if (CourseItemEnum.CHAPTER.toString().equals(mTaskItems.get(position).type)) {
            return CourseItemEnum.CHAPTER.getIndex();
        } else if (CourseItemEnum.UNIT.toString().equals(mTaskItems.get(position).type)) {
            return CourseItemEnum.UNIT.getIndex();
        } else {
            return CourseItemEnum.TASK.getIndex();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CourseItemEnum.CHAPTER.getIndex()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task_chapter, parent, false);
            return new CourseTaskChapterViewHolder(view);
        } else if (viewType == CourseItemEnum.UNIT.getIndex()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task_unit, parent, false);
            return new CourseTaskUnitViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task, parent, false);
            return new CourseTaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseItem taskItem = mTaskItems.get(position);
        if (holder instanceof CourseTaskChapterViewHolder) {
            CourseTaskChapterViewHolder chapterHolder = (CourseTaskChapterViewHolder) holder;
            String chapterName = SharedPreferencesUtils.getInstance(mContext)
                    .open(SharedPreferencesHelper.CourseSetting.XML_NAME)
                    .getString(SharedPreferencesHelper.CourseSetting.CHAPTER_NAME_KEY);
            chapterHolder.chapterTitle.setText(String.format(mContext.getString(R.string.course_project_chapter)
                    , taskItem.number, chapterName, taskItem.title));
        } else if (holder instanceof CourseTaskUnitViewHolder) {
            CourseTaskUnitViewHolder unitHolder = (CourseTaskUnitViewHolder) holder;
            String partName = SharedPreferencesUtils.getInstance(mContext)
                    .open(SharedPreferencesHelper.CourseSetting.XML_NAME)
                    .getString(SharedPreferencesHelper.CourseSetting.PART_NAME_KEY);
            unitHolder.unitTitle.setText(String.format(mContext.getString(R.string.course_project_unit), taskItem.number, partName, taskItem.title));
        } else {
            CourseTaskViewHolder taskHolder = (CourseTaskViewHolder) holder;
            if (mIsJoin) {
                setTaskStatusIcon(taskHolder, mLearnMode, taskItem);
            } else {
                taskHolder.taskStatus.setImageResource(
                        mLearnMode == CourseProject.LearnMode.FREEMODE ? R.drawable.lesson_status : R.drawable.lesson_status_lock);
            }
            taskHolder.taskName.setText(String.format(mContext.getString(R.string.course_project_task_item_name), taskItem.toTaskItemSequence(), taskItem.title));

            TaskTypeEnum taskType = getTaskType(taskItem.task.type);
            if (taskType == VIDEO || taskType == AUDIO) {
                taskHolder.taskDuration.setText(TimeUtils.getSecond2Min(taskItem.task.length));
                taskHolder.taskDuration.setVisibility(View.VISIBLE);
            } else {
                taskHolder.taskDuration.setVisibility(View.INVISIBLE);
            }

            if (taskType == LIVE) {
                taskHolder.taskLiveStatus.setVisibility(View.VISIBLE);
                setLiveStatus(taskHolder, taskItem);
            } else {
                taskHolder.taskLiveStatus.setVisibility(View.GONE);
            }

            if (isShowTryLookable(taskItem.task)) {
                taskHolder.taskIsFreeOrTryLookable.setVisibility(View.VISIBLE);
                taskHolder.taskIsFreeOrTryLookable.setText(R.string.task_try_loock);
                taskHolder.taskIsFreeOrTryLookable.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
            } else if (taskItem.task.isFree == 1 && !mIsJoin) {
                taskHolder.taskIsFreeOrTryLookable.setVisibility(View.VISIBLE);
                taskHolder.taskIsFreeOrTryLookable.setText(R.string.task_free);
                taskHolder.taskIsFreeOrTryLookable.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            } else {
                taskHolder.taskIsFreeOrTryLookable.setVisibility(View.GONE);
            }

            taskHolder.taskType.setText(getTaskIconResId(taskItem.task.type));
            if (mCurrentClickPosition != null && position == mCurrentClickPosition) {
                taskHolder.taskType.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                taskHolder.taskName.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                taskHolder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            } else {
                taskHolder.taskType.setTextColor(mContext.getResources().getColor(R.color.secondary2_font_color));
                taskHolder.taskName.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
                taskHolder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            }
        }
    }

    private void setLiveStatus(CourseTaskViewHolder taskViewHolder, CourseItem item) {
        long currentTime = System.currentTimeMillis();
        long startTime = item.task.startTime * 1000;
        long endTime = item.task.endTime * 1000;
        if (currentTime <= startTime) {
            taskViewHolder.taskLiveStatus.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            taskViewHolder.taskLiveStatus.setText(TimeUtils.getTime(startTime));
        } else {
            if (currentTime > endTime) {
                if ("ungenerated".equals(item.task.activity.replayStatus)) {
                    taskViewHolder.taskLiveStatus.setText(R.string.live_state_finish);
                    taskViewHolder.taskLiveStatus.setTextColor(ContextCompat.getColor(mContext, R.color.secondary2_font_color));
                    taskViewHolder.taskLiveStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_state_finish));
                } else {
                    taskViewHolder.taskLiveStatus.setText(R.string.live_state_replay);
                    taskViewHolder.taskLiveStatus.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
                    taskViewHolder.taskLiveStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_state_replay));
                }
            } else {
                taskViewHolder.taskLiveStatus.setText(R.string.live_state_ing);
                taskViewHolder.taskLiveStatus.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                taskViewHolder.taskLiveStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_state_ing));
            }
        }
    }

    private void setTaskStatusIcon(CourseTaskViewHolder holder, CourseProject.LearnMode mode, CourseItem taskItem) {
        if (mode == CourseProject.LearnMode.FREEMODE) {
            setTaskResult(holder, taskItem.task);
        } else {
            if (taskItem.task.lock) {
                holder.taskStatus.setImageResource(R.drawable.lesson_status_lock);
            } else {
                setTaskResult(holder, taskItem.task);
            }
        }
    }

    private void setTaskResult(CourseTaskViewHolder holder, CourseTask courseTask) {
        if (courseTask.result == null) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status);
        } else if (TaskResultEnum.FINISH.toString().equals(courseTask.result.status)) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status_finish);
        } else if (TaskResultEnum.START.toString().equals(courseTask.result.status)) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status_learning);
        }
        //当前选中的
        if (mCurrentClickPosition != null && !courseTask.isFinish()
                && mTaskItems.get(mCurrentClickPosition).task.id == courseTask.id) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status_learning);
        }
    }

    void setCurrentClickItem(CourseTask courseTask) {
        for (int i = 0; i < mTaskItems.size(); i++) {
            if (mTaskItems.get(i).task != null && mTaskItems.get(i).task.id == courseTask.id) {
                mCurrentClickPosition = i;
                if (mIsJoin && mTaskItems.get(i).task.result == null) {
                    mTaskItems.get(i).task.result = new TaskResult();
                    mTaskItems.get(i).task.result.status = TaskResultEnum.START.toString();
                }
                break;
            }
        }
        notifyDataSetChanged();
    }

    private boolean isShowTryLookable(CourseTask task) {
        return !mIsJoin && getTaskType(task.type) == VIDEO && task.isFree == 0 && mCourseProject.tryLookable == 1
                && task.activity != null && "cloud".equals(task.activity.mediaStorage);
    }

    @Override
    public int getItemCount() {
        return mTaskItems.size();
    }

    private int getTaskIconResId(String type) {
        TaskTypeEnum icon = getTaskType(type);
        switch (icon) {
            case TEXT:
                return R.string.task_text;
            case VIDEO:
                return R.string.task_video;
            case AUDIO:
                return R.string.task_audio;
            case LIVE:
                return R.string.task_live;
            case DISCUSS:
                return R.string.task_discuss;
            case FLASH:
                return R.string.task_flash;
            case DOC:
                return R.string.task_doc;
            case PPT:
                return R.string.task_ppt;
            case TESTPAPER:
                return R.string.task_testpaper;
            case HOMEWORK:
                return R.string.task_homework;
            case EXERCISE:
                return R.string.task_exercise;
            case DOWNLOAD:
                return R.string.task_download;
            case QUESTIONNAIRE:
                return R.string.task_questionnaire;
            default:
                return R.string.task_download;
        }
    }

    private TaskTypeEnum getTaskType(String type) {
        return TaskTypeEnum.fromString(type);
    }

    public CourseItem getItem(int position) {
        return mTaskItems.get(position);
    }

    static class CourseTaskViewHolder extends RecyclerView.ViewHolder {
        ESIconView taskType;
        TextView   taskName;
        TextView   taskDuration;
        TextView   taskIsFreeOrTryLookable;
        ImageView  taskStatus;
        TextView   taskLiveStatus;

        CourseTaskViewHolder(View view) {
            super(view);
            taskType = (ESIconView) view.findViewById(R.id.ev_task_type);
            taskName = (TextView) view.findViewById(R.id.tv_task_name);
            taskDuration = (TextView) view.findViewById(R.id.tv_task_duration);
            taskIsFreeOrTryLookable = (TextView) view.findViewById(R.id.tv_task_is_free);
            taskStatus = (ImageView) view.findViewById(R.id.iv_task_status);
            taskLiveStatus = (TextView) view.findViewById(R.id.tv_live_status);
        }
    }

    static class CourseTaskUnitViewHolder extends RecyclerView.ViewHolder {
        TextView unitTitle;

        CourseTaskUnitViewHolder(View view) {
            super(view);
            unitTitle = (TextView) view.findViewById(R.id.tv_unit_title);
        }
    }

    static class CourseTaskChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;

        CourseTaskChapterViewHolder(View view) {
            super(view);
            chapterTitle = (TextView) view.findViewById(R.id.tv_chapter_title);
        }
    }
}
