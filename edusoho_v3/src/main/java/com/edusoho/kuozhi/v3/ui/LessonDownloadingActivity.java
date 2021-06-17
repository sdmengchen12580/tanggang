package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.service.PPTDownloadService;
import com.edusoho.kuozhi.v3.broadcast.DownloadStatusReceiver;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.DownloadStatus;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LessonDownloadingActivity extends ActionBarBaseActivity {
    public static final String COURSE_PROJECT = "course_project";
    public static final String COURSE_ID      = "course_id";
    public static final String CHAPTER_TOP    = "前言";

    protected DownloadStatusReceiver mDownloadStatusReceiver;
    private   TextView               btnSelectAll;
    private   TextView               btnDownload;
    private   int                    mCourseId;
    private   ExpandableListView     mListView;
    private   Context                mContext;
    private   List<LessonItem>       mLessonList;
    private   CourseProject          mCourseProject;
    private   DownloadLessonAdapter  mAdapter;

    private List<LessonItem>       mGroupItems = new ArrayList<>();
    private List<List<LessonItem>> mChildItems = new ArrayList<>();

    public static void launch(Context context, int courseId, CourseProject courseProject) {
        Intent intent = new Intent(context, LessonDownloadingActivity.class);
        intent.putExtra(COURSE_ID, courseId);
        intent.putExtra(COURSE_PROJECT, courseProject);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_downloading);
        setBackMode(BACK, getResources().getString(R.string.lesson_cache));
        mContext = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDownloadStatusReceiver == null) {
            mDownloadStatusReceiver = new DownloadStatusReceiver(mStatusCallback);
            registerReceiver(mDownloadStatusReceiver, new IntentFilter(DownloadStatusReceiver.ACTION));
        }
    }

    private DownloadStatusReceiver.StatusCallback mStatusCallback = new DownloadStatusReceiver.StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            if (lessonId == 0) {
                return;
            }
            M3U8DbModel m3u8Model = M3U8Util.queryM3U8Model(
                    mContext, app.loginUser.id, lessonId, app.domain, M3U8Util.ALL);
            if (mAdapter != null) {
                mAdapter.updateDownloadSign(lessonId, m3u8Model);
            }
        }
    };

    private void getTasks() {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.setMessage(getString(R.string.loading));
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseItems(mCourseId, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<List<CourseItem>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadDialog.show();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        loadDialog.dismiss();
                    }

                    @Override
                    public void onNext(List<CourseItem> courseItems) {
                        List<LessonItem> items = new ArrayList<>();
                        for (CourseItem courseItem : courseItems) {
                            items.add(new LessonItem().parse(courseItem, mCourseId));
                        }
                        if (items.size() > 0) {
                            initDownLessons(items);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        loadDialog.dismiss();
                    }
                });
    }

    private void initDownLessons(List<LessonItem> lessonItems) {
        mLessonList = lessonItems;
        filterLesson(mLessonList);
        loadLocalLessonStatus(mLessonList);

        if (mLessonList != null) {
            initData();
            if (mGroupItems.size() < mChildItems.size()) {
                LessonItem group = new LessonItem();
                group.title = CHAPTER_TOP;
                mGroupItems.add(0, group);
            }
            mAdapter = new DownloadLessonAdapter(mGroupItems, mChildItems);
            mListView.setAdapter(mAdapter);
            for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                mListView.expandGroup(i);
            }
        }

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LessonItem lessonItem = mAdapter.getChild(groupPosition, childPosition);
                lessonItem.isSelected = !lessonItem.isSelected;
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (v.getTag() instanceof GroupPanel) {
                    GroupPanel gp = (GroupPanel) v.getTag();
                    if (parent.isGroupExpanded(groupPosition)) {
                        gp.ivIndicator.setText(getString(R.string.font_less));
                    } else {
                        gp.ivIndicator.setText(getString(R.string.font_more));
                    }
                }
                return false;
            }
        });
    }

    private void initView() {
        mListView = (ExpandableListView) findViewById(R.id.el_download);
        btnSelectAll = (TextView) findViewById(R.id.tv_select_all);
        btnSelectAll.setOnClickListener(mSelectAllClick);
        btnDownload = (TextView) findViewById(R.id.tv_download);
        btnDownload.setOnClickListener(mDownloadClick);

        Intent intent = getIntent();
        if (intent != null) {
            mCourseId = intent.getIntExtra(COURSE_ID, 0);
            mCourseProject = (CourseProject) intent.getSerializableExtra(COURSE_PROJECT);
        }

        getTasks();
    }

    private void initData() {
        //节
        int unitCount = 0;
        int chapterCount = 0;
        List<LessonItem> tempArray = new ArrayList<>();
        int size = mLessonList.size();
        for (int i = 0; i < size; i++) {
            LessonItem curLessonItem = mLessonList.get(i);
            LessonItem.ItemType itemType = LessonItem.ItemType.cover(curLessonItem.itemType);
            LessonItem.ItemType type = LessonItem.ItemType.cover(curLessonItem.type);
            if (itemType.equals(LessonItem.ItemType.CHAPTER) && type.equals(LessonItem.ItemType.CHAPTER)) {
                //章节
                curLessonItem.title = String.format("第%s章 %s", ++chapterCount, curLessonItem.title);
                //如果最后一个GroupItem没有云视频课程，则删除改GroupItem
                if (mGroupItems.size() != 0 && mChildItems.size() < mGroupItems.size()) {
                    mGroupItems.remove(mGroupItems.size() - 1);
                }
                mGroupItems.add(curLessonItem);
            } else {
                if (type.equals(LessonItem.ItemType.UNIT)) {
                    curLessonItem.title = String.format("第%s节 %s", ++unitCount, curLessonItem.title);
                }
                tempArray.add(curLessonItem);
                if (i == size - 1) {
                    mChildItems.add(tempArray);
                    break;
                }
                LessonItem nextLessonItem = mLessonList.get(i + 1);
                if (nextLessonItem.itemType.toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString())
                        && nextLessonItem.type.toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString())) {
                    mChildItems.add(tempArray);
                    tempArray = new ArrayList<>();
                }
            }
        }
        //遍历结束，检查最后一个GroupItem，如果没有云视频课程，则删除改GroupItem
        if (mGroupItems.size() != 0 && mChildItems.size() < mGroupItems.size()) {
            mGroupItems.remove(mGroupItems.size() - 1);
        }
    }

    private View.OnClickListener mDownloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getDeviceFreeSize() < (1024 * 1024 * 50)) {
                CommonUtil.longToast(mContext, "手机可用空间不足,不能缓存!");
                return;
            }

            if (!AppUtil.isWiFiConnect(mActivity) && mActivity.app.config.offlineType == 0) {
                PopupDialog popupDialog = PopupDialog.createMuilt(mActivity,
                        mActivity.getString(R.string.notification),
                        mActivity.getString(R.string.play_with_4g_info), new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    for (LessonItem item : mLessonList) {
                                        if (item.isSelected) {
                                            downloadLesson(item);
                                            item.isSelected = false;
                                        }
                                    }
                                }
                            }
                        });
                popupDialog.setOkText(mActivity.getString(R.string.yes));
                popupDialog.setCancelText(mActivity.getString(R.string.no));
                popupDialog.setCanceledOnTouchOutside(false);
                popupDialog.show();
            } else {
                if (mLessonList == null) {
                    return;
                }
                for (LessonItem item : mLessonList) {
                    if (item.isSelected) {
                        item.isSelected = false;
                        downloadLesson(item);
                    }
                }
            }
        }
    };

    private View.OnClickListener mSelectAllClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnSelectAll.getVisibility() == View.VISIBLE
                    && btnSelectAll.getText().equals(getString(R.string.select_all))) {
                btnSelectAll.setText(getString(R.string.select_all_cancel));
                for (List<LessonItem> itemList : mChildItems) {
                    for (LessonItem item : itemList) {
                        if (item.m3u8Model == null) {
                            item.isSelected = true;
                        }
                    }
                }
            } else if (btnSelectAll.getVisibility() == View.VISIBLE
                    && btnSelectAll.getText().equals(getString(R.string.select_all_cancel))) {
                btnSelectAll.setText(getString(R.string.select_all));
                for (List<LessonItem> itemList : mChildItems) {
                    for (LessonItem item : itemList) {
                        if (item.m3u8Model == null) {
                            item.isSelected = false;
                        }
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @SuppressWarnings("deprecation")
    private long getDeviceFreeSize() {
        long free = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcard = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(sdcard.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            free = blockSize * availableBlocks;
        }

        return free;
    }

    /**
     * 过滤非云平台视频
     */
    private void filterLesson(List<LessonItem> lessonItems) {
        Iterator<LessonItem> lessonItemIterator = lessonItems.iterator();
        while (lessonItemIterator.hasNext()) {
            LessonItem lessonItem = lessonItemIterator.next();
            CourseLessonType type = CourseLessonType.value(lessonItem.type);
            if (!(type == CourseLessonType.VIDEO && "published".equals(lessonItem.status) && "self".equals(lessonItem.mediaSource) && "cloud".equals(lessonItem.mediaStorage))
                    && !(type == CourseLessonType.PPT && "published".equals(lessonItem.status))
                    && !(type == CourseLessonType.LIVE && "videoGenerated".equals(lessonItem.replayStatus))) {
                //非章、非节，删除
                if (!lessonItem.itemType.toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString())) {
                    lessonItemIterator.remove();
                    continue;
                }
            }
            if (lessonItem.uploadFile != null && "local".equals(lessonItem.uploadFile.storage)) {
                lessonItemIterator.remove();
            }
        }
    }

    /**
     * 加载已存储视频状态
     */
    private void loadLocalLessonStatus(final List<LessonItem> lessonItems) {
        int index = 0;
        int[] ids = new int[lessonItems.size()];
        final StringBuilder idStr = new StringBuilder("(");
        for (LessonItem lessonItem : lessonItems) {
            ids[index++] = lessonItem.id;
            idStr.append("'lesson-").append(lessonItem.id).append("',");
        }
        if (idStr.length() > 1) {
            idStr.deleteCharAt(idStr.length() - 1);
        }
        idStr.append(")");
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);

        final SparseArray<M3U8DbModel> m3U8DbModels = M3U8Util.getM3U8ModelList(
                mContext, ids, app.loginUser.id, app.domain, M3U8Util.ALL);

        SqliteUtil.QueryParser<SparseArray<DownloadStatus>> queryParser;
        queryParser = new SqliteUtil.QueryParser<SparseArray<DownloadStatus>>() {
            @Override
            public SparseArray<DownloadStatus> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>() {
                        });
                for (LessonItem lessonItem : lessonItems) {
                    if (lessonItem.id == item.id) {
                        lessonItem.m3u8Model = m3U8DbModels.get(item.id);
                    }
                }
                return null;
            }
        };

        sqliteUtil.query(
                queryParser,
                "select * from data_cache where type=? and key in " + idStr.toString(),
                Const.CACHE_LESSON_TYPE
        );
    }

    /**
     * 下载视频
     */
    private void downloadLesson(final LessonItem lessonItem) {
        if (lessonItem == null) {
            CommonUtil.longToast(mContext, "获取的任务资源不存在!");
            return;
        }

        if (!"lesson".equals(lessonItem.itemType)) {
            return;
        }

        saveCache(
                mContext,
                Const.CACHE_LESSON_TYPE,
                "lesson-" + lessonItem.id,
                app.gson.toJson(lessonItem)
        );
        saveCourseCache(
                mContext,
                Const.CACHE_COURSE_TYPE,
                "course-" + mCourseProject.id,
                app.gson.toJson(new Course().parse(mCourseProject))
        );

        CourseLessonType type = CourseLessonType.value(lessonItem.type);
        if (type == CourseLessonType.VIDEO || (type == CourseLessonType.LIVE && lessonItem.replayStatus.equals("videoGenerated"))) {
            M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                    mContext, app.loginUser.id, lessonItem.id, app.domain, M3U8Util.ALL);
            if (m3U8DbModel != null) {
                return;
            }
            m3U8DbModel = M3U8Util.saveM3U8Model(
                    mContext, lessonItem.id, app.domain, app.loginUser.id);
            M3U8DownService.startDown(
                    mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
            mAdapter.updateDownloadSign(lessonItem.id, m3U8DbModel);
        } else if (type == CourseLessonType.PPT) {
            //写入
            M3U8DbModel m3U8DbModel = M3U8Util.saveM3U8Model(
                    mContext, lessonItem.id, app.domain, app.loginUser.id);
            PPTDownloadService.start(mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
            mAdapter.updateDownloadSign(lessonItem.id, m3U8DbModel);
        }
    }

    private void saveCache(Context context, String type, String key, String value) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(context);

        Object obj = sqliteUtil.queryForObj(
                new TypeToken<Object>() {
                },
                "where type=? and key=?",
                type,
                key
        );

        if (obj == null) {
            sqliteUtil.saveLocalCache(type, key, value);
        }
    }

    private void saveCourseCache(Context context, String type, String key, String value) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(context);

        Object obj = sqliteUtil.queryForObj(
                new TypeToken<Object>() {
                },
                "where type=? and key=?",
                type,
                key
        );

        if (obj == null || (value.contains("courseSetTitle") && ((LinkedTreeMap) obj).get("courseSetTitle") == null)) {
            sqliteUtil.saveLocalCache(type, key, value);
        }
    }

    public class DownloadLessonAdapter extends BaseExpandableListAdapter {
        private List<LessonItem>       mGroupItems;
        private List<List<LessonItem>> mChildItems;

        public DownloadLessonAdapter(List<LessonItem> groupItems, List<List<LessonItem>> childItems) {
            mGroupItems = groupItems;
            mChildItems = childItems;
        }

        public void updateDownloadSign(int lessonId, M3U8DbModel model) {
            for (List<LessonItem> itemList : mChildItems) {
                for (LessonItem lessonItem : itemList) {
                    if (lessonItem.id == lessonId) {
                        lessonItem.m3u8Model = model;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return mGroupItems.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildItems.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupItems.get(groupPosition);
        }

        @Override
        public LessonItem getChild(int groupPosition, int childPosition) {
            return mChildItems.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupPanel groupPanel;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_course_group, null);
                groupPanel = new GroupPanel(convertView);
                convertView.setTag(groupPanel);
            } else {
                groupPanel = (GroupPanel) convertView.getTag();
            }

            LessonItem item = mGroupItems.get(groupPosition);
            groupPanel.tvGroupTitle.setText(item.title);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildPanel childPanel;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lesson_child, null);
                childPanel = new ChildPanel(convertView);
                convertView.setTag(childPanel);
            } else {
                childPanel = (ChildPanel) convertView.getTag();
            }
            final LessonItem item = mChildItems.get(groupPosition).get(childPosition);
            if (LessonItem.ItemType.LESSON == LessonItem.ItemType.cover(item.itemType)) {
                childPanel.viewLessonUnitTitle.setVisibility(View.GONE);
                childPanel.viewLessonInfo.setVisibility(View.VISIBLE);
                childPanel.tvLessonTitle.setText(item.title);
                if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.FINISH) {
                    //已下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.download_finish));
                } else if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.UN_FINISH) {
                    //正在下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.downloading));
                } else if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.START) {
                    //等待下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.wait_download));
                } else if (item.m3u8Model == null) {
                    //未下载
                    childPanel.ivDownloadSelected.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setVisibility(View.GONE);
                }

                //是否选中
                if (item.isSelected) {
                    childPanel.ivDownloadSelected.setText(getResources().getString(R.string.font_download_select));
                    childPanel.ivDownloadSelected.setTextColor(getResources().getColor(R.color.primary_color));
                } else {
                    childPanel.ivDownloadSelected.setText(getResources().getString(R.string.font_download_unselect));
                    childPanel.ivDownloadSelected.setTextColor(getResources().getColor(R.color.secondary2_font_color));
                }
            } else {
                childPanel.viewLessonUnitTitle.setVisibility(View.VISIBLE);
                childPanel.tvUnitTitle.setText(item.title);
                childPanel.viewLessonInfo.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public static class GroupPanel {
        TextView        tvGroupTitle;
        EduSohoIconView ivIndicator;

        GroupPanel(View view) {
            tvGroupTitle = (TextView) view.findViewById(R.id.tv_group);
            ivIndicator = (EduSohoIconView) view.findViewById(R.id.iv_indicator);
        }
    }

    public static class ChildPanel {
        TextView           tvUnitTitle;
        TextView           tvLessonTitle;
        TextView           tvDownloadFinish;
        EduSohoNewIconView ivDownloadSelected;
        View               viewLessonInfo;
        View               viewLessonUnitTitle;

        ChildPanel(View view) {
            tvUnitTitle = (TextView) view.findViewById(R.id.tv_lesson_chapter);
            tvLessonTitle = (TextView) view.findViewById(R.id.tv_lesson_name);
            tvDownloadFinish = (TextView) view.findViewById(R.id.iv_download_finish);
            ivDownloadSelected = (EduSohoNewIconView) view.findViewById(R.id.iv_download_selected);
            viewLessonInfo = view.findViewById(R.id.rl_lesson_info);
            viewLessonUnitTitle = view.findViewById(R.id.rl_unit_title);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadStatusReceiver != null) {
            unregisterReceiver(mDownloadStatusReceiver);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(MessageEvent<Integer> messageEvent) {
        int downloadType = messageEvent.getType();
        M3U8DbModel m3u8Model = new M3U8DbModel();
        switch (downloadType) {
            case MessageEvent.PPT_DOWNLOAD_DOING:
                m3u8Model.finish = M3U8Util.UN_FINISH;
                mAdapter.updateDownloadSign(messageEvent.getMessageBody(), m3u8Model);
                break;
            case MessageEvent.PPT_DONWLOAD_FINISH:
                m3u8Model.finish = M3U8Util.FINISH;
                mAdapter.updateDownloadSign(messageEvent.getMessageBody(), m3u8Model);
                break;

        }
    }
}
