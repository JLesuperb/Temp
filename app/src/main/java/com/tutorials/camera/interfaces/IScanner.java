package com.tutorials.camera.interfaces;

import android.net.Uri;

public interface IScanner
{
    void onBitmapSelect(Uri uri);

    void onScanFinish(Uri uri);
}
