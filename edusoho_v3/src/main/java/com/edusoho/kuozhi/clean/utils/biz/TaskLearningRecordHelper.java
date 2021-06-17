package com.edusoho.kuozhi.clean.utils.biz;


import android.content.Context;

import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;

import java.io.Serializable;

public class TaskLearningRecordHelper {

    public static void put(TaskLearningRecord key, int seek, Context context) {
        SharedPreferencesUtils.getInstance(context)
                .open(SharedPreferencesHelper.TaskLearningRecord.XML)
                .putInt(key.toString(), seek);
    }

    public static int get(TaskLearningRecord key, Context context) {
        return SharedPreferencesUtils.getInstance(context)
                .open(SharedPreferencesHelper.TaskLearningRecord.XML)
                .getInt(key.toString());
    }

    public static class TaskLearningRecord implements Serializable {

        public int userId;
        public int taskId;

        public TaskLearningRecord(int userId, int taskId) {
            this.userId = userId;
            this.taskId = taskId;
        }

        @Override
        public String toString() {
            return String.format("%d-%d", userId, taskId);
        }
    }
}
