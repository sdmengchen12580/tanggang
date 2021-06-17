package com.edusoho.kuozhi.v3.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.appview.CourseConsultAction;
import com.edusoho.kuozhi.v3.plugin.appview.GenseeLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.LonginusLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.TalkFunLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.ThreadCreateAction;
import com.edusoho.kuozhi.v3.plugin.appview.ThreadDiscussAction;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.fragment.ChatSelectFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.MultipartRequest;
import com.edusoho.kuozhi.v3.util.OpenLoginUtil;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.util.annotations.JsAnnotation;
import com.edusoho.kuozhi.v3.util.volley.StringVolleyRequest;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupInputDialog;
import com.edusoho.kuozhi.v3.view.webview.ESWebChromeClient;
import com.edusoho.kuozhi.v3.view.webview.bridgeadapter.bridge.BaseBridgePlugin;
import com.edusoho.kuozhi.v3.view.webview.bridgeadapter.bridge.BridgeCallback;
import com.edusoho.kuozhi.v3.view.webview.bridgeadapter.bridge.BridgePluginContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class MenuClickPlugin extends BaseBridgePlugin<Activity> {

    @Override
    public String getName() {
        return "ESNativeCore";
    }

    @JsAnnotation
    public void sendNativeMessage(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        String type = args.getString(0);
        JSONObject data = args.getJSONObject(1);
        MessageEngine.getInstance().sendMsg(type, JsonObject2Bundle(data));
        EventBus.getDefault().postSticky(new MessageEvent<>(MessageEvent.PAY_SUCCESS));
    }

    @JsAnnotation
    public void redirect(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        JSONObject body = args.getJSONObject(0);
        final RedirectBody redirectBody = RedirectBody.createByJsonObj(body);
        CoreEngine.create(mContext).runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择");
                startIntent.putExtra(ChatSelectFragment.BODY, redirectBody);
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ChatSelectFragment");
            }
        });
    }

    @JsAnnotation
    public void openNativeCourseDetailPage(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        if (args != null && args.length() > 0) {
            String type = args.getString(1);
            if ("course".equals(type)) {
                CourseProjectActivity.launch(mContext, args.getInt(0));
            } else if ("courseSet".equals(type)) {
                CourseUnLearnActivity.launch(mContext, args.getInt(0));
            }
        }
    }

    @JsAnnotation
    public void openNativeClassroomDetailPage(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.CLASSROOM_ID, args.getInt(0));
        CoreEngine.create(mContext).runNormalPluginWithBundle("ClassroomActivity", mContext, bundle);
    }

    @JsAnnotation
    public void showInput(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        String title = args.getString(0);
        String content = args.getString(1);
        String type = args.getString(2);
        final PopupInputDialog dlg = PopupInputDialog.create(mActivity, title, content, type);
        dlg.setOkListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                callbackContext.success(dlg.getInputString());
            }
        });
        dlg.show();
    }

    @JsAnnotation
    public void uploadImage(final JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        String acceptType = args.getString(3);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(acceptType);

        BridgePluginContext.Callback callback = new BridgePluginContext.Callback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                if (result == null) {
                    return;
                }
                try {
                    String url = args.getString(0);
                    JSONObject heads = args.getJSONObject(1);
                    JSONObject params = args.getJSONObject(2);

                    Uri imageUri = ESWebChromeClient.compressImage(mActivity.getBaseContext(), result);
                    if (imageUri != null) {
                        String key = params.keys().next();
                        params.put(key, new File(imageUri.getPath()));
                        upload(url, heads, params, callbackContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mPluginContext.startActivityForResult(
                callback,
                Intent.createChooser(i, "File Browser"),
                ESWebChromeClient.FILECHOOSER_RESULTCODE);
    }

    private void upload(String url, JSONObject heads, JSONObject params, final BridgeCallback callbackContext) throws Exception {
        final RequestUrl requestUrl = new RequestUrl(url);
        Iterator<String> itor = heads.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.heads.put(key, heads.getString(key));
        }

        itor = params.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.muiltParams.put(key, params.get(key));
        }

        VolleySingleton volley = VolleySingleton.getInstance(mActivity.getBaseContext());
        volley.getRequestQueue();

        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        MultipartRequest request = new MultipartRequest(
                Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadDialog.dismiss();
                        JSONObject result = null;
                        try {
                            result = new JSONObject(response);
                        } catch (Exception e) {
                        }
                        callbackContext.success(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadDialog.dismiss();
                        CommonUtil.longToast(mActivity.getBaseContext(), "上传图片失败!");
                    }
                }
        );
        request.setTag(requestUrl.url);
        volley.addToRequestQueue(request);

        loadDialog.show();
    }

    @JsAnnotation
    public void openDrawer(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        String message = args.getString(0);
        if (message.equals("open")) {
            EdusohoApp.app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
                @Override
                public void success(Object obj) {
                    mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
                }
            });
        }
    }

    @JsAnnotation
    public void openPlatformLogin(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        String type = args.getString(0);
        final OpenLoginUtil openLoginUtil = OpenLoginUtil.getUtil(mContext, false);
        openLoginUtil.setLoginHandler(new NormalCallback<UserResult>() {
            @Override
            public void success(UserResult obj) {
                mPluginContext.getActivity().finish();
            }
        });
        openLoginUtil.login(type).then(new PromiseCallback<String[]>() {
            @Override
            public Promise invoke(String[] obj) {
                openLoginUtil.bindOpenUser(mActivity, obj);
                return null;
            }
        });
    }

    @JsAnnotation
    public void backWebView(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        MessageEngine.getInstance().sendMsgToTaget(WebViewActivity.BACK, null, mPluginContext.getActivity());
    }

    @JsAnnotation
    public void openWebView(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        final String strUrl = args.getString(0);
        CoreEngine.create(mContext).runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.WEB_URL, strUrl);
            }
        });
    }

    @JsAnnotation
    public void closeWebView(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        MessageEngine.getInstance().sendMsgToTaget(WebViewActivity.CLOSE, null, mPluginContext.getActivity());
    }

    @JsAnnotation
    public JSONObject getUserToken(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        JSONObject result = new JSONObject();
        User user = EdusohoApp.app.loginUser;
        if (user != null) {
            result.put("user", new JSONObject(new Gson().toJson(user)));
            result.put("token", EdusohoApp.app.token);
        }

        return result;
    }

    @JsAnnotation
    public void post(JSONArray args, final BridgeCallback callbackContext) throws JSONException {
        String url = args.getString(0);
        JSONObject heads = args.getJSONObject(1);
        JSONObject params = args.getJSONObject(2);

        final RequestUrl requestUrl = new RequestUrl(url);

        Iterator<String> itor = heads.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.heads.put(key, heads.getString(key));
        }

        itor = params.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.params.put(key, params.getString(key));
        }

        VolleySingleton volley = VolleySingleton.getInstance(mActivity.getBaseContext());
        volley.getRequestQueue();

        StringVolleyRequest request = new StringVolleyRequest(
                Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callbackContext.success(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackContext.error(error.getMessage());
            }
        });
        request.setTag(requestUrl.url);
        volley.addToRequestQueue(request);
    }

    @JsAnnotation
    public void saveUserToken(JSONArray args, BridgeCallback callbackContext) throws JSONException {

        EdusohoApp app = (EdusohoApp) mActivity.getApplication();
        UserResult userResult = new UserResult();
        userResult.token = args.length() > 1 ? args.getString(1) : "";
        userResult.user = app.parseJsonValue(args.getJSONObject(0).toString(), new TypeToken<User>() {
        });
        app.saveToken(userResult);
        app.sendMessage(Const.LOGIN_SUCCESS, null);
        Bundle bundle = new Bundle();
        bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
    }

    @JsAnnotation
    public void updateUser(JSONArray args, BridgeCallback callbackContext) throws JSONException {

        EdusohoApp app = (EdusohoApp) mActivity.getApplication();
        UserResult userResult = new UserResult();
        userResult.user = app.parseJsonValue(args.getJSONObject(0).toString(), new TypeToken<User>() {
        });
        userResult.token = app.token;
        app.saveToken(userResult);

        User user = userResult.user;
        if (user != null) {
            Role role = new Role();
            role.setRid(user.id);
            role.setNickname(user.nickname);
            role.setAvatar(user.getMediumAvatar());
            IMClient.getClient().getRoleManager().updateRole(role);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("id", userResult.user.id);
        app.sendMessage(Const.USER_UPDATE, bundle);
    }

    @JsAnnotation
    public void share(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        String url = args.getString(0);
        String title = args.getString(1);
        String about = args.getString(2);
        String pic = args.getString(3);

        ShareHelper.builder()
                .init(mActivity)
                .setTitle(title)
                .setText(about)
                .setUrl(url)
                .setImageUrl(pic)
                .build()
                .share();
    }

    @JsAnnotation
    public void pay(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        final String mTitle = args.getString(0);
        final String payUrl = args.getString(1);
        CoreEngine.create(mContext).runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                startIntent.putExtra(Const.ACTIONBAR_TITLE, mTitle);
                startIntent.putExtra("payurl", payUrl);
            }
        });
    }

    @JsAnnotation
    public void showKeyInput(JSONArray args, BridgeCallback callbackContext) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @JsAnnotation
    public void learnCourseLesson(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        final int courseId = args.getInt(0);
        final int lessonId = args.getInt(1);
        final int[] lessonArray = coverJsonArrayToIntArray(args.getJSONArray(2));
        EdusohoApp.app.mEngine.runNormalPlugin(
                LessonActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, courseId);
                        startIntent.putExtra(Const.LESSON_ID, lessonId);
                        startIntent.putExtra(LessonActivity.LESSON_IDS, lessonArray);
                    }
                }
        );
    }

    @JsAnnotation
    public void showImages(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        int index = args.getInt(0);
        JSONArray imageArray = args.getJSONArray(1);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        String[] imgPaths = new String[imageArray.length()];
        for (int i = 0; i < imageArray.length(); i++) {
            imgPaths[i] = imageArray.getString(i);
        }
        bundle.putStringArray("images", imgPaths);
        CoreEngine.create(mContext).runNormalPluginWithBundle("ViewPagerActivity", mActivity, bundle);
    }

    @JsAnnotation
    public void clearUserToken(JSONArray args, BridgeCallback callbackContext) throws JSONException {

        EdusohoApp app = (EdusohoApp) mActivity.getApplication();
        app.removeToken();
        app.sendMessage(Const.LOGOUT_SUCCESS, null);
    }

    @JsAnnotation
    public void showDownLesson(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        final int courseId = args.getInt(0);
        CoreEngine.create(mContext).runNormalPlugin(
                "LessonDownloadingActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, courseId);
                    }
                }
        );
    }

    @JsAnnotation
    public void startAppView(JSONArray args, BridgeCallback callbackContext) throws JSONException {

        final String name = args.getString(0);
        JSONObject data = args.getJSONObject(1);
        String type = args.getString(2);

        final Bundle bundle = new Bundle();
        Iterator<String> iterator = data.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = data.get(key);

            if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                bundle.putInt(key, ((Double) value).intValue());
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, ((Boolean) value).booleanValue());
            } else {
                bundle.putString(key, value.toString());
            }
        }
        if ("Fragment".equals(type)) {
            CoreEngine.create(mContext).runPluginWithFragmentByBundle(name + "Fragment", mActivity, bundle);
        } else if ("courseConsult".equals(name)) {
            new CourseConsultAction(mActivity).invoke(bundle);
        } else if ("threadDiscuss".equals(name)) {
            new ThreadDiscussAction(mActivity).invoke(bundle);
        } else if ("threadCreate".equals(name)) {
            new ThreadCreateAction(mActivity).invoke(bundle);
        } else if ("longinusLivePlayer".equals(name)) {
            new LonginusLivePlayerAction(mActivity).invoke(bundle);
        } else if ("genseeLivePlayer".equals(name)) {
            new GenseeLivePlayerAction(mActivity).invoke(bundle);
        } else if ("talkfunLivePlayer".equals(name)) {
            new TalkFunLivePlayerAction(mActivity).invoke(bundle);
        }
    }

    @JsAnnotation
    public JSONArray getSupportLiveClients(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        JSONArray result = new JSONArray();
        result.put("gensee");
        result.put("longinus");
        result.put("talkfun");
        return result;
    }

    @JsAnnotation
    public void showCourseSetting(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        final int chatRoomId = args.getInt(0);
        final String type = args.getString(1);
        if (type.equals("classroom")) {
            CoreEngine.create(mContext).runNormalPlugin(
                    "ClassroomDetailActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.FROM_ID, chatRoomId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, "班级详情");
                        }
                    }
            );
        } else {
            CoreEngine.create(mContext).runNormalPlugin(
                    "CourseDetailActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.FROM_ID, chatRoomId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, "课程详情");
                        }
                    }
            );
        }
    }

    @JsAnnotation
    public void originalLogin(JSONArray args, BridgeCallback callbackContext) throws JSONException {
        EdusohoApp app = (EdusohoApp) mActivity.getApplication();
        app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
            @Override
            public void success(Object obj) {
                mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
            }
        });
    }

    private int[] coverJsonArrayToIntArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            try {
                array[i] = jsonArray.getInt(i);
            } catch (Exception e) {
                array[i] = 0;
            }
        }

        return array;
    }

    private Bundle JsonObject2Bundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);

            if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                bundle.putInt(key, ((Double) value).intValue());
            } else {
                bundle.putString(key, value.toString());
            }
        }

        return bundle;
    }
}
