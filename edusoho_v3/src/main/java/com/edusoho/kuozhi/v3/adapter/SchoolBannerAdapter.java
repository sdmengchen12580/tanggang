package com.edusoho.kuozhi.v3.adapter;

/**
 * Created by su on 2016/2/19.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 14-8-10.
 */
public class SchoolBannerAdapter extends PagerAdapter {

    private Context            mContext;
    private List<SchoolBanner> mSchoolBanners;

    public SchoolBannerAdapter(Context context, List<SchoolBanner> schoolBanners) {
        mContext = context;
        mSchoolBanners = schoolBanners;
    }

    @Override
    public int getCount() {
        if (mSchoolBanners != null) {
            return mSchoolBanners.size();
        } else {
            return 0;
        }
    }

    public void setItems(List<SchoolBanner> schoolBanners) {
        this.mSchoolBanners.clear();
        this.mSchoolBanners = schoolBanners;
        notifyDataSetChanged();
    }

    public void wrapContent() {
        if (mSchoolBanners != null && mSchoolBanners.size() > 0) {
            SchoolBanner top = mSchoolBanners.get(0);
            SchoolBanner last = mSchoolBanners.get(mSchoolBanners.size() - 1);
            mSchoolBanners.add(mSchoolBanners.size(), top);
            mSchoolBanners.add(0, last);
        }
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        SchoolBanner banner = mSchoolBanners.get(position);
        ImageView photoView = new ImageView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if ("localRes".equals(banner.url)) {
            //photoView.setImageBitmap(cacheBitmap);
        } else {
            ImageLoader.getInstance().displayImage(mSchoolBanners.get(position).url, photoView);
        }

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final SchoolBanner banner = mSchoolBanners.get(position);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("index", String.format("第%d张轮播图", position));
                    map.put("type", banner.action);
                    MobclickAgent.onEvent(mContext, "find_topPoster", map);
                    if ("webview".equals(banner.action)) {
                        Pattern CLASSROOM_PAT = Pattern.compile("/classroom/(\\d+)", Pattern.DOTALL);
                        Matcher matcher = CLASSROOM_PAT.matcher(banner.params);
                        if (matcher.find()) {
                            if (matcher.groupCount() >= 1) {
                                try {
                                    final int classroomId = Integer.parseInt(matcher.group(1));
                                    CoreEngine.create(mContext).runNormalPlugin((AppUtil.isX3Version() ? "X3" : "") + "ClassroomActivity", mContext, new PluginRunCallback() {
                                        @Override
                                        public void setIntentDate(Intent startIntent) {
                                            startIntent.putExtra(Const.CLASSROOM_ID, classroomId);
                                        }
                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(completeWithHttp(banner.params));
                            intent.setData(content_url);
                            mContext.startActivity(intent);
                        }
                    } else if ("course".equals(banner.action)) {
                        int courseId = Integer.parseInt(banner.params);
                        CourseProjectActivity.launch(mContext, courseId);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return photoView;
    }

    private String completeWithHttp(String url) {
        if (url.startsWith("http://")) {
            return url;
        }
        return "http://" + url;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}