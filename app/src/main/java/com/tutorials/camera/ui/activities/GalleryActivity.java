package com.tutorials.camera.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments.gallery.GridFragment;

public class GalleryActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if(savedInstanceState!=null)
        {
            return;
        }
        GridFragment gridFragment = GridFragment.getInstance();
        gridFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, gridFragment).commit();
    }
}
