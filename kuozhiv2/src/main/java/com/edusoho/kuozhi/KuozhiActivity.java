package com.edusoho.kuozhi;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.edusoho.kuozhi.v3.ui.StartActivity;
import com.edusoho.kuozhi.v3.util.AllowX509TrustManager;


public class KuozhiActivity extends StartActivity {

    protected final String[] permissionManifestCamera = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    protected final int PERMISSION_REQUEST_CAMEAR_CODE = 0x1;
    protected int mNoPermissionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AllowX509TrustManager.allowAllSSL();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void startAnim() {
        if (Build.VERSION.SDK_INT >= 23 && !permissionCheck(1)) {
            ActivityCompat.requestPermissions(KuozhiActivity.this, permissionManifestCamera, PERMISSION_REQUEST_CAMEAR_CODE);
        } else {
            startSplash();
        }
    }

    //检测是否有权限 1：camera
    protected boolean permissionCheck(int type) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        String permission;
        if (type == 1) {
            for (int i = 0; i < permissionManifestCamera.length; i++) {
                permission = permissionManifestCamera[i];
                mNoPermissionIndex = i;
                if (PermissionChecker.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = PackageManager.PERMISSION_DENIED;
                }
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (permsRequestCode) {
            case PERMISSION_REQUEST_CAMEAR_CODE:
                startSplash();
                return;
        }
    }
}
