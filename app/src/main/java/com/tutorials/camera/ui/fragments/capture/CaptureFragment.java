package com.tutorials.camera.ui.fragments.capture;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.io.IOException;

public class CaptureFragment extends _BaseFragment implements SurfaceHolder.Callback, View.OnClickListener
{
    private static CaptureFragment _instance;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public static CaptureFragment getInstance()
    {
        if (_instance == null) {
            _instance = new CaptureFragment();
        }
        return _instance;
    }

    public CaptureFragment() {
        //Must be empty
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        /*if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture_capture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        AppCompatButton captureBtn = view.findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(this);
    }

    public void refreshCamera()
    {
        if (surfaceHolder.getSurface() == null)
        {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try
        {
            camera.stopPreview();
        }
        catch (Exception e)
        {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception ignored) {}
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        refreshCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
        camera = Camera.open();
        /*Camera.Parameters param = camera.getParameters();
        param.setPreviewSize(352, 288);*/
        /*camera.setParameters(param);*/
        /*camera.setDisplayOrientation();*/
        try
        {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        if(camera!=null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.captureBtn:
                if(camera!=null)
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] bytes, Camera camera) {
                            if (getView() != null && getActivity() != null)
                            {
                                final LinearLayoutCompat confirmContainer = getView().findViewById(R.id.confirmContainer);
                                confirmContainer.setVisibility(View.VISIBLE);

                                AppCompatButton okBtn = getView().findViewById(R.id.okBtn);
                                okBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        SCamera.getInstance().setBytes(bytes);
                                        refreshCamera();
                                        surfaceHolder.removeCallback(CaptureFragment.this);
                                        /*if (CaptureFragment.this.camera != null)
                                        {
                                            CaptureFragment.this.camera.stopPreview();
                                            CaptureFragment.this.camera.release();
                                            CaptureFragment.this.camera = null;
                                        }*/

                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FormFragment fragment = FormFragment.getInstance();
                                        fragmentManager
                                                .beginTransaction()
                                                .hide(CaptureFragment.this)
                                                .add(R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });

                                AppCompatButton cancelBtn = getView().findViewById(R.id.cancelBtn);
                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        refreshCamera();
                                        confirmContainer.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });
                break;
        }
    }
}
