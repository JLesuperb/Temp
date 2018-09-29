package com.tutorials.camera.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments._BaseFragment;
import com.tutorials.camera.ui.fragments.capture.CaptureFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity
{

    List<WeakReference<Fragment>> fragList = new ArrayList<>();

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

    @Override
    public void onAttachFragment (Fragment fragment)
    {
        fragList.add(new WeakReference<>(fragment));
    }

    @Override
    public void onBackPressed()
    {
        boolean handled = false;
        for(WeakReference<Fragment> ref : fragList)
        {
            Fragment f = ref.get();
            if(f != null)
            {
                if(f.isVisible())
                {
                    handled = ((_BaseFragment)f).onBackPressed();
                    if(handled)
                    {
                        break;
                    }
                }
            }
        }

        if(!handled)
        {
            //this.moveTaskToBack(true);
            super.onBackPressed();
        }
    }
}
