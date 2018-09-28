package com.tutorials.camera.ui.activities;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AndroidSurfaceView extends AppCompatActivity implements SurfaceHolder.Callback
{

    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback jpegCallback;
    private Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_android);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback()
        {
            @SuppressLint({"DefaultLocale", "SdCardPath"})
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream;
                try {
                    outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();
                //refreshCamera();
            }
        };
    }

    public void captureImage(View v) {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        refreshCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
