package com.edusoho.kuozhi.clean.module.main.find;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.AppChannel;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineActivityDetailActivity;
import com.edusoho.kuozhi.clean.module.main.study.project.ProjectPlanEnrollDetailActivity;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

class FindCardItemAdapter extends BaseAdapter {

    private static final int COURSE    = 0;
    private static final int LIVE      = 1;
    private static final int CLASSROOM = 2;
    private static final int ACTIVITY  = 3;
    private static final int PROJECT   = 4;
    private int    marginSzie;
    private String showStudentNumEnabled;

    private Context             mContext;
    private AppChannel          mAppChannel;
    private DisplayImageOptions mOptions;
    private DisplayImageOptions mClassRoomOptions;

    FindCardItemAdapter(Context context) {
        this(context, new AppChannel());
        marginSzie = AppUtil.dp2px(mContext, 8);
        showStudentNumEnabled = SharedPreferencesUtils.getInstance(mContext)
                .open(SharedPreferencesHelper.CourseSetting.XML_NAME)
                .getString(SharedPreferencesHelper.CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY);
    }

    private FindCardItemAdapter(Context context, AppChannel appChannel) {
        this.mContext = context;
        this.mAppChannel = appChannel;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_course).
                        showImageOnFail(R.drawable.default_course)
                .build();
        mClassRoomOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_classroom).
                        showImageOnFail(R.drawable.default_classroom)
                .build();
        marginSzie = AppUtil.dp2px(mContext, 8);
    }

    public void clear() {
        mAppChannel.data.clear();
    }

    public void setData(AppChannel appChannel) {
        mAppChannel = appChannel;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mAppChannel.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mAppChannel.type) {
            case "course":
                return COURSE;
            case "classroom":
                return CLASSROOM;
            case "live":
                return LIVE;
            case "offlineActivity":
                return ACTIVITY;
            case "projectPlan":
                return PROJECT;
        }
        return COURSE;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getCount() {
        return mAppChannel.data != null ? mAppChannel.data.size() : 0;
    }

    private View getViewByType(int position, ViewGroup parent) {
        int viewType = getItemViewType(position);
        View convertView;

        ViewHolder viewHolder = new ViewHolder();
        switch (viewType) {
            case CLASSROOM:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_classroom_layout, parent, false);
                break;
            case LIVE:
            case COURSE:
            default:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_layout, parent, false);
                viewHolder.discount = (TextView) convertView.findViewById(R.id.tv_discount);
        }
        viewHolder.findCardView = (LinearLayout) convertView.findViewById(R.id.llayout_find_card_layout);
        viewHolder.coverView = (ImageView) convertView.findViewById(R.id.card_cover);
        viewHolder.titleView = (TextView) convertView.findViewById(R.id.card_title);
        viewHolder.priceView = (TextView) convertView.findViewById(R.id.card_price);
        viewHolder.studentNumView = (TextView) convertView.findViewById(R.id.card_num);
        if (viewType == ACTIVITY || viewType == PROJECT) {
            viewHolder.activityCategory = (TextView) convertView.findViewById(R.id.tv_activity_category);
            viewHolder.activityNum = (TextView) convertView.findViewById(R.id.tv_activity_stu_num);
        }
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = getViewByType(position, parent);
            viewHolder = (ViewHolder) convertView.getTag();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.findCardView.getLayoutParams();

            if (position % 2 == 0) {
                lp.setMargins(0, marginSzie, marginSzie, marginSzie);
            } else {
                lp.setMargins(marginSzie, marginSzie, 0, marginSzie);
            }
            viewHolder.findCardView.setLayoutParams(lp);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppChannel.Discovery discovery = mAppChannel.data.get(position);
        DisplayImageOptions options = getItemViewType(position) == CLASSROOM ? mClassRoomOptions : mOptions;
        ImageLoader.getInstance().displayImage(discovery.cover.middle, viewHolder.coverView, options);
        viewHolder.titleView.setText(discovery.title);
        setDiscoveryCardClickListener(convertView, mAppChannel.type, discovery.id);
        if (getItemViewType(position) == CLASSROOM) {
            viewHolder.studentNumView.setText(discovery.studentNum + " " + mContext.getString(R.string.find_card_student_num));
        } else if (getItemViewType(position) == ACTIVITY || getItemViewType(position) == PROJECT) {
            if (getItemViewType(position) == PROJECT) {
                viewHolder.studentNumView.setText(String.format("报名时间：%s至%s",
                        AppUtil.timeStampToDate(discovery.enrollmentStartDate, "MM-dd"),
                        AppUtil.timeStampToDate(discovery.enrollmentEndDate, "MM-dd")));
            } else {
                viewHolder.studentNumView.setText(String.format("报名截止：%s", AppUtil.timeStampToDate(discovery.enrollmentEndDate, "MM-dd")));
            }
            viewHolder.activityCategory.setVisibility(View.VISIBLE);
            viewHolder.activityNum.setVisibility(View.VISIBLE);
            viewHolder.activityCategory.setText(discovery.categoryName);
            if (discovery.maxStudentNum == 0) {
                viewHolder.activityNum.setText(String.format("%d/不限", discovery.studentNum));
            } else {
                viewHolder.activityNum.setText(String.format("%d/%d", discovery.studentNum, discovery.maxStudentNum));
            }
        } else {
            viewHolder.studentNumView.setText(showStudentNumEnabled != null && "1".equals(showStudentNumEnabled) ?
                    discovery.studentNum + " " + mContext.getString(R.string.find_card_student_num) : "");
        }
        viewHolder.priceView.setVisibility(View.GONE);
        return convertView;
    }

    private void setDiscoveryCardClickListener(View view, String type, int id) {
        if (type.equals("classroom")) {
            view.setTag(R.id.card_cover, id);
            view.setOnClickListener(mViewOnClickListener);
        } else if (type.equals("offlineActivity")) {
            view.setTag(R.id.card_cover, id);
            view.setOnClickListener(mViewOnClickListener3);
        } else if (type.equals("projectPlan")) {
            view.setTag(R.id.card_cover, id);
            view.setOnClickListener(mViewOnClickListener4);
        } else {
            view.setTag(R.id.card_cover, id);
            view.setOnClickListener(mViewOnClickListener2);
        }
    }

    //fixme 课程专题
    private View.OnClickListener mViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = (int) v.getTag(R.id.card_cover);
            EdusohoApp.app.mEngine.runNormalPlugin("ClassroomActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.CLASSROOM_ID, id);
                }
            });
        }
    };

    //fixme 带视频：公共课程 + 部门课程
    private View.OnClickListener mViewOnClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = (int) v.getTag(R.id.card_cover);
            CourseUnLearnActivity.launch(mContext, id);
        }
    };

    //fixme 培训项目
    private View.OnClickListener mViewOnClickListener4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = (int) v.getTag(R.id.card_cover);
            ProjectPlanEnrollDetailActivity.launch(mContext, Integer.toString(id));
        }
    };

    private View.OnClickListener mViewOnClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = (int) v.getTag(R.id.card_cover);
            OfflineActivityDetailActivity.launch(mContext, Integer.toString(id));
        }
    };

    static class ViewHolder {
        LinearLayout findCardView;
        TextView     discount;
        ImageView    coverView;
        TextView     titleView;
        TextView     priceView;
        TextView     studentNumView;
        TextView     activityCategory;
        TextView     activityNum;
    }
}
