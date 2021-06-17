package com.edusoho.kuozhi.v3.http;

import android.util.Log;

public class LogUtils {

    public static boolean needLog= true;

    public static void logE(String tag, String content) {
        if(needLog){
            Log.e(tag, content);
        }
    }
}
