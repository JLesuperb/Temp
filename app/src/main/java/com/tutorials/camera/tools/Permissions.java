package com.tutorials.camera.tools;

import android.content.Context;
import android.content.pm.PackageManager;

public class Permissions
{
    public static boolean checkCameraPermission(Context context)
    {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkReadingExternalPermission(Context context)
    {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkWritingExternalPermission(Context context)
    {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
