package com.edusoho.kuozhi.clean.module.course.tasks.webvideo;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;

import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.widget.ESTaskFinishButton;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

public class WebVideoLessonFragment extends BaseFragment {

    public static final  int    HIDE                             = 1;
    public static final  int    FULL_SCREEN                      = 2;
    public static final  int    CHECK_YOUKU_SCREEN               = 3;
    public static final  String URL                              = "url";
    public static final  String COURSE_TASK                      = "course_task";
    public static final  String IS_MEMBER                        = "is_member";
    public static final  String COURSE_PROJECT                   = "course_project";
    private static final String INJECT_YOUKU_FULLSCREEN_CLICK_JS = "javascript:var divs = document.getElementsByTagName('body');" +
            "for(var i=0; i < divs.length; i++){" +
            "if (divs[i].className == 'x-zoomin'){" +
            "window.obj.addFullScreenEvent();" +
            "divs[i].addEventListener('click', function(event){window.obj.toggleFullScreen(), false});}}";
    private static final String TAG                              = "WebVideoLessonFragment";

    private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";

    private Toolbar                            mToolbar;
    private WebView mWebView;
    private TextView mTitle;
    private ESTaskFinishButton                 mFinishTask;
    private View                               mBack;
    private WebVideoWebChromeClient            mWebVideoWebChromeClient;
    private IX5WebChromeClient.CustomViewCallback mCustomViewCallback;

    private String        mUrl;
    private View          mView;
    private CourseTask    mCourseTask;
    private CourseProject mCourseProject;
    private boolean       mIsMember;

    public static WebVideoLessonFragment newInstance(String url, CourseTask courseTask, CourseProject courseProject, boolean isMember) {
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putSerializable(COURSE_TASK, courseTask);
        args.putBoolean(IS_MEMBER, isMember);
        args.putSerializable(COURSE_PROJECT, courseProject);
        WebVideoLessonFragment fragment = new WebVideoLessonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webViewStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webvideo_fragment, container, false);
        mWebView = (WebView) view.findViewById(R.id.webvideo_webview);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mTitle = (TextView) view.findViewById(R.id.tv_toolbar_title);
        mFinishTask = (ESTaskFinishButton) view.findViewById(R.id.tv_finish_task);
        mBack = view.findViewById(R.id.iv_back);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        initWebViewSetting();
        Bundle bundle = getArguments();
        mUrl = bundle.getString(URL);
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mIsMember = bundle.getBoolean(IS_MEMBER);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        mWebView.loadUrl(mUrl);
        Message message = workHandler.obtainMessage(CHECK_YOUKU_SCREEN);
        workHandler.sendMessageDelayed(message, 1000);

        mTitle.setText(mCourseTask.title);
        if (!mIsMember) {
            mFinishTask.setVisibility(View.INVISIBLE);
        } else if (mCourseTask != null && mCourseProject != null) {
            if (!mCourseTask.isFinish()) {
                final TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                        .setCourseId(mCourseProject.id)
                        .setCourseTask(mCourseTask)
                        .setEnableFinish(mCourseProject.enableFinish);

                final TaskFinishHelper mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                        .setActionListener(new TaskFinishActionHelper() {
                            @Override
                            public void onFinish(TaskEvent taskEvent) {
                                EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                                mCourseTask.result = taskEvent.result;
                                mFinishTask.setFinish(true);
                                if (mCourseProject.enableFinish == 0)
                                    TaskFinishDialog
                                            .newInstance(taskEvent, mCourseTask, mCourseProject)
                                            .show(getActivity().getSupportFragmentManager(), "TaskFinishDialog");
                            }
                        });

                mFinishTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIsMember && !mCourseTask.isFinish()) {
                            mTaskFinishHelper.stickyFinish();
                        }
                    }
                });
            } else {
                mFinishTask.setFinish(true);
            }
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initWebViewSetting() {
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setAllowFileAccess(true);
        mWebView.addJavascriptInterface(new JavaScriptObj(), "obj");

        mWebVideoWebChromeClient = new WebVideoWebChromeClient();
        mWebView.setWebChromeClient(mWebVideoWebChromeClient);
        mWebView.setWebViewClient(new WebVideoWebViewClient());
    }

    private void webViewStop() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int i) {
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        );

        Log.i(null, "WebVideoActivity webview stop");
        mWebView.onPause();
    }

    private Handler workHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_YOUKU_SCREEN:
                    mWebView.loadUrl(INJECT_YOUKU_FULLSCREEN_CLICK_JS);
                    break;
                case HIDE:
                    normalScreen();
                    break;
                case FULL_SCREEN:
                    fullScreen();
                    break;
            }
        }
    };

    public class JavaScriptObj {
        @JavascriptInterface
        public void show(String html) {
            Log.i(null, "html->" + html);
        }

        @JavascriptInterface
        public void addFullScreenEvent() {
            //isAddFullScreenEvent = true;
        }

        @JavascriptInterface
        public void toggleFullScreen() {
//            if (isFullScreen) {
//                workHandler.obtainMessage(HIDE).sendToTarget();
//                return;
//            }
            workHandler.obtainMessage(FULL_SCREEN).sendToTarget();
        }
    }

    private void fullScreen() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mToolbar.setVisibility(View.GONE);
    }

    private void normalScreen() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mToolbar.setVisibility(View.VISIBLE);
        if (mView == null) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) mView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mView);
            viewGroup.addView(mWebView);
        }

        if (mCustomViewCallback != null) {
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;
        }

        mView = null;
    }

    private class WebVideoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            if (url.endsWith("javascript:;")) {
                return;
            }
            super.onPageFinished(view, url);
        }
    }

    private class WebVideoWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, IX5WebChromeClient.CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
                return;
            }

            fullScreen();
            ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            viewGroup.removeView(mWebView);
            view.setBackgroundColor(Color.BLACK);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewGroup.addView(view);

            mView = view;
            mCustomViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            normalScreen();
        }
    }
}
