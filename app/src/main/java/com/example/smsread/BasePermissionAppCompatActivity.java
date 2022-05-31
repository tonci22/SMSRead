package com.example.smsread;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

public class BasePermissionAppCompatActivity extends AppCompatActivity {

    private final static int REQUEST_ALL_PERMISSIONS = 3004;

    private final static String[] permissions = new String[]{
            Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET
    };

    private RequestPermissionAction onPermissionCallBack;

    protected boolean hasAllPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void getPermissions(RequestPermissionAction onPermissionCallBack) {
        this.onPermissionCallBack = onPermissionCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasAllPermissionsGranted()) {
                requestPermissions(permissions, REQUEST_ALL_PERMISSIONS);
            } else {
                onPermissionCallBack.permissionGranted();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                onPermissionCallBack.permissionDenied();
                return;
            }
        }
        onPermissionCallBack.permissionGranted();
    }

    public interface RequestPermissionAction {
        void permissionDenied();

        void permissionGranted();
    }
}
