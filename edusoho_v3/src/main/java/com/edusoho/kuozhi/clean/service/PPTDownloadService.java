package com.edusoho.kuozhi.clean.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.LessonApi;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PPTDownloadService extends Service {

    private static final String TASK_ID = "task_id";
    private static final String TITLE   = "title";

    private ExecutorService           mCacheThreadPool;
    private SparseArray<Notification> mNotificationList;

    public static void start(Context context, int lessonId, int courseId, String title) {
        Intent intent = new Intent();
        intent.putExtra(TASK_ID, lessonId);
        intent.putExtra(TITLE, title);
        intent.setClass(context, PPTDownloadService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCacheThreadPool = Executors.newFixedThreadPool(1);
        mNotificationList = new SparseArray<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int taskId = intent.getIntExtra(TASK_ID, 0);
            if (taskId != 0) {
                mCacheThreadPool.execute(new PPTUrlsRunnable(taskId, startId));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCacheThreadPool.shutdown();
        mCacheThreadPool = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class PPTUrlsRunnable implements Runnable {
        private int taskId;
        private int startId;

        public PPTUrlsRunnable(int taskId, int startId) {
            this.taskId = taskId;
            this.startId = startId;
        }

        @Override
        public void run() {
            HttpUtils.getInstance()
                    .baseOnApi()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(LessonApi.class)
                    .getLesson(taskId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new SubscriberProcessor<LessonItem>() {
                        @Override
                        public void onNext(LessonItem lessonItem) {
                            LinkedTreeMap<String, List<String>> content = (LinkedTreeMap<String, List<String>>) lessonItem.content;
                            List<String> pptUrls = content.get("resource");
                            EventBus.getDefault().post(new MessageEvent<>(lessonItem.id, MessageEvent.PPT_DOWNLOAD_DOING));
                            createNotification(lessonItem.courseId, taskId, lessonItem.title);
                            for (int i = 0; i < pptUrls.size(); i++) {
                                ImageLoader.getInstance().loadImageSync(pptUrls.get(i), EdusohoApp.app.mOptions);
                                updateNotification(taskId, lessonItem.title, i + 1, pptUrls.size());
                                if (i == pptUrls.size() - 1) {
                                    SqliteUtil sqliteUtil = SqliteUtil.getUtil(PPTDownloadService.this);
                                    ContentValues cv = new ContentValues();
                                    cv.put("finish", 1);
                                    cv.put("total_num", pptUrls.size());
                                    cv.put("download_num", pptUrls.size());
                                    cv.put("userId", EdusohoApp.app.loginUser.id);
                                    cv.put("lessonId", taskId);
                                    cv.put("host", EdusohoApp.app.domain);
                                    cv.put("play_list", GsonUtils.parseString(pptUrls));
                                    sqliteUtil.update("data_m3u8", cv, "userId = ? and lessonId = ?", new String[]{EdusohoApp.app.loginUser.id + "", taskId + ""});
                                    showCompleteNotification(taskId, lessonItem.title);
                                    EventBus.getDefault().post(new MessageEvent<>(lessonItem.id, MessageEvent.PPT_DONWLOAD_FINISH));
                                    stopSelf(startId);
                                }
                            }
                        }
                    });
        }
    }

    private void createNotification(int courseId, int taskId, String title) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent(this, DownloadManagerActivity.class);
        notificationIntent.putExtra(Const.COURSE_ID, courseId);
        notification.contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(taskId, notification);
        mNotificationList.put(taskId, notification);
    }

    public void updateNotification(int lessonId, String title, int download, int total) {
        Notification notification = mNotificationList.get(lessonId);
        if (mNotificationList == null) {
            return;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setProgressBar(R.id.notify_progress, total, download, false);
        float percent = (download / (float) total);
        remoteViews.setTextViewText(R.id.notify_percent, (int) (percent * 100) + "%");
        notification.contentView = remoteViews;
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(lessonId, notification);
    }

    public void showCompleteNotification(int lessonId, String title) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.notification_download_icon,
                "下载完成 " + title, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.tickerText = "下载完成";

        Intent notificationIntent = new Intent();
        notification.contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setViewVisibility(R.id.notify_progress, View.GONE);
        remoteViews.setViewVisibility(R.id.notify_finish, View.VISIBLE);
        remoteViews.setTextViewText(R.id.notify_finish, "下载完成");
        remoteViews.setTextViewText(R.id.notify_percent, "100%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.cancel(lessonId);
        notificationManager.notify(lessonId, notification);
        mNotificationList.remove(lessonId);
    }
}
