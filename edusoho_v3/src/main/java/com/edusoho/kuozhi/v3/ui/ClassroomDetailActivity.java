package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.ClassroomMember;
import com.edusoho.kuozhi.v3.model.bal.ClassroomMemberResult;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by JesseHuang on 15/10/27.
 */
public class ClassroomDetailActivity extends ChatItemBaseDetail {

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "获取班级信息失败");
            return;
        }
        mFromId = intent.getIntExtra(Const.FROM_ID, 0);
        mConvNo = intent.getStringExtra(CONV_NO);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));

        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.CLASSROOM_MEMBERS, mFromId), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ClassroomMemberResult memberResult = parseJsonValue(response, new TypeToken<ClassroomMemberResult>() {
                });
                int total;
                if (memberResult != null) {
                    total = memberResult.total;
                    tvMemberSum.setText(String.format("%s(%d)", getString(R.string.classroom_all_members), total));
                    if (memberResult.resources != null) {
                        MemberAvatarAdapter adapter = new MemberAvatarAdapter(Arrays.asList(memberResult.resources));
                        gvMemberAvatar.setAdapter(adapter);
                    }
                } else {
                    CommonUtil.longToast(mContext, "获取班级信息失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mContext, "获取班级信息失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_announcement) {
            app.mEngine.runNormalPlugin((AppUtil.isX3Version() ? "X3" : "") + "WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_ANNOUNCEMENT, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        } else if (v.getId() == R.id.rl_entry) {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.CLASSROOM_ID, mFromId);
            CoreEngine.create(mContext).runNormalPluginWithBundle((AppUtil.isX3Version() ? "X3" : "") + "ClassroomActivity", mContext, bundle);
        } else if (v.getId() == R.id.rl_clear_record) {
            ESAlertDialog.newInstance(getString(R.string.info), getString(R.string.is_delete_record), getString(R.string.delete_chat_history), getString(R.string.cancel))
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            clearHistory();
                            dialog.dismiss();
                        }
                    })
                    .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show(getSupportFragmentManager(), "ESAlertDialog");
        } else if (v.getId() == R.id.btn_del_and_quit) {
            ESAlertDialog.newInstance(null, getString(R.string.exit_classroom_delete_cache), getString(R.string.confirm), getString(R.string.cancel))
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            unLearnClassRoom();
                            dialog.dismiss();
                        }
                    })
                    .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show(getSupportFragmentManager(), "ESAlertDialog");
        }
    }

    private void clearHistory() {
        ConvEntity convEntity = IMClient.getClient().getConvManager()
                .getConvByTypeAndId(Destination.CLASSROOM, mFromId);
        if (convEntity == null) {
            return;
        }
        IMClient.getClient().getMessageManager().deleteByConvNo(convEntity.getConvNo());
        IMClient.getClient().getConvManager().clearLaterMsg(convEntity.getConvNo());
        MessageEngine.getInstance().sendMsgToTaget(
                ClassroomDiscussActivity.CLEAR, null, ClassroomDiscussActivity.class);
    }

    private void unLearnClassRoom() {
        RequestUrl requestUrl = app.bindUrl(Const.CLASSROOM_UNLEARN, true);
        Map<String, String> params = requestUrl.getParams();
        params.put("classRoomId", mFromId + "");
        params.put("targetType", "classroom");
        ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("true")) {
                    removeClassRoomConvEntity();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.FROM_ID, mFromId);
                    app.sendMsgToTarget(Const.REFRESH_LIST, bundle, NewsFragment.class);
                    app.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.SWITCH_NEWS_TAB, true);
                        }
                    });
                    clearClassRoomCoursesCache();
                } else {
                    CommonUtil.shortToast(mContext, "退出失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.shortToast(mContext, "退出失败");
            }
        });
    }

    private void clearClassRoomCoursesCache() {
        Cache cache = SqliteUtil.getUtil(getBaseContext()).query(
                "select * from data_cache where key=? and type=?",
                "classroom-" + mFromId,
                Const.CACHE_CLASSROOM_COURSE_IDS_TYPE
        );
        if (cache != null && cache.get() != null) {
            int[] ids = splitIntArrayByString(cache.get());
            if (ids.length <= 0) {
                return;
            }

            new CourseCacheHelper(getBaseContext(), app.domain, app.loginUser.id).clearLocalCacheByCourseId(ids);
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

    private void removeClassRoomConvEntity() {
        ConvEntity convEntity = IMClient.getClient().getConvManager()
                .getConvByTypeAndId(Destination.CLASSROOM, mFromId);
        if (convEntity == null) {
            return;
        }
        IMClient.getClient().getMessageManager().deleteByConvNo(convEntity.getConvNo());
        IMClient.getClient().getConvManager().deleteConvByTypeAndId(Destination.CLASSROOM, mFromId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.news_course_profile_menu, menu);
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            final LoadDialog loadDialog = LoadDialog.create(mContext);
            loadDialog.setTextVisible(View.GONE);
            loadDialog.show();

            new ClassRoomProvider(mContext).getClassRoom(mFromId)
                    .success(new NormalCallback<Classroom>() {
                        @Override
                        public void success(Classroom classroom) {
                            loadDialog.dismiss();
                            if (classroom == null) {
                                CommonUtil.longToast(mContext, "获取班级信息失败");
                                return;
                            }
                            ShareHelper.builder()
                                    .init(mContext)
                                    .setTitle(classroom.title)
                                    .setText(classroom.about)
                                    .setUrl(app.host + "/classroom/" + mFromId)
                                    .setImageUrl(classroom.middlePicture)
                                    .build()
                                    .share();
                        }
                    }).fail(new NormalCallback<VolleyError>() {
                @Override
                public void success(VolleyError obj) {
                    loadDialog.dismiss();
                    CommonUtil.longToast(mContext, "获取班级信息失败");
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public class MemberAvatarAdapter extends BaseAdapter {
        public  List<ClassroomMember> mList;
        private DisplayImageOptions   mOptions;

        public MemberAvatarAdapter(List<ClassroomMember> mList) {
            this.mList = mList;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                    showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        @Override
        public int getCount() {
            if (mList != null) {
                return mList.size() + 1;
            }
            return 1;
        }

        @Override
        public ClassroomMember getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_member_avatar, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getCount() - 1 != position) {
                final ClassroomMember member = mList.get(position);
                viewHolder.ivAvatar.setBackground(null);
                ImageLoader.getInstance().displayImage(member.user.avatar, viewHolder.ivAvatar, mOptions);
                viewHolder.tvMemberName.setText(member.user.nickname);
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin((AppUtil.isX3Version() ? "X3" : "") + "WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_PROFILE, member.user.id));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                    }
                });
            } else {
                viewHolder.ivAvatar.setBackgroundResource(R.drawable.group_member_more_bg);
                viewHolder.tvMemberName.setText("更多");
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin((AppUtil.isX3Version() ? "X3" : "") + "WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_MEMBER_LIST, mFromId));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                    }
                });
            }
            return convertView;
        }
    }
}
