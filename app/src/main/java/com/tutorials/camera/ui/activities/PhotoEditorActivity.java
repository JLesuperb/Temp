package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PhotoEditorActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //Check if send image path
        if(intent.getStringExtra("filePath")==null)
        {
            finish();
        }
        initViews();
    }

    private void initViews()
    {

    }
}
