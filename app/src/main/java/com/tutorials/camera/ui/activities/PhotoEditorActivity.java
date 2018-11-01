package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tutorials.camera.R;
import com.tutorials.camera.adapters.EditingToolsAdapter;
import com.tutorials.camera.enums.ToolType;
import com.tutorials.camera.ui.dialogs.PropertiesBSFragment;
import com.tutorials.camera.ui.dialogs.TextEditorDialogFragment;

import java.io.File;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

public class PhotoEditorActivity extends AppCompatActivity implements OnPhotoEditorListener, EditingToolsAdapter.OnItemSelected, PropertiesBSFragment.Properties {
    private PhotoEditor photoEditor;
    private TextView mTxtCurrentTool;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private PropertiesBSFragment mPropertiesBSFragment;

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

        setContentView(R.layout.activity_photo_editor);

        String filePath = intent.getStringExtra("filePath");
        File file = new File(filePath);
        Uri imageUri = FileProvider.getUriForFile(this,"com.tutorials.camera",file);
        PhotoEditorView photoEditorView = findViewById(R.id.photoEditorView);
        photoEditorView.getSource().setImageURI(imageUri);

        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto);

        Typeface mEmiliTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmiliTypeFace)
                .build();

        photoEditor.setOnPhotoEditorListener(this);

        initViews();
    }

    private void initViews()
    {
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode)
    {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode)
            {
                photoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews)
    {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews)
    {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews)
    {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType)
    {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType)
    {

    }

    @Override
    public void onToolSelected(ToolType toolType)
    {
        switch (toolType)
        {
            case BRUSH:
                photoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            /*case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                photoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;*/
        }
    }

    @Override
    public void onColorChanged(int colorCode)
    {
        photoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity)
    {
        photoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize)
    {
        photoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }
}
