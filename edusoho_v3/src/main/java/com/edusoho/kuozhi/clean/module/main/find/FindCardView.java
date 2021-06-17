package com.edusoho.kuozhi.clean.module.main.find;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.AppChannel;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineListActivity;
import com.edusoho.kuozhi.clean.module.main.study.project.ProjectPlanListActivity;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardView extends LinearLayout {

    private Context             mContext;
    private GridView            mGridView;
    private TextView            mTitleView;
    private View                mMore;
    private FindCardItemAdapter mAdapter;
    private SparseIntArray      mChildHeightArray;
    private int                 mChildId;
    private String              showStudentNumEnabled;

    public FindCardView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public FindCardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        initView();
        showStudentNumEnabled = SharedPreferencesUtils.getInstance(mContext)
                .open(SharedPreferencesHelper.CourseSetting.XML_NAME)
                .getString(SharedPreferencesHelper.CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY);
    }

    protected void initView() {
        setOrientation(LinearLayout.VERTICAL);
        int padding = AppUtil.dp2px(getContext(), 16);
        setPadding(padding, 0, padding, 0);
        setBackgroundColor(mContext.getResources().getColor(R.color.disabled0_hint_color));
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.view_find_card_head_layout, null);
        mTitleView = (TextView) headView.findViewById(R.id.card_title);
        mMore = headView.findViewById(R.id.llayout_more);
        addView(headView);

        mGridView = createGridView();
        addView(mGridView);
        mChildHeightArray = new SparseIntArray();
    }

    public void setDiscoveryCardEntity(AppChannel appChannel) {
        this.mChildId = appChannel.id;
        mTitleView.setText(appChannel.title);
        setData(appChannel);
    }

    public void setMoreClickListener(String orderType, String type, int categoryId) {
        final String url;
        switch (type) {
            case "live":
                url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.MOBILE_WEB_LIVE_COURSES, categoryId, orderType)
                );
                break;
            case "classroom":
                url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.MOBILE_WEB_CLASSROOMS, categoryId, orderType)
                );
                break;
            case "course":
            default:
                url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.MOBILE_WEB_COURSES, categoryId, orderType)
                );
        }

        if (type.equals("offlineActivity")) {
            mMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OfflineListActivity.launch(mContext);

                }
            });
        } else if (type.equals("projectPlan")) {
            mMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectPlanListActivity.launch(mContext);
                }
            });
        } else {
            mMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext, "find_moreButton");
                    EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.WEB_URL, url + (showStudentNumEnabled != null && "1".equals(showStudentNumEnabled) ?
                                    "&shouldShowStudentNum=1" : ""));
                        }
                    });
                }
            });
        }


    }

    protected GridView createGridView() {
        GridView gridView = new GridView(getContext());

        gridView.setColumnWidth(getContext().getResources().getDimensionPixelSize(R.dimen.card_grid_width));
        gridView.setNumColumns(2);
        gridView.setVerticalSpacing(0);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setHorizontalScrollBarEnabled(false);
        return gridView;
    }

    public void setData(AppChannel appChannel) {
        mAdapter.setData(appChannel);
        drawGridView();
    }

    private void drawGridView() {
        int totalHeight;
        int childHeight;
        totalHeight = mChildHeightArray.get(mChildId, 0);
        if (totalHeight == 0) {
            int count = mAdapter.getCount();
            count = count % 2 == 0 ? count / 2 : count / 2 + 1;

            View child = mAdapter.getView(0, null, mGridView);
            child.measure(0, 0);
            childHeight = child.getMeasuredHeight();
            totalHeight = childHeight * count;
            mChildHeightArray.put(mChildId, totalHeight);
        }

        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = totalHeight;
        mGridView.setLayoutParams(lp);
    }

    public void setAdapter(ListAdapter adapter) {
        mAdapter = (FindCardItemAdapter) adapter;
        mGridView.setAdapter(adapter);
    }

    public int getCardViewListSize() {
        return mAdapter.getCount();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mChildHeightArray.clear();
    }
}
