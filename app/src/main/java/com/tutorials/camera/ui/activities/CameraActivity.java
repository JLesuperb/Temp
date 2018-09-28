package com.tutorials.camera.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments.capture.CaptureFragment;

public class CameraActivity extends AppCompatActivity
{
    public CameraActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        CaptureFragment captureFragment = CaptureFragment.getInstance();
        captureFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, captureFragment).commit();
    }
}
