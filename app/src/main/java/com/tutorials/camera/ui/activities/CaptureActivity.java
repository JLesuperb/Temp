package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.tools.AppTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        View.OnLongClickListener
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BARCODE_CAPTURE = 2;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);

        findViewById(R.id.saveBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.imagePreview).setOnLongClickListener(this);

        dispatchTakePictureIntent();
        AppCompatEditText barCodeEdt = findViewById(R.id.barCodeEdt);
        barCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b)
                {
                    dispatchTakeBarCodeIntent();
                }
                view.clearFocus();
            }
        });
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File path = new File(SCamera.getInstance().getFolderName(".tmp"));
            if(path.exists() || path.mkdirs())
            {
                AppTools.clean(path);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path,"tmp")));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void dispatchTakeBarCodeIntent()
    {
        Intent takePictureIntent = new Intent(this,ZXingActivity.class);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_BARCODE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();
                if(extras!=null)
                {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                    imagePreview.setImageBitmap(bitmap);
                    if(imageBitmap!=null)
                        imageBitmap.recycle();
                    imageBitmap = bitmap;
                }
            }
            else
            {
                if(imageBitmap==null)
                    finish();
            }

        }
        else if (requestCode == REQUEST_BARCODE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            if(extras!=null)
            {
                String text = extras.getString("data");
                AppCompatEditText barCodeEdt = findViewById(R.id.barCodeEdt);
                barCodeEdt.setText(text);
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.saveBtn:
                saveData();
                break;
            case R.id.cancelBtn:
                cancelData();
                break;
        }
    }

    private void saveData()
    {
        SCamera app = SCamera.getInstance();
        File path = new File(app.getFolderName());
        {
            try
            {
                if(path.mkdirs() || path.exists())
                {
                    File file = new File(path,String.format("%s.jpg", AppTools.getUniqueString()));
                    FileOutputStream outStream = new FileOutputStream(file.getAbsolutePath());
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.close();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void cancelData()
    {

    }

    @Override
    public boolean onLongClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imagePreview:
                dispatchTakePictureIntent();
                return true;
        }
        return false;
    }
}
