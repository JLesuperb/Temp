package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import com.tutorials.camera.R;

import java.io.File;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditorActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.getStringExtra("filePath")==null)
        {
            finish();
        }

        setContentView(R.layout.activity_photo_editor);
        String filePath = intent.getStringExtra("filePath");
        File file = new File(filePath);
        Uri imageUri = FileProvider.getUriForFile(this,"com.tutorials.camera",file);
        PhotoEditorView photoEditorView = findViewById(R.id.photoEditorView);
        photoEditorView.getSource().setImageURI(imageUri);

        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto);

        Typeface mEmiliTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmiliTypeFace)
                .build();
    }
}
